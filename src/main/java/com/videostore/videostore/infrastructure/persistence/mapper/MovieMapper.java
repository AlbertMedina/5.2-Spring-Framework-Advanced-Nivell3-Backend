package com.videostore.videostore.infrastructure.persistence.mapper;

import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.*;
import com.videostore.videostore.infrastructure.persistence.entity.MovieEntity;

public final class MovieMapper {

    private MovieMapper() {
    }

    public static MovieEntity toEntity(Movie movie) {
        MovieEntity entity = new MovieEntity(
                movie.getTitle().value(),
                movie.getYear().value(),
                movie.getGenre().value(),
                movie.getDuration().value(),
                movie.getDirector().value(),
                movie.getSynopsis().value(),
                movie.getNumberOfCopies().value()
        );

        if (movie.getPosterUrl() != null) {
            entity.setPosterUrl(movie.getPosterUrl().value());
        }

        return entity;
    }

    public static Movie toDomain(MovieEntity entity) {
        Movie movie = Movie.create(
                new MovieId(entity.getId()),
                new Title(entity.getTitle()),
                new Year(entity.getYear()),
                new Genre(entity.getGenre()),
                new Duration(entity.getDuration()),
                new Director(entity.getDirector()),
                new Synopsis(entity.getSynopsis()),
                new NumberOfCopies(entity.getNumberOfCopies())
        );

        if (entity.getPosterUrl() != null) {
            movie.setPosterUrl(new PosterUrl(entity.getPosterUrl()));
        }

        return movie;
    }
}
