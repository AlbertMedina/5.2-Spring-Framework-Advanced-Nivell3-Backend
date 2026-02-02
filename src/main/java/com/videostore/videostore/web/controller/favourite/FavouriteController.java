package com.videostore.videostore.web.controller.favourite;

import com.videostore.videostore.application.command.favourite.AddFavouriteCommand;
import com.videostore.videostore.application.command.favourite.RemoveFavouriteCommand;
import com.videostore.videostore.application.port.in.favourite.AddFavouriteUseCase;
import com.videostore.videostore.application.port.in.favourite.RemoveFavouriteUseCase;
import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.web.controller.favourite.dto.request.AddFavouriteRequest;
import com.videostore.videostore.web.controller.favourite.dto.response.FavouriteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FavouriteController {

    private final AddFavouriteUseCase addFavouriteUseCase;
    private final RemoveFavouriteUseCase removeFavouriteUseCase;

    public FavouriteController(AddFavouriteUseCase addFavouriteUseCase, RemoveFavouriteUseCase removeFavouriteUseCase) {
        this.addFavouriteUseCase = addFavouriteUseCase;
        this.removeFavouriteUseCase = removeFavouriteUseCase;
    }

    @PostMapping("/favourites")
    public ResponseEntity<FavouriteResponse> addFavourite(@RequestBody AddFavouriteRequest request) {
        AddFavouriteCommand command = new AddFavouriteCommand(request.userId(), request.movieId());
        Favourite favourite = addFavouriteUseCase.execute(command);

        FavouriteResponse response = FavouriteResponse.fromDomain(favourite);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/favourites")
    public ResponseEntity<Void> removeFavourite(@RequestParam Long userId, @RequestParam Long movieId) {
        RemoveFavouriteCommand command = new RemoveFavouriteCommand(userId, movieId);
        removeFavouriteUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }
}
