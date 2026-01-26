package com.videostore.videostore.domain.model.movie.valueobject;

public record NumberOfCopies(int value) {

    public NumberOfCopies {
        if (value < 0) {
            throw new IllegalArgumentException("There must be at least one copy");
        }
    }
}
