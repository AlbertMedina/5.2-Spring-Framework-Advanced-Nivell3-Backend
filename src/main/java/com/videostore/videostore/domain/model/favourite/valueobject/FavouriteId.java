package com.videostore.videostore.domain.model.favourite.valueobject;

public record FavouriteId(Long value) {

    public FavouriteId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("FavouriteId cannot be null or non-positive");
        }
    }
}
