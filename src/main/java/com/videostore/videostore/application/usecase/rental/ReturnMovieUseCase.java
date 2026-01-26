package com.videostore.videostore.application.usecase.rental;

import com.videostore.videostore.application.command.rental.ReturnMovieCommand;
import com.videostore.videostore.domain.exception.RentalNotFoundException;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.repository.RentalRepository;

public class ReturnMovieUseCase {

    private final RentalRepository rentalRepository;

    public ReturnMovieUseCase(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public void execute(ReturnMovieCommand returnMovieCommand) {
        Rental rental = rentalRepository.findByUserIdAndMovieId(returnMovieCommand.getUserId(), returnMovieCommand.getMovieId())
                .orElseThrow(() -> new RentalNotFoundException("User cannot return a movie they haven't rented"));

        rentalRepository.returnRental(rental);
    }
}
