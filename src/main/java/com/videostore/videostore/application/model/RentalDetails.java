package com.videostore.videostore.application.model;

import com.videostore.videostore.domain.model.rental.Rental;

public record RentalDetails(
        Rental rental,
        String username,
        String title
) {
}
