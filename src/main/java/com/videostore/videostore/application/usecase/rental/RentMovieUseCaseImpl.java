package com.videostore.videostore.application.usecase.rental;

import com.videostore.videostore.application.command.rental.RentMovieCommand;
import com.videostore.videostore.application.model.RentalDetails;
import com.videostore.videostore.application.port.in.rental.RentMovieUseCase;
import com.videostore.videostore.domain.exception.conflict.MovieAlreadyRentedException;
import com.videostore.videostore.domain.exception.conflict.MovieNotAvailableException;
import com.videostore.videostore.domain.exception.notfound.MovieNotFoundException;
import com.videostore.videostore.domain.exception.notfound.UserNotFoundException;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.model.rental.valueobject.RentalDate;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.model.user.valueobject.Username;
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
    public RentalDetails execute(RentMovieCommand command) {
        Username username = new Username(command.username());
        MovieId movieId = new MovieId(command.movieId());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username.value()));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId.value()));

        validateRental(user.getId(), movieId, movie);

        Rental rental = Rental.create(
                null,
                user.getId(),
                movie.getId(),
                new RentalDate(LocalDate.now())
        );

        Rental newRental = rentalRepository.addRental(rental);

        return new RentalDetails(
                newRental.getId().value(),
                newRental.getRentalDate().value(),
                user.getUsername().value(),
                movie.getId().value(),
                movie.getTitle().value()
        );
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
