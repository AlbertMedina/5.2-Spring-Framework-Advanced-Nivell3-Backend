package com.videostore.videostore.infrastructure.persistence.mapper;

import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.*;
import com.videostore.videostore.infrastructure.persistence.entity.MovieEntity;

public final class MovieMapper {

    private MovieMapper() {
    }

    public static MovieEntity toEntity(Movie movie) {
        return new MovieEntity(
                movie.getTitle().value(),
                movie.getYear().value(),
                movie.getGenre().value(),
                movie.getDuration().value(),
                movie.getDirector().value(),
                movie.getSynopsis().value(),
                movie.getNumberOfCopies().value()
        );
    }

    public static Movie toDomain(MovieEntity entity) {
        return Movie.create(
                new Title(entity.getTitle()),
                new Year(entity.getYear()),
                new Genre(entity.getGenre()),
                new Duration(entity.getDuration()),
                new Director(entity.getDirector()),
                new Synopsis(entity.getSynopsis()),
                new NumberOfCopies(entity.getNumberOfCopies())
        );
    }
}
