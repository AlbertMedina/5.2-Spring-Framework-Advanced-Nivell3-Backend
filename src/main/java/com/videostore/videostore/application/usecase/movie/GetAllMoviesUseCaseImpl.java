package com.videostore.videostore.application.usecase.movie;

import com.videostore.videostore.application.model.MovieDetails;
import com.videostore.videostore.application.port.in.movie.GetAllMoviesUseCase;
import com.videostore.videostore.application.command.movie.GetAllMoviesCommand;
import com.videostore.videostore.domain.common.PagedResult;
import com.videostore.videostore.domain.common.RatingSummary;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class GetAllMoviesUseCaseImpl implements GetAllMoviesUseCase {

    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;

    public GetAllMoviesUseCaseImpl(MovieRepository movieRepository, ReviewRepository reviewRepository) {
        this.movieRepository = movieRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<MovieDetails> execute(GetAllMoviesCommand getAllMoviesCommand) {
        PagedResult<Movie> movies = movieRepository.findAll(
                getAllMoviesCommand.page(),
                getAllMoviesCommand.size(),
                getAllMoviesCommand.genre(),
                getAllMoviesCommand.onlyAvailable(),
                getAllMoviesCommand.title(),
                getAllMoviesCommand.sortBy(),
                getAllMoviesCommand.ascending()
        );

        List<MovieId> movieIds = movies.getContent()
                .stream()
                .map(Movie::getId)
                .toList();

        Map<Long, RatingSummary> ratings = reviewRepository.getAverageRatingsByMovieIds(movieIds);

        List<MovieDetails> moviesWithRatings = movies.getContent()
                .stream()
                .map(movie -> {
                    RatingSummary rating = ratings.get(movie.getId().value());
                    if (rating == null) rating = new RatingSummary(0, 0);
                    return new MovieDetails(
                            movie.getId().value(),
                            movie.getTitle().value(),
                            movie.getYear().value(),
                            movie.getGenre().value(),
                            movie.getDuration().value(),
                            movie.getDirector().value(),
                            movie.getSynopsis().value(),
                            movie.getNumberOfCopies().value(),
                            movie.getPosterUrl() != null ? movie.getPosterUrl().value() : null,
                            rating);
                })
                .toList();

        return new PagedResult<>(
                moviesWithRatings,
                movies.getPage(),
                movies.getSize(),
                movies.getTotalElements()
        );
    }
}
