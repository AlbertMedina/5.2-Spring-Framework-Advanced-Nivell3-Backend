package com.videostore.videostore.application.usecase.favourite;

import com.videostore.videostore.application.command.favourite.AddFavouriteCommand;
import com.videostore.videostore.domain.exception.*;
import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.domain.model.favourite.valueobject.FavouriteDate;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.repository.FavouriteRepository;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.UserRepository;

import java.time.LocalDate;

public class AddFavouriteUseCase {

    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public AddFavouriteUseCase(FavouriteRepository favouriteRepository, MovieRepository movieRepository, UserRepository userRepository) {
        this.favouriteRepository = favouriteRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    public Favourite execute(AddFavouriteCommand addFavouriteCommand) {
        Long userId = addFavouriteCommand.userId();
        Long movieId = addFavouriteCommand.movieId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        validateFavourite(userId, movieId);

        return favouriteRepository.addFavourite(new Favourite(user, movie, new FavouriteDate(LocalDate.now())));
    }

    private void validateFavourite(Long userId, Long movieId) {
        if (favouriteRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new FavouriteAlreadyExistingException(userId, movieId);
        }
    }
}
