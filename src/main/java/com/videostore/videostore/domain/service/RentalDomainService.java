package com.videostore.videostore.domain.service;

import com.videostore.videostore.domain.exception.BusinessRuleViolationException;
import com.videostore.videostore.domain.model.movie.Movie;

public class RentalDomainService {

    public void validateRental(Movie movie, boolean hasRentedMovie, int totalActiveRentalsOfMovie) {
        if (hasRentedMovie) {
            throw new BusinessRuleViolationException("User has already rented this movie");
        }

        if (totalActiveRentalsOfMovie >= movie.getNumberOfCopies().value()) {
            throw new BusinessRuleViolationException("No copies available for this movie");
        }
    }

    public void validateReturn(boolean hasRentedMovie) {
        if (!hasRentedMovie) {
            throw new BusinessRuleViolationException("User cannot return a movie they haven't rented");
        }
    }
}
