package com.videostore.videostore.domain.exception.conflict;

public class MovieAlreadyReviewedException extends BusinessRuleViolationException {
    public MovieAlreadyReviewedException(Long userId, Long movieId) {
        super("User " + userId + " has already a review on movie " + movieId);
    }
}
