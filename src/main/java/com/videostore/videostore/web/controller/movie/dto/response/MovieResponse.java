package com.videostore.videostore.web.controller.movie.dto.response;

import com.videostore.videostore.domain.model.movie.Movie;

public record MovieResponse(
        Long id,
        String title,
        int year,
        String genre,
        int duration,
        String director,
        String synopsis,
        int numberOfCopies,
        String posterUrl
) {
    public static MovieResponse fromDomain(Movie movie) {
        return new MovieResponse(
                movie.getId().value(),
                movie.getTitle().value(),
                movie.getYear().value(),
                movie.getGenre().value(),
                movie.getDuration().value(),
                movie.getDirector().value(),
                movie.getSynopsis().value(),
                movie.getNumberOfCopies().value(),
                movie.getPosterUrl() != null ? movie.getPosterUrl().value() : null
        );
    }
}
