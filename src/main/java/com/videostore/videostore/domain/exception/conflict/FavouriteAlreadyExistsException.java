package com.videostore.videostore.domain.exception.conflict;

public class FavouriteAlreadyExistsException extends BusinessRuleViolationException {
    public FavouriteAlreadyExistsException(Long userId, Long movieId) {
        super("User " + userId + " has already in favourites the movie " + movieId);
    }
}
