package com.videostore.videostore.application.usecase.favourite;

import com.videostore.videostore.application.command.favourite.RemoveFavouriteCommand;
import com.videostore.videostore.application.port.in.favourite.RemoveFavouriteUseCase;
import com.videostore.videostore.domain.exception.notfound.FavouriteNotFoundException;
import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
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
        Favourite favourite = favouriteRepository.findByUserIdAndMovieId(new UserId(command.userId()), new MovieId(command.movieId()))
                .orElseThrow(() -> new FavouriteNotFoundException("The user cannot remove a movie from favourites if it is not marked as a favourites"));

        favouriteRepository.removeFavourite(favourite.getId());
    }
}
