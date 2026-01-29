package com.videostore.videostore.domain.model.review.valueobject;

public record ReviewId(Long value) {

    public ReviewId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ReviewId cannot be null or non-positive");
        }
    }
}
