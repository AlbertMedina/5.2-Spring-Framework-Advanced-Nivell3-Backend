package com.videostore.videostore.application.usecase.rental;

import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.repository.RentalRepository;

import java.util.List;

public class GetRentalsByMovieUseCase {

    private final RentalRepository rentalRepository;

    public GetRentalsByMovieUseCase(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public List<Rental> execute(Long movieId) {
        return rentalRepository.findAllByMovie(movieId);
    }
}
