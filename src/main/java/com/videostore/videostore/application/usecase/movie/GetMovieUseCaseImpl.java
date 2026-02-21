package com.videostore.videostore.application.usecase.movie;

import com.videostore.videostore.application.model.MovieDetails;
import com.videostore.videostore.application.port.in.movie.GetMovieUseCase;
import com.videostore.videostore.domain.common.RatingSummary;
import com.videostore.videostore.domain.exception.notfound.MovieNotFoundException;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetMovieUseCaseImpl implements GetMovieUseCase {

    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;

    public GetMovieUseCaseImpl(MovieRepository movieRepository, ReviewRepository reviewRepository) {
        this.movieRepository = movieRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public MovieDetails execute(Long id) {
        MovieId movieId = new MovieId(id);
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException(id));
        RatingSummary ratingSummary = reviewRepository.getAverageRatingByMovieId(movieId).orElse(new RatingSummary(0.0, 0));

        return new MovieDetails(
                movie.getId().value(),
                movie.getTitle().value(),
                movie.getYear().value(),
                movie.getGenre().value(),
                movie.getDuration().value(),
                movie.getDirector().value(),
                movie.getSynopsis().value(),
                movie.getNumberOfCopies().value(),
                movie.getPosterUrl().value(),
                ratingSummary
        );
    }
}
