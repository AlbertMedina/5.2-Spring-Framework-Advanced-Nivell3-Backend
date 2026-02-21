package com.videostore.videostore.application.model;

import com.videostore.videostore.domain.common.RatingSummary;

public record MovieDetails(
        Long id,
        String title,
        int year,
        String genre,
        int duration,
        String director,
        String synopsis,
        int numberOfCopies,
        String posterUrl,
        RatingSummary rating
) {
}
