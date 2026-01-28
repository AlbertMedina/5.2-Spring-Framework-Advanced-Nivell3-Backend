package com.videostore.videostore.application.port.in.movie;

import com.videostore.videostore.application.command.movie.UpdateMovieInfoCommand;
import com.videostore.videostore.domain.model.movie.Movie;

public interface UpdateMovieInfoUseCase {
    Movie execute(Long id, UpdateMovieInfoCommand command);
}
