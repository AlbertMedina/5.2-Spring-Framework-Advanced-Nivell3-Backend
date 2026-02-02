package com.videostore.videostore.domain.exception.conflict;

public class MovieHasActiveRentalsException extends BusinessRuleViolationException {
    public MovieHasActiveRentalsException(String message) {
        super(message);
    }
}
