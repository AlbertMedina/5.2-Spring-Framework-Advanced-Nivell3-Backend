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
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException(id));

        movie.setTitle(new Title(command.getTitle()));
        movie.setYear(new Year(command.getYear()));
        movie.setGenre(new Genre(command.getGenre()));
        movie.setDuration(new Duration(command.getDuration()));
        movie.setDirector(new Director(command.getDirector()));
        movie.setSynopsis(new Synopsis(command.getSynopsis()));

        return movieRepository.save(movie);
    }
}
