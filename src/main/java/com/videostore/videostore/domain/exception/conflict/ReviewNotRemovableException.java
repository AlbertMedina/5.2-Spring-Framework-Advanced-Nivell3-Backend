package com.videostore.videostore.domain.exception.conflict;

public class ReviewNotRemovableException extends BusinessRuleViolationException {
    public ReviewNotRemovableException(Long reviewId, Long userId) {
        super(userId + " is not allowed to remove review " + reviewId);
    }
}
