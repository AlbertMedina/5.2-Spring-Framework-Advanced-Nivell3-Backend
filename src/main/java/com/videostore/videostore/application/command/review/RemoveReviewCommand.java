package com.videostore.videostore.application.command.review;

public record RemoveReviewCommand(
        String username,
        Long movieId
) {
}
