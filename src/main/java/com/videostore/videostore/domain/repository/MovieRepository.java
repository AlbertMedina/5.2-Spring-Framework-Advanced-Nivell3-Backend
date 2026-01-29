package com.videostore.videostore.domain.repository;

import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;

import java.util.List;
import java.util.Optional;

public interface MovieRepository {

    Optional<Movie> findById(MovieId id);

    boolean existsById(MovieId id);

    List<Movie> findAll(int page, int amount, String genre, boolean onlyAvailable);

    Movie addMovie(Movie movie);

    Movie updateMovie(Movie movie);

    void removeMovie(MovieId movieId);
}
