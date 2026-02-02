package com.videostore.videostore.application.auth;

public record LoginIdentifier(String value) {

    public LoginIdentifier {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Invalid login identifier");
        }
    }

    public boolean isEmail() {
        return value.contains("@");
    }
}