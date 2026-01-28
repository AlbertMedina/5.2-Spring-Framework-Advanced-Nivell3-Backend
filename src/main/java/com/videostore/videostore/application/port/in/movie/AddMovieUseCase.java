package com.videostore.videostore.application.port.in.movie;

import com.videostore.videostore.application.command.movie.AddMovieCommand;
import com.videostore.videostore.domain.model.movie.Movie;

public interface AddMovieUseCase {
    Movie execute(AddMovieCommand command);
}
