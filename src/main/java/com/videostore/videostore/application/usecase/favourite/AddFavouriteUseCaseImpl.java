package com.videostore.videostore.application.usecase.favourite;

import com.videostore.videostore.application.command.favourite.AddFavouriteCommand;
import com.videostore.videostore.application.port.in.favourite.AddFavouriteUseCase;
import com.videostore.videostore.domain.exception.*;
import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.domain.model.favourite.valueobject.FavouriteDate;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.repository.FavouriteRepository;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AddFavouriteUseCaseImpl implements AddFavouriteUseCase {

    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public AddFavouriteUseCaseImpl(FavouriteRepository favouriteRepository, MovieRepository movieRepository, UserRepository userRepository) {
        this.favouriteRepository = favouriteRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional
    public Favourite execute(AddFavouriteCommand addFavouriteCommand) {
        Long userId = addFavouriteCommand.userId();
        Long movieId = addFavouriteCommand.movieId();

        User user = userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));

        Movie movie = movieRepository.findById(new MovieId(movieId))
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        validateFavourite(userId, movieId);

        Favourite favourite = Favourite.create(
                user.getId(),
                movie.getId(),
                new FavouriteDate(LocalDate.now())
        );

        return favouriteRepository.addFavourite(favourite);
    }

    private void validateFavourite(Long userId, Long movieId) {
        if (favouriteRepository.existsByUserIdAndMovieId(new UserId(userId), new MovieId(movieId))) {
            throw new FavouriteAlreadyExistingException(userId, movieId);
        }
    }
}
