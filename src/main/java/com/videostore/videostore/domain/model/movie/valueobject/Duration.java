package com.videostore.videostore.domain.model.movie.valueobject;

public record Duration(int value) {

    public Duration {
        if (value <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
    }
}
