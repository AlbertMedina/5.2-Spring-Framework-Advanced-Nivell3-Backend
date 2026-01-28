package com.videostore.videostore.application.port.in.review;

import com.videostore.videostore.application.command.review.RemoveReviewCommand;

public interface RemoveReviewUseCase {
    void execute(RemoveReviewCommand command);
}
