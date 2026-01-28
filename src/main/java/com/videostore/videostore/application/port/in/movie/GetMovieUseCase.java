package com.videostore.videostore.application.port.in.movie;

import com.videostore.videostore.domain.model.movie.Movie;

public interface GetMovieUseCase {
    Movie execute(Long id);
}
