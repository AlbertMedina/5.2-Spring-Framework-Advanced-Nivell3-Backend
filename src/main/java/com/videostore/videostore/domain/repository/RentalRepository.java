package com.videostore.videostore.domain.repository;

import com.videostore.videostore.domain.model.rental.Rental;

import java.util.List;
import java.util.Optional;

public interface RentalRepository {
    
    Optional<Rental> findByUserIdAndMovieId(Long userId, Long movieId);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    List<Rental> findAllByUser(Long userId);

    List<Rental> findAllByMovie(Long movieId);

    int activeRentalsByMovie(Long movieId);

    Rental addRental(Rental rental);

    void removeRental(Rental rental);
}
