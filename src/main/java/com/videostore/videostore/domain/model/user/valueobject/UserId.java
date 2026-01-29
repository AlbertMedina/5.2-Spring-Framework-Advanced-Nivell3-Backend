package com.videostore.videostore.domain.model.user.valueobject;

public record UserId(Long value) {

    public UserId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("UserId cannot be null or non-positive");
        }
    }
}
