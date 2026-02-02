package com.videostore.videostore.application.usecase.favourite;

import com.videostore.videostore.application.command.favourite.AddFavouriteCommand;
import com.videostore.videostore.application.port.in.favourite.AddFavouriteUseCase;
import com.videostore.videostore.domain.exception.conflict.FavouriteAlreadyExistsException;
import com.videostore.videostore.domain.exception.notfound.MovieNotFoundException;
import com.videostore.videostore.domain.exception.notfound.UserNotFoundException;
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
        UserId userId = new UserId(addFavouriteCommand.userId());
        MovieId movieId = new MovieId(addFavouriteCommand.movieId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.value()));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId.value()));

        validateFavourite(userId, movieId);

        Favourite favourite = Favourite.create(
                null,
                user.getId(),
                movie.getId(),
                new FavouriteDate(LocalDate.now())
        );

        return favouriteRepository.addFavourite(favourite);
    }

    private void validateFavourite(UserId userId, MovieId movieId) {
        if (favouriteRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new FavouriteAlreadyExistsException(userId.value(), movieId.value());
        }
    }
}
