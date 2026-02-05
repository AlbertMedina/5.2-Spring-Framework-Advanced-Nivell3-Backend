package com.videostore.videostore.application.command.review;

public record AddReviewCommand(
        String username,
        Long movieId,
        int rating,
        String comment
) {
}
