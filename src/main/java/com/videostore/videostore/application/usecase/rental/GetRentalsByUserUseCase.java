package com.videostore.videostore.application.usecase.rental;

import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.repository.RentalRepository;

import java.util.List;

public class GetRentalsByUserUseCase {

    private final RentalRepository rentalRepository;

    public GetRentalsByUserUseCase(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public List<Rental> execute(Long userId) {
        return rentalRepository.findAllByUser(userId);
    }
}
