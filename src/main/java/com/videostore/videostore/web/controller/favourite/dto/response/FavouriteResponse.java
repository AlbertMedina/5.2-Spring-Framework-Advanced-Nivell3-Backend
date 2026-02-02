package com.videostore.videostore.web.controller.favourite.dto.response;

import com.videostore.videostore.domain.model.favourite.Favourite;

public record FavouriteResponse(
        Long id,
        Long userId,
        Long movieId,
        String favouriteDate
) {
    public static FavouriteResponse fromDomain(Favourite favourite) {
        return new FavouriteResponse(
                favourite.getId().value(),
                favourite.getUserId().value(),
                favourite.getMovieId().value(),
                favourite.getFavouriteDate().toString()
        );
    }
}
