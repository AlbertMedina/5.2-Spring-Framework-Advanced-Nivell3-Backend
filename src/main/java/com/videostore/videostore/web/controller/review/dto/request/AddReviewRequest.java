package com.videostore.videostore.web.controller.review.dto.request;

import jakarta.validation.constraints.*;

public record AddReviewRequest(
        @NotNull @Positive Long movieId,
        @Min(1) @Max(5) int rating,
        @NotNull @NotBlank String comment
) {
}
