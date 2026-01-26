package com.videostore.videostore.domain.model.movie.valueobject;

public record Genre(String value) {

    public Genre {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Movie genre cannot be empty");
        }

        if (!value.matches("^[A-Za-zÀ-ÿ' -]+$")) {
            throw new IllegalArgumentException("Invalid genre");
        }
    }
}
