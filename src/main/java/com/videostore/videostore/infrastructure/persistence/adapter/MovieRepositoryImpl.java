package com.videostore.videostore.infrastructure.persistence.adapter;

import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.repository.MovieRepository;

import java.util.List;
import java.util.Optional;

public class MovieRepositoryImpl implements MovieRepository {

    @Override
    public Optional<Movie> findById(MovieId id) {
        return Optional.empty();
    }

    @Override
    public List<Movie> findAll(int page, int amount, String genre, boolean onlyAvailable) {
        return List.of();
    }

    @Override
    public Movie addMovie(Movie movie) {
        return null;
    }

    @Override
    public Movie updateMovie(Movie movie) {
        return null;
    }

    @Override
    public void removeMovie(Movie movie) {

    }
}
