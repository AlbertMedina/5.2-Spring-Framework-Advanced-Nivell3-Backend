package com.videostore.videostore.domain.repository;

import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.review.Review;
import com.videostore.videostore.domain.model.user.valueobject.UserId;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    Optional<Review> findByUserIdAndMovieId(UserId userId, MovieId movieId);

    List<Review> findAllByMovie(MovieId movieId);

    boolean existsByUserIdAndMovieId(UserId userId, MovieId movieId);

    Review addReview(Review review);

    void removeReview(Review review);
}
