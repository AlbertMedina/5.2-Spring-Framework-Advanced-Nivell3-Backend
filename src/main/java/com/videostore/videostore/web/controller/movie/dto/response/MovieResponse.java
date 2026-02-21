package com.videostore.videostore.web.controller.movie.dto.response;

import com.videostore.videostore.application.model.MovieDetails;

public record MovieResponse(
        Long id,
        String title,
        int year,
        String genre,
        int duration,
        String director,
        String synopsis,
        int numberOfCopies,
        String posterUrl,
        RatingResponse rating
) {

    public static MovieResponse from(MovieDetails movieDetails) {
        return new MovieResponse(
                movieDetails.id(),
                movieDetails.title(),
                movieDetails.year(),
                movieDetails.genre(),
                movieDetails.duration(),
                movieDetails.director(),
                movieDetails.synopsis(),
                movieDetails.numberOfCopies(),
                movieDetails.posterUrl() != null ? movieDetails.posterUrl() : null,
                movieDetails.rating() != null ? RatingResponse.from(movieDetails.rating()) : null
        );
    }
}
