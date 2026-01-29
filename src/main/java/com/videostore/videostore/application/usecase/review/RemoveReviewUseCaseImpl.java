package com.videostore.videostore.application.usecase.review;

import com.videostore.videostore.application.command.review.RemoveReviewCommand;
import com.videostore.videostore.application.port.in.review.RemoveReviewUseCase;
import com.videostore.videostore.domain.exception.ReviewNotFoundException;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.review.Review;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveReviewUseCaseImpl implements RemoveReviewUseCase {
    private final ReviewRepository reviewRepository;

    public RemoveReviewUseCaseImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public void execute(RemoveReviewCommand command) {
        Review review = reviewRepository.findByUserIdAndMovieId(new UserId(command.userId()), new MovieId(command.userId()))
                .orElseThrow(() -> new ReviewNotFoundException("User cannot remove a movie they haven't made"));

        reviewRepository.removeReview(review);
    }
}
