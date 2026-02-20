package com.videostore.videostore.domain.repository;

import com.videostore.videostore.domain.common.RatingSummary;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.review.Review;
import com.videostore.videostore.domain.model.review.valueobject.ReviewId;
import com.videostore.videostore.domain.model.user.valueobject.UserId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReviewRepository {

    Optional<Review> findById(ReviewId reviewId);

    boolean existsByUserIdAndMovieId(UserId userId, MovieId movieId);

    List<Review> findAllByMovie(MovieId movieId);

    Review addReview(Review review);

    void removeReview(ReviewId reviewId);

    Optional<RatingSummary> getAverageRatingByMovieId(MovieId movieId);

    Map<Long, RatingSummary> getAverageRatingsByMovieIds(List<MovieId> movieIds);
}
