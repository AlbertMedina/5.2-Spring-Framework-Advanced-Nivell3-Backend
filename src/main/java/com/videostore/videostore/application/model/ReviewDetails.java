package com.videostore.videostore.application.model;

import java.time.LocalDate;

public record ReviewDetails(
        Long id,
        int rating,
        String comment,
        LocalDate reviewDate,
        String username
) {
}