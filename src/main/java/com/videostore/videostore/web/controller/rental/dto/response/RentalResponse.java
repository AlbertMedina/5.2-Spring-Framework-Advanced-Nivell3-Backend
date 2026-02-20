package com.videostore.videostore.web.controller.rental.dto.response;

import com.videostore.videostore.application.model.RentalDetails;

public record RentalResponse(
        Long id,
        Long userId,
        Long movieId,
        String rentalDate,
        String username,
        String title
) {
    public static RentalResponse from(RentalDetails rentalDetails) {
        return new RentalResponse(
                rentalDetails.rental().getId().value(),
                rentalDetails.rental().getUserId().value(),
                rentalDetails.rental().getMovieId().value(),
                rentalDetails.rental().getRentalDate().toString(),
                rentalDetails.username(),
                rentalDetails.title()
        );
    }
}
