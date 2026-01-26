package com.videostore.videostore.domain.model.movie.valueobject;

public record Director(String value) {

    public Director {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Movie director cannot be empty");
        }

        if (!value.matches("^[A-Za-zÀ-ÿ' -]+$")) {
            throw new IllegalArgumentException("Invalid movie director name format");
        }
    }
}
