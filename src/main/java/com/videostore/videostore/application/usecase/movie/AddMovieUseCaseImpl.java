package com.videostore.videostore.application.usecase.movie;

import com.videostore.videostore.application.command.movie.AddMovieCommand;
import com.videostore.videostore.application.model.MovieDetails;
import com.videostore.videostore.application.port.in.movie.AddMovieUseCase;
import com.videostore.videostore.application.port.out.ImageStoragePort;
import com.videostore.videostore.domain.common.RatingSummary;
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
    public MovieDetails execute(AddMovieCommand command) {
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

        Movie newMovie = movieRepository.addMovie(movie);

        return new MovieDetails(
                newMovie.getId().value(),
                newMovie.getTitle().value(),
                newMovie.getYear().value(),
                newMovie.getGenre().value(),
                newMovie.getDuration().value(),
                newMovie.getDirector().value(),
                newMovie.getSynopsis().value(),
                newMovie.getNumberOfCopies().value(),
                newMovie.getPosterUrl() != null ? newMovie.getPosterUrl().value() : null,
                new RatingSummary(0, 0)
        );
    }
}
