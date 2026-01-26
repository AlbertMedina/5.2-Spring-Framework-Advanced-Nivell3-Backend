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
                new Title(command.getTitle()),
                new Year(command.getYear()),
                new Genre(command.getGenre()),
                new Duration(command.getDuration()),
                new Director(command.getDirector()),
                new Synopsis(command.getSynopsis()),
                new NumberOfCopies(command.getNumberOfCopies())
        );

        return movieRepository.save(movie);
    }
}
