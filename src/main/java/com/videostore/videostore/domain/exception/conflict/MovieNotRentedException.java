package com.videostore.videostore.domain.exception.conflict;

public class MovieNotRentedException extends BusinessRuleViolationException {
    public MovieNotRentedException(Long userId, Long movieId) {
        super("User " + userId + " has not rented the movie " + movieId);
    }
}
