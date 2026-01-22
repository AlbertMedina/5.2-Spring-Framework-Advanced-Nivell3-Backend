package com.videostore.videostore.domain.service;

import com.videostore.videostore.domain.exception.BusinessRuleViolationException;

public class FavouriteDomainService {

    public void validateAddFavourite(boolean isAlreadyFavourite) {
        if (isAlreadyFavourite) {
            throw new BusinessRuleViolationException("User has already added this movie to favourites");
        }
    }

    public void validateRemoveFavourite(boolean isFavourite) {
        if (!isFavourite) {
            throw new BusinessRuleViolationException("User cannot remove a movie that is not in favourites");
        }
    }
}
