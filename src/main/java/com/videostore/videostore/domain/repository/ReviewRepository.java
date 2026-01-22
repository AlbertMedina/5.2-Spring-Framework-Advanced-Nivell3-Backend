package com.videostore.videostore.domain.repository;

import com.videostore.videostore.domain.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    Optional<Review> findById(Long id);

    List<Review> findByMovie(Long movieId);

    Review save(Review review);

    void deleteById(Long id);
}
