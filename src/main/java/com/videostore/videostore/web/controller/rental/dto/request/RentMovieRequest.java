package com.videostore.videostore.web.controller.rental.dto.request;

public record RentMovieRequest(
        Long userId,
        Long movieId
) {
}
