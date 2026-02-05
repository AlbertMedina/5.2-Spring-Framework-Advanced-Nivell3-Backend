package com.videostore.videostore.web.controller.favourite;

import com.videostore.videostore.application.command.favourite.AddFavouriteCommand;
import com.videostore.videostore.application.command.favourite.RemoveFavouriteCommand;
import com.videostore.videostore.application.port.in.favourite.AddFavouriteUseCase;
import com.videostore.videostore.application.port.in.favourite.GetMyFavouritesUseCase;
import com.videostore.videostore.application.port.in.favourite.RemoveFavouriteUseCase;
import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.web.controller.favourite.dto.request.AddFavouriteRequest;
import com.videostore.videostore.web.controller.favourite.dto.response.FavouriteResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
public class FavouriteController {

    private final AddFavouriteUseCase addFavouriteUseCase;
    private final RemoveFavouriteUseCase removeFavouriteUseCase;
    private final GetMyFavouritesUseCase getMyFavouritesUseCase;

    public FavouriteController(
            AddFavouriteUseCase addFavouriteUseCase,
            RemoveFavouriteUseCase removeFavouriteUseCase,
            GetMyFavouritesUseCase getMyFavouritesUseCase
    ) {
        this.addFavouriteUseCase = addFavouriteUseCase;
        this.removeFavouriteUseCase = removeFavouriteUseCase;
        this.getMyFavouritesUseCase = getMyFavouritesUseCase;
    }

    @PostMapping("/favourites")
    public ResponseEntity<FavouriteResponse> addFavourite(@RequestBody @Valid AddFavouriteRequest request, Authentication authentication) {
        AddFavouriteCommand command = new AddFavouriteCommand(authentication.getName(), request.movieId());
        Favourite favourite = addFavouriteUseCase.execute(command);

        FavouriteResponse response = FavouriteResponse.fromDomain(favourite);
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/favourites/{movieId}")
    public ResponseEntity<Void> removeFavourite(@PathVariable @Positive Long movieId, Authentication authentication) {
        RemoveFavouriteCommand command = new RemoveFavouriteCommand(authentication.getName(), movieId);
        removeFavouriteUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/favourites")
    public ResponseEntity<List<FavouriteResponse>> getFavouritesByUser(Authentication authentication) {
        List<FavouriteResponse> response = getMyFavouritesUseCase.execute(authentication.getName())
                .stream().map(FavouriteResponse::fromDomain).toList();

        return ResponseEntity.ok(response);
    }
}
