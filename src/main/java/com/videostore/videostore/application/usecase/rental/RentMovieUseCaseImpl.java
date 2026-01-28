package com.videostore.videostore.application.usecase.rental;

import com.videostore.videostore.application.command.rental.RentMovieCommand;
import com.videostore.videostore.application.port.in.rental.RentMovieUseCase;
import com.videostore.videostore.domain.exception.*;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.model.rental.valueobject.RentalDate;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.RentalRepository;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class RentMovieUseCaseImpl implements RentMovieUseCase {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public RentMovieUseCaseImpl(RentalRepository rentalRepository, MovieRepository movieRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional
    public Rental execute(RentMovieCommand command) {
        Long userId = command.userId();
        Long movieId = command.movieId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        validateRental(userId, movieId, movie);

        Rental rental = Rental.create(
                user,
                movie,
                new RentalDate(LocalDate.now())
        );

        return rentalRepository.addRental(rental);
    }

    private void validateRental(Long userId, Long movieId, Movie movie) {
        if (rentalRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new MovieAlreadyRentedException(userId, movieId);
        }

        if (rentalRepository.activeRentalsByMovie(movieId) >= movie.getNumberOfCopies().value()) {
            throw new MovieNotAvailableException(movieId);
        }
    }
}
