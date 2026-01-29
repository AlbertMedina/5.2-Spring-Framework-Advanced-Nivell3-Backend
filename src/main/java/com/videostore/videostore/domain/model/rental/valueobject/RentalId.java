package com.videostore.videostore.domain.model.rental.valueobject;

public record RentalId(Long value) {

    public RentalId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("RentalId cannot be null or non-positive");
        }
    }
}
