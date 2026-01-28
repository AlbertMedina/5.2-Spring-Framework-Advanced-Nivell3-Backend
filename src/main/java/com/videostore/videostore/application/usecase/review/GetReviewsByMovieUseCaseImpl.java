package com.videostore.videostore.application.usecase.review;

import com.videostore.videostore.application.port.in.review.GetReviewsByMovieUseCase;
import com.videostore.videostore.domain.model.review.Review;
import com.videostore.videostore.domain.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetReviewsByMovieUseCaseImpl implements GetReviewsByMovieUseCase {

    private final ReviewRepository reviewRepository;

    public GetReviewsByMovieUseCaseImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> execute(Long movieId) {
        return reviewRepository.findAllByMovie(movieId);
    }
}
