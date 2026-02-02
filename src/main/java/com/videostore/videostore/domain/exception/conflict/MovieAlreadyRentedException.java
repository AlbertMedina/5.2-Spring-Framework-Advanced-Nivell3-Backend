package com.videostore.videostore.domain.exception.conflict;

public class MovieAlreadyRentedException extends BusinessRuleViolationException {
    public MovieAlreadyRentedException(Long userId, Long movieId) {
        super("User " + userId + " has already rented movie " + movieId);
    }
}
