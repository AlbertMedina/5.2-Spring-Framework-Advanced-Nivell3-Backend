package com.videostore.videostore.domain.exception;

public class FavouriteAlreadyExistingException extends RuntimeException {
    public FavouriteAlreadyExistingException(Long userId, Long movieId) {
        super("User " + userId + " has already in favourites the movie " + movieId);
    }
}
