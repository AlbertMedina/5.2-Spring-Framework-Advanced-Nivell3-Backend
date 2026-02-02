package com.videostore.videostore.domain.exception.conflict;

public class MovieNotAvailableException extends BusinessRuleViolationException {
    public MovieNotAvailableException(Long movieId) {
        super("Movie " + movieId + " is not available for renting");
    }
}
