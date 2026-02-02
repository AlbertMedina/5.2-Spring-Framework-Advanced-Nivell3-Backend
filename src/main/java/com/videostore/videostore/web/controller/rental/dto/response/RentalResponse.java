package com.videostore.videostore.web.controller.rental.dto.response;

import com.videostore.videostore.domain.model.rental.Rental;

public record RentalResponse(
        Long id,
        Long userId,
        Long movieId,
        String rentalDate
) {
    public static RentalResponse fromDomain(Rental rental) {
        return new RentalResponse(
                rental.getId().value(),
                rental.getUserId().value(),
                rental.getMovieId().value(),
                rental.getRentalDate().toString()
        );
    }
}
