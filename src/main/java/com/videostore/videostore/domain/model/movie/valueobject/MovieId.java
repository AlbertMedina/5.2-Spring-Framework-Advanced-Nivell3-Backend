package com.videostore.videostore.domain.model.movie.valueobject;

public record MovieId(Long value) {

    public MovieId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("MovieId cannot be null or non-positive");
        }
    }
}
