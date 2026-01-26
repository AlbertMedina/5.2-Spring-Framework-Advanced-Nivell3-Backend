package com.videostore.videostore.domain.model.movie.valueobject;

public record Synopsis(String value) {

    public Synopsis {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Movie synopsys cannot be empty");
        }
    }
}
