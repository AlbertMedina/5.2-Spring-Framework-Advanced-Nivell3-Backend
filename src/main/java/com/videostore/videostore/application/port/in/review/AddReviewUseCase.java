package com.videostore.videostore.application.port.in.review;

import com.videostore.videostore.application.command.review.AddReviewCommand;
import com.videostore.videostore.domain.model.review.Review;

public interface AddReviewUseCase {
    Review execute(AddReviewCommand command);
}
