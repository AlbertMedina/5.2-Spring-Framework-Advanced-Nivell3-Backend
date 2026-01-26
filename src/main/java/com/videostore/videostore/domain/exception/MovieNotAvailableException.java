package com.videostore.videostore.domain.exception;

public class MovieNotAvailableException extends RuntimeException {
    public MovieNotAvailableException(Long movieId) {
        super("Movie " + movieId + " is not available for renting");
    }
}
