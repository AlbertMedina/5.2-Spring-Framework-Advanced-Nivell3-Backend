package com.videostore.videostore.domain.exception;

public class MovieAlreadyRentedException extends RuntimeException {
    public MovieAlreadyRentedException(Long userId, Long movieId) {
        super("User " + userId + " has already rented movie " + movieId);
    }
}
