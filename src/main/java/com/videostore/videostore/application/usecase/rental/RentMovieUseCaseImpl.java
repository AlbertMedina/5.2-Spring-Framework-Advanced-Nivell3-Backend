package com.videostore.videostore.application.usecase.rental;

import com.videostore.videostore.application.command.rental.RentMovieCommand;
import com.videostore.videostore.application.port.in.rental.RentMovieUseCase;
import com.videostore.videostore.domain.exception.*;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.model.rental.valueobject.RentalDate;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
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
        UserId userId = new UserId(command.userId());
        MovieId movieId = new MovieId(command.movieId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.value()));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId.value()));

        validateRental(userId, movieId, movie);

        Rental rental = Rental.create(
                null,
                user.getId(),
                movie.getId(),
                new RentalDate(LocalDate.now())
        );

        return rentalRepository.addRental(rental);
    }

    private void validateRental(UserId userId, MovieId movieId, Movie movie) {
        if (rentalRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new MovieAlreadyRentedException(userId.value(), movieId.value());
        }

        if (rentalRepository.countRentalsByMovie(movieId) >= movie.getNumberOfCopies().value()) {
            throw new MovieNotAvailableException(movieId.value());
        }
    }
}
