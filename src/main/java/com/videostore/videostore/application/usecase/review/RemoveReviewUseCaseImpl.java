package com.videostore.videostore.application.usecase.review;

import com.videostore.videostore.application.command.review.RemoveReviewCommand;
import com.videostore.videostore.application.port.in.review.RemoveReviewUseCase;
import com.videostore.videostore.domain.exception.conflict.ReviewNotRemovableException;
import com.videostore.videostore.domain.exception.notfound.ReviewNotFoundException;
import com.videostore.videostore.domain.exception.notfound.UserNotFoundException;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.review.Review;
import com.videostore.videostore.domain.model.review.valueobject.ReviewId;
import com.videostore.videostore.domain.model.user.Role;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.Username;
import com.videostore.videostore.domain.repository.ReviewRepository;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class RemoveReviewUseCaseImpl implements RemoveReviewUseCase {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public RemoveReviewUseCaseImpl(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void execute(RemoveReviewCommand command) {
        Username username = new Username((command.username()));
        ReviewId reviewId = new ReviewId(command.reviewId());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username.value()));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId.value()));

        validateRemoval(review, user);

        reviewRepository.removeReview(review.getId());
    }

    private void validateRemoval(Review review, User user) {
        if (user.getRole() != Role.ADMIN && !Objects.equals(review.getUserId().value(), user.getId().value())) {
            throw new ReviewNotRemovableException(review.getId().value(), user.getId().value());
        }
    }
}
