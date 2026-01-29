package com.videostore.videostore.infrastructure.persistence.adapter;

import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.review.Review;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.repository.ReviewRepository;

import java.util.List;
import java.util.Optional;

public class ReviewRepositoryImpl implements ReviewRepository {
    
    @Override
    public Optional<Review> findByUserIdAndMovieId(UserId userId, MovieId movieId) {
        return Optional.empty();
    }

    @Override
    public List<Review> findAllByMovie(MovieId movieId) {
        return List.of();
    }

    @Override
    public boolean existsByUserIdAndMovieId(UserId userId, MovieId movieId) {
        return false;
    }

    @Override
    public Review addReview(Review review) {
        return null;
    }

    @Override
    public void removeReview(Review review) {

    }
}
