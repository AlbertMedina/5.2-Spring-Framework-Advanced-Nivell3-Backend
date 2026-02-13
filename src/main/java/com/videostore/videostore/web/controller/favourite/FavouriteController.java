package com.videostore.videostore.web.controller.favourite;

import com.videostore.videostore.application.command.favourite.AddFavouriteCommand;
import com.videostore.videostore.application.command.favourite.RemoveFavouriteCommand;
import com.videostore.videostore.application.port.in.favourite.AddFavouriteUseCase;
import com.videostore.videostore.application.port.in.favourite.GetMyFavouritesUseCase;
import com.videostore.videostore.application.port.in.favourite.RemoveFavouriteUseCase;
import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.web.controller.favourite.dto.request.AddFavouriteRequest;
import com.videostore.videostore.web.controller.favourite.dto.response.FavouriteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@Tag(name = "Favourites", description = "Operations related to user favourite movies")
public class FavouriteController {

    private static final Logger log = LoggerFactory.getLogger(FavouriteController.class);

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

    @Operation(summary = "Add a movie to the authenticated user favourites")
    @PostMapping("/favourites")
    @CacheEvict(value = "favourites", key = "#authentication.name")
    public ResponseEntity<FavouriteResponse> addFavourite(@RequestBody @Valid AddFavouriteRequest request, Authentication authentication) {
        log.info("User {} requested to add movie {} to favourites", authentication.getName(), request.movieId());

        AddFavouriteCommand command = new AddFavouriteCommand(authentication.getName(), request.movieId());
        Favourite favourite = addFavouriteUseCase.execute(command);

        log.info("User {} successfully added movie {} to favourites", authentication.getName(), request.movieId());

        FavouriteResponse response = FavouriteResponse.fromDomain(favourite);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Remove a movie from the authenticated user favourites")
    @DeleteMapping("/favourites/{movieId}")
    @CacheEvict(value = "favourites", key = "#authentication.name")
    public ResponseEntity<Void> removeFavourite(@PathVariable @Positive Long movieId, Authentication authentication) {
        log.info("User {} requested to remove movie {} from favourites", authentication.getName(), movieId);

        RemoveFavouriteCommand command = new RemoveFavouriteCommand(authentication.getName(), movieId);
        removeFavouriteUseCase.execute(command);

        log.info("User {} successfully removed movie {} from favourites", authentication.getName(), movieId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all favourite movies for the authenticated user")
    @GetMapping("/me/favourites")
    @Cacheable(value = "favourites", key = "#authentication.name")
    public ResponseEntity<List<FavouriteResponse>> getMyFavourites(Authentication authentication) {
        log.info("User {} requested all their favourite movies", authentication.getName());

        List<FavouriteResponse> response = getMyFavouritesUseCase.execute(authentication.getName())
                .stream().map(FavouriteResponse::fromDomain).toList();

        log.info("User {} successfully retrieved {} favourite movies", authentication.getName(), response.size());

        return ResponseEntity.ok(response);
    }
}
