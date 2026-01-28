package com.videostore.videostore.application.port.in.movie.query;

public record GetAllMoviesQuery(
        int page,
        int amount,
        String genre,
        boolean onlyAvailable
) {
}
