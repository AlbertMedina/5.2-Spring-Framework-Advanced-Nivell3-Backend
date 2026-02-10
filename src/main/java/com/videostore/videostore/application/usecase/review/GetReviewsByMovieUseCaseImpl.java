package com.videostore.videostore.application.usecase.review;

import com.videostore.videostore.application.port.in.review.GetReviewsByMovieUseCase;
import com.videostore.videostore.domain.exception.notfound.MovieNotFoundException;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.review.Review;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetReviewsByMovieUseCaseImpl implements GetReviewsByMovieUseCase {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;

    public GetReviewsByMovieUseCaseImpl(ReviewRepository reviewRepository, MovieRepository movieRepository) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> execute(Long movieId) {
        MovieId movieIdVo = new MovieId(movieId);
        Movie movie = movieRepository.findById(movieIdVo)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        return reviewRepository.findAllByMovie(movie.getId());
    }
}
