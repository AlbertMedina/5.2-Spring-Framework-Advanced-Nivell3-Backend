package com.videostore.videostore.domain.model.user.valueobject;

public record Email(String value) {

    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (!value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
