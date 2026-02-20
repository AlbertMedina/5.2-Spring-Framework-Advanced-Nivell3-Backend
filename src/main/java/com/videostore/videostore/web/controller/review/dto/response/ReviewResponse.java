package com.videostore.videostore.web.controller.review.dto.response;

import com.videostore.videostore.application.model.ReviewDetails;

public record ReviewResponse(
        Long id,
        Long userId,
        Long movieId,
        int rating,
        String comment,
        String reviewDate,
        String username
) {
    public static ReviewResponse from(ReviewDetails reviewDetails) {
        return new ReviewResponse(
                reviewDetails.review().getId().value(),
                reviewDetails.review().getUserId().value(),
                reviewDetails.review().getMovieId().value(),
                reviewDetails.review().getRating().value(),
                reviewDetails.review().getComment().value(),
                reviewDetails.review().getReviewDate().value().toString(),
                reviewDetails.username()
        );
    }
}
