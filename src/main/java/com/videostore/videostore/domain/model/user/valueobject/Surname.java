package com.videostore.videostore.domain.model.user.valueobject;

public record Surname(String value) {

    public Surname {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Surname cannot be empty");
        }

        if (!value.matches("^[A-Za-zÀ-ÿ' -]+$")) {
            throw new IllegalArgumentException("Invalid surname format");
        }
    }
}
