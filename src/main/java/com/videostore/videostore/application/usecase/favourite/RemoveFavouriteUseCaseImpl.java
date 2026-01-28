package com.videostore.videostore.application.usecase.favourite;

import com.videostore.videostore.application.command.favourite.RemoveFavouriteCommand;
import com.videostore.videostore.application.port.in.favourite.RemoveFavouriteUseCase;
import com.videostore.videostore.domain.exception.FavouriteNotFound;
import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.domain.repository.FavouriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveFavouriteUseCaseImpl implements RemoveFavouriteUseCase {

    private final FavouriteRepository favouriteRepository;

    public RemoveFavouriteUseCaseImpl(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
    }

    @Override
    @Transactional
    public void execute(RemoveFavouriteCommand command) {
        Favourite favourite = favouriteRepository.findByUserIdAndMovieId(command.userId(), command.movieId())
                .orElseThrow(() -> new FavouriteNotFound("The user cannot remove a movie from favourites if it is not marked as a favourites"));

        favouriteRepository.removeFavourite(favourite);
    }
}
