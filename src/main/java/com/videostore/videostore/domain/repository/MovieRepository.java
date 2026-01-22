package com.videostore.videostore.domain.repository;

import com.videostore.videostore.domain.model.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieRepository {

    Optional<Movie> findById(Long id);

    List<Movie> findAll();

    // TODO: define filters
    List<Movie> findFiltered();

    Movie save(Movie movie);

    void deleteById(Long id);
}
