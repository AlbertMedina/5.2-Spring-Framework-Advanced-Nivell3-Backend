package com.videostore.videostore.application.usecase.movie;

import com.videostore.videostore.application.command.movie.AddMovieCommand;
import com.videostore.videostore.application.port.in.movie.AddMovieUseCase;
import com.videostore.videostore.application.port.out.ImageStoragePort;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.*;
import com.videostore.videostore.domain.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddMovieUseCaseImpl implements AddMovieUseCase {

    private final MovieRepository movieRepository;
    private final ImageStoragePort imageStoragePort;

    public AddMovieUseCaseImpl(MovieRepository movieRepository, ImageStoragePort imageStoragePort) {
        this.movieRepository = movieRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Override
    @Transactional
    public Movie execute(AddMovieCommand command) {
        Movie movie = Movie.create(
                null,
                new Title(command.title()),
                new Year(command.year()),
                new Genre(command.genre()),
                new Duration(command.duration()),
                new Director(command.director()),
                new Synopsis(command.synopsis()),
                new NumberOfCopies(command.numberOfCopies())
        );

        if (command.poster() != null) {
            String posterUrl = imageStoragePort.upload(
                    command.poster(),
                    command.posterFilename()
            );
            movie.setPosterUrl(new PosterUrl(posterUrl));
        }

        return movieRepository.addMovie(movie);
    }
}
