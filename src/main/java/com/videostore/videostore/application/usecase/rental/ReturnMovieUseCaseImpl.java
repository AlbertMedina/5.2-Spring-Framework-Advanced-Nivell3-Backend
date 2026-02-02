package com.videostore.videostore.application.usecase.rental;

import com.videostore.videostore.application.command.rental.ReturnMovieCommand;
import com.videostore.videostore.application.port.in.rental.ReturnMovieUseCase;
import com.videostore.videostore.domain.exception.notfound.RentalNotFoundException;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReturnMovieUseCaseImpl implements ReturnMovieUseCase {

    private final RentalRepository rentalRepository;

    public ReturnMovieUseCaseImpl(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional
    public void execute(ReturnMovieCommand command) {
        Rental rental = rentalRepository.findByUserIdAndMovieId(new UserId(command.userId()), new MovieId(command.movieId()))
                .orElseThrow(() -> new RentalNotFoundException("User cannot return a movie they haven't rented"));

        rentalRepository.removeRental(rental.getId());
    }
}
