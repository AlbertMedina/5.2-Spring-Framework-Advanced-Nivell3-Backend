package com.videostore.videostore.web.controller.review;

import com.videostore.videostore.application.command.review.AddReviewCommand;
import com.videostore.videostore.application.command.review.RemoveReviewCommand;
import com.videostore.videostore.application.port.in.review.AddReviewUseCase;
import com.videostore.videostore.application.port.in.review.GetReviewsByMovieUseCase;
import com.videostore.videostore.application.port.in.review.RemoveReviewUseCase;
import com.videostore.videostore.domain.model.review.Review;
import com.videostore.videostore.web.controller.review.dto.request.AddReviewRequest;
import com.videostore.videostore.web.controller.review.dto.response.ReviewResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
public class ReviewController {

    private final AddReviewUseCase addReviewUseCase;
    private final RemoveReviewUseCase removeReviewUseCase;
    private final GetReviewsByMovieUseCase getReviewsByMovieUseCase;

    public ReviewController(
            AddReviewUseCase addReviewUseCase,
            RemoveReviewUseCase removeReviewUseCase,
            GetReviewsByMovieUseCase getReviewsByMovieUseCase
    ) {
        this.addReviewUseCase = addReviewUseCase;
        this.removeReviewUseCase = removeReviewUseCase;
        this.getReviewsByMovieUseCase = getReviewsByMovieUseCase;
    }

    @PostMapping("/reviews")
    public ResponseEntity<ReviewResponse> addReview(@RequestBody @Valid AddReviewRequest request, Authentication authentication) {
        AddReviewCommand command = new AddReviewCommand(authentication.getName(), request.movieId(), request.rating(), request.comment());
        Review review = addReviewUseCase.execute(command);

        ReviewResponse response = ReviewResponse.fromDomain(review);
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/reviews/{movieId}")
    public ResponseEntity<Void> removeReview(@PathVariable @Positive Long movieId, Authentication authentication) {
        RemoveReviewCommand command = new RemoveReviewCommand(authentication.getName(), movieId);
        removeReviewUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/movies/{movieId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviewsByMovie(@PathVariable @Positive Long movieId) {
        List<ReviewResponse> response = getReviewsByMovieUseCase.execute(movieId)
                .stream().map(ReviewResponse::fromDomain).toList();

        return ResponseEntity.ok(response);
    }
}
