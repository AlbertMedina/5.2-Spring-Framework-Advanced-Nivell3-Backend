package com.videostore.videostore.domain.model.review.valueobject;

import java.time.LocalDate;

public record ReviewDate(LocalDate value) {

    public ReviewDate {
        if (value == null) {
            throw new IllegalArgumentException("Review date cannot be null");
        }
        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Review date cannot be in the future");
        }
    }
}
