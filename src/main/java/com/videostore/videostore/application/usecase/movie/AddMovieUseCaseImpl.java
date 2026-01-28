package com.videostore.videostore.application.usecase.movie;

import com.videostore.videostore.application.command.movie.AddMovieCommand;
import com.videostore.videostore.application.port.in.movie.AddMovieUseCase;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.*;
import com.videostore.videostore.domain.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddMovieUseCaseImpl implements AddMovieUseCase {

    private final MovieRepository movieRepository;

    public AddMovieUseCaseImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional
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
