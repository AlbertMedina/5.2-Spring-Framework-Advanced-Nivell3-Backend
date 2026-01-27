package com.videostore.videostore.application.usecase.favourite;

import com.videostore.videostore.application.command.favourite.RemoveFavouriteCommand;
import com.videostore.videostore.domain.exception.FavouriteNotFound;
import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.domain.repository.FavouriteRepository;

public class RemoveFavouriteUseCase {

    private final FavouriteRepository favouriteRepository;

    public RemoveFavouriteUseCase(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
    }

    public void execute(RemoveFavouriteCommand command) {
        Favourite favourite = favouriteRepository.findByUserIdAndMovieId(command.userId(), command.movieId())
                .orElseThrow(() -> new FavouriteNotFound("The user cannot remove a movie from favourites if it is not marked as a favourites"));

        favouriteRepository.removeFavourite(favourite);
    }
}
