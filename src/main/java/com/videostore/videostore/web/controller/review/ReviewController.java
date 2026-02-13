package com.videostore.videostore.web.controller.review;

import com.videostore.videostore.application.command.review.AddReviewCommand;
import com.videostore.videostore.application.command.review.RemoveReviewCommand;
import com.videostore.videostore.application.port.in.review.AddReviewUseCase;
import com.videostore.videostore.application.port.in.review.GetReviewsByMovieUseCase;
import com.videostore.videostore.application.port.in.review.RemoveReviewUseCase;
import com.videostore.videostore.domain.model.review.Review;
import com.videostore.videostore.web.controller.review.dto.request.AddReviewRequest;
import com.videostore.videostore.web.controller.review.dto.response.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@Tag(name = "Reviews", description = "Operations related to movie reviews")
public class ReviewController {

    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

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

    @Operation(summary = "Add a review by the authenticated user to a movie")
    @PostMapping("/reviews")
    @CacheEvict(value = "reviewsByMovie", key = "#request.movieId")
    public ResponseEntity<ReviewResponse> addReview(@RequestBody @Valid AddReviewRequest request, Authentication authentication) {
        log.info("User {} requested to add a review to movie {}", authentication.getName(), request.movieId());

        AddReviewCommand command = new AddReviewCommand(authentication.getName(), request.movieId(), request.rating(), request.comment());
        Review review = addReviewUseCase.execute(command);

        log.info("User {} successfully added review to movie {}", authentication.getName(), request.movieId());

        ReviewResponse response = ReviewResponse.fromDomain(review);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Remove a review by the authenticated user from a movie")
    @DeleteMapping("/reviews/{movieId}")
    @CacheEvict(value = "reviewsByMovie", key = "#movieId")
    public ResponseEntity<Void> removeReview(@PathVariable @Positive Long movieId, Authentication authentication) {
        log.info("User {} requested to remove their review from movie {}", authentication.getName(), movieId);

        RemoveReviewCommand command = new RemoveReviewCommand(authentication.getName(), movieId);
        removeReviewUseCase.execute(command);

        log.info("User {} successfully removed their review from movie {}", authentication.getName(), movieId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all the reviews for a movie")
    @GetMapping("/movies/{movieId}/reviews")
    @Cacheable(value = "reviewsByMovie", key = "#movieId")
    public ResponseEntity<List<ReviewResponse>> getReviewsByMovie(@PathVariable @Positive Long movieId) {
        log.info("Request received to get all reviews for movie {}", movieId);

        List<ReviewResponse> response = getReviewsByMovieUseCase.execute(movieId)
                .stream().map(ReviewResponse::fromDomain).toList();

        log.info("Successfully retrieved {} reviews for movie {}", response.size(), movieId);

        return ResponseEntity.ok(response);
    }
}
