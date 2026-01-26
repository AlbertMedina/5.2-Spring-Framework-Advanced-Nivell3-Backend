package com.videostore.videostore.domain.model.user.valueobject;

public record Name(String value) {

    public Name {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (!value.matches("^[A-Za-zÀ-ÿ' -]+$")) {
            throw new IllegalArgumentException("Invalid name format");
        }
    }
}
