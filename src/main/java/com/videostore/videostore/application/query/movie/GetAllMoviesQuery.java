package com.videostore.videostore.application.query.movie;

public record GetAllMoviesQuery(
        int page,
        int amount,
        String genre,
        boolean onlyAvailable
) {
}
