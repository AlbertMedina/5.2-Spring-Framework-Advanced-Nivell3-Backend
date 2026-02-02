package com.videostore.videostore.web.controller.favourite.dto.response;

import com.videostore.videostore.domain.model.favourite.Favourite;

public record FavouriteResponse(
        Long userId,
        Long movieId,
        String favouriteDate
) {
    public static FavouriteResponse fromDomain(Favourite favourite) {
        return new FavouriteResponse(
                favourite.getUserId().value(),
                favourite.getMovieId().value(),
                favourite.getFavouriteDate().toString()
        );
    }
}
