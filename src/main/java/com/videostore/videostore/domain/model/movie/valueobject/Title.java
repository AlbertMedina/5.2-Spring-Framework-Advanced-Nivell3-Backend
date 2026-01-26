package com.videostore.videostore.domain.model.movie.valueobject;

public record Title(String value) {
    
    public Title {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Movie title cannot be empty");
        }
    }
}
