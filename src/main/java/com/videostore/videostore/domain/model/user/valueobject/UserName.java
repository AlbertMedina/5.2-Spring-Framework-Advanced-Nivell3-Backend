package com.videostore.videostore.domain.model.user.valueobject;

public record UserName(String value) {

    public UserName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (value.matches(".*\\s+.*")) {
            throw new IllegalArgumentException("Username cannot contain whitespace");
        }

        if (!value.matches("^[A-Za-z0-9_-]+$")) {
            throw new IllegalArgumentException("Username may only contain letters, numbers, underscores and hyphens");
        }

        if (value.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
    }
}
