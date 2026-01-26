package com.videostore.videostore.domain.model.movie.valueobject;

import java.time.LocalDate;

public record Year(int value) {

    public Year {
        int currentYear = LocalDate.now().getYear();
        if (value < 1895 || value > currentYear) {
            throw new IllegalArgumentException("Movie year must be between 1895 and " + currentYear);
        }
    }
}
