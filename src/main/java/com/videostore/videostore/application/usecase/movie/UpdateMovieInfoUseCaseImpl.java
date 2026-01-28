package com.videostore.videostore.application.usecase.movie;

import com.videostore.videostore.application.command.movie.UpdateMovieInfoCommand;
import com.videostore.videostore.application.port.in.movie.UpdateMovieInfoUseCase;
import com.videostore.videostore.domain.exception.MovieNotFoundException;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.*;
import com.videostore.videostore.domain.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateMovieInfoUseCaseImpl implements UpdateMovieInfoUseCase {

    private final MovieRepository movieRepository;

    public UpdateMovieInfoUseCaseImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional
    public Movie execute(Long id, UpdateMovieInfoCommand command) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        movie.setTitle(new Title(command.title()));
        movie.setYear(new Year(command.year()));
        movie.setGenre(new Genre(command.genre()));
        movie.setDuration(new Duration(command.duration()));
        movie.setDirector(new Director(command.director()));
        movie.setSynopsis(new Synopsis(command.synopsis()));

        return movieRepository.updateMovie(movie);
    }
}
