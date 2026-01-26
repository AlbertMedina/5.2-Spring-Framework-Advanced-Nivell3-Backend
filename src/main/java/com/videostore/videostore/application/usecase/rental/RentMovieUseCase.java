package com.videostore.videostore.application.usecase.rental;

import com.videostore.videostore.application.command.rental.RentMovieCommand;
import com.videostore.videostore.domain.exception.*;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.model.rental.valueobject.RentalDate;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.RentalRepository;
import com.videostore.videostore.domain.repository.UserRepository;

import java.time.LocalDate;

public class RentMovieUseCase {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public RentMovieUseCase(RentalRepository rentalRepository, MovieRepository movieRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    public Rental execute(RentMovieCommand rentMovieCommand) {
        Long userId = rentMovieCommand.getUserId();
        Long movieId = rentMovieCommand.getMovieId();

        User user = userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException(userId));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        if (rentalRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new MovieAlreadyRentedException(userId, movieId);
        }

        if (rentalRepository.activeRentalsByMovie(movieId) >= movie.getNumberOfCopies().value()) {
            throw new MovieNotAvailableException(movieId);
        }

        return rentalRepository.addRental(new Rental(user, movie, new RentalDate(LocalDate.now())));
    }
}
