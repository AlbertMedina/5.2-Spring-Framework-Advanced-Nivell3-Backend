package com.videostore.videostore.application.model;

import java.time.LocalDate;

public record RentalDetails(
        Long id,
        LocalDate rentalDate,
        String username,
        String title
) {
}
