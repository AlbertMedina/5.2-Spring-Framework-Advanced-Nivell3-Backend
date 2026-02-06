package com.videostore.videostore.application.command.movie;

import com.videostore.videostore.domain.model.movie.MovieSortBy;

public record GetAllMoviesCommand(
        int page,
        int size,
        String genre,
        boolean onlyAvailable,
        String title,
        MovieSortBy sortBy,
        boolean ascending
) {
}
