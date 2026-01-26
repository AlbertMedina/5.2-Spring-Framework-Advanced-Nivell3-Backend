package com.videostore.videostore.application.usecase.movie;

import com.videostore.videostore.application.command.movie.UpdateMovieInfoCommand;
import com.videostore.videostore.domain.exception.MovieNotFoundException;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.*;
import com.videostore.videostore.domain.repository.MovieRepository;

public class UpdateMovieInfoUseCase {

    private final MovieRepository movieRepository;

    public UpdateMovieInfoUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie execute(Long id, UpdateMovieInfoCommand command) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        movie.setTitle(new Title(command.title()));
        movie.setYear(new Year(command.year()));
        movie.setGenre(new Genre(command.genre()));
        movie.setDuration(new Duration(command.duration()));
        movie.setDirector(new Director(command.director()));
        movie.setSynopsis(new Synopsis(command.synopsis()));

        return movieRepository.addMovie(movie);
    }
}
