package com.videostore.videostore.application.port.in.movie;

import com.videostore.videostore.application.query.movie.GetAllMoviesQuery;
import com.videostore.videostore.domain.model.movie.Movie;

import java.util.List;

public interface GetAllMoviesUseCase {
    List<Movie> execute(GetAllMoviesQuery getAllMoviesQuery);
}
