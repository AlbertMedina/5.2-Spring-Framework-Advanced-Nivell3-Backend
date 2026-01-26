package com.videostore.videostore.application.usecase.movie;

import com.videostore.videostore.application.command.movie.AddMovieCommand;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.*;
import com.videostore.videostore.domain.repository.MovieRepository;

public class AddMovieUseCase {

    private final MovieRepository movieRepository;

    public AddMovieUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie execute(AddMovieCommand command) {
        Movie movie = Movie.create(
                new Title(command.title()),
                new Year(command.year()),
                new Genre(command.genre()),
                new Duration(command.duration()),
                new Director(command.director()),
                new Synopsis(command.synopsis()),
                new NumberOfCopies(command.numberOfCopies())
        );

        return movieRepository.addMovie(movie);
    }
}
