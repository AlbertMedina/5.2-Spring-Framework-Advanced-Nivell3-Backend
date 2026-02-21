package com.videostore.videostore.web.controller.rental.dto.response;

import com.videostore.videostore.application.model.RentalDetails;

public record RentalResponse(
        Long id,
        String rentalDate,
        String username,
        Long movieId,
        String title
) {
    public static RentalResponse from(RentalDetails rentalDetails) {
        return new RentalResponse(
                rentalDetails.id(),
                rentalDetails.rentalDate().toString(),
                rentalDetails.username(),
                rentalDetails.movieId(),
                rentalDetails.title()
        );
    }
}
