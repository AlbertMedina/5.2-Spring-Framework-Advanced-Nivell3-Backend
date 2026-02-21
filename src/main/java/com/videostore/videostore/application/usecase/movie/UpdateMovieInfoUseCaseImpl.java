package com.videostore.videostore.application.usecase.movie;

import com.videostore.videostore.application.command.movie.UpdateMovieInfoCommand;
import com.videostore.videostore.application.model.MovieDetails;
import com.videostore.videostore.application.port.in.movie.UpdateMovieInfoUseCase;
import com.videostore.videostore.domain.common.RatingSummary;
import com.videostore.videostore.domain.exception.notfound.MovieNotFoundException;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.*;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateMovieInfoUseCaseImpl implements UpdateMovieInfoUseCase {

    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;

    public UpdateMovieInfoUseCaseImpl(MovieRepository movieRepository, ReviewRepository reviewRepository) {
        this.movieRepository = movieRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public MovieDetails execute(Long id, UpdateMovieInfoCommand command) {
        MovieId movieId = new MovieId(id);
        Movie movie = movieRepository.findById(new MovieId(id))
                .orElseThrow(() -> new MovieNotFoundException(id));

        movie.setTitle(new Title(command.title()));
        movie.setYear(new Year(command.year()));
        movie.setGenre(new Genre(command.genre()));
        movie.setDuration(new Duration(command.duration()));
        movie.setDirector(new Director(command.director()));
        movie.setSynopsis(new Synopsis(command.synopsis()));
        Movie updated = movieRepository.updateMovie(movie);

        RatingSummary ratingSummary = reviewRepository.getAverageRatingByMovieId(movieId).orElse(new RatingSummary(0.0, 0));

        return new MovieDetails(
                updated.getId().value(),
                updated.getTitle().value(),
                updated.getYear().value(),
                updated.getGenre().value(),
                updated.getDuration().value(),
                updated.getDirector().value(),
                updated.getSynopsis().value(),
                updated.getNumberOfCopies().value(),
                updated.getPosterUrl().value(),
                ratingSummary
        );
    }
}
