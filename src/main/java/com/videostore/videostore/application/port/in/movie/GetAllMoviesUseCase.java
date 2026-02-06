package com.videostore.videostore.application.port.in.movie;

import com.videostore.videostore.application.command.movie.GetAllMoviesCommand;
import com.videostore.videostore.domain.model.movie.Movie;

import java.util.List;

public interface GetAllMoviesUseCase {
    List<Movie> execute(GetAllMoviesCommand getAllMoviesCommand);
}
