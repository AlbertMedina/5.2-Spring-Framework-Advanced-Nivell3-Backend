package com.videostore.videostore.web.controller.favourite.dto.request;

public record AddFavouriteRequest(
        Long userId,
        Long movieId
) {
}
