package com.videostore.videostore.application.port.in.review;

import com.videostore.videostore.domain.model.review.Review;

import java.util.List;

public interface GetReviewsByMovieUseCase {
    List<Review> execute(Long movieId);
}
