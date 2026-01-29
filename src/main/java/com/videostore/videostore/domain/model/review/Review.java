package com.videostore.videostore.domain.model.review;

import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.review.valueobject.Comment;
import com.videostore.videostore.domain.model.review.valueobject.Rating;
import com.videostore.videostore.domain.model.review.valueobject.ReviewDate;
import com.videostore.videostore.domain.model.review.valueobject.ReviewId;
import com.videostore.videostore.domain.model.user.valueobject.UserId;

public class Review {

    private final ReviewId reviewId;
    private final UserId userId;
    private final MovieId movieId;
    private final Rating rating;
    private final Comment comment;
    private final ReviewDate reviewDate;

    public Review(ReviewId reviewId, UserId userId, MovieId movieId, Rating rating, Comment comment, ReviewDate reviewDate) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    public ReviewId getReviewId() {
        return reviewId;
    }

    public UserId getUserId() {
        return userId;
    }

    public MovieId getMovieId() {
        return movieId;
    }

    public Rating getRating() {
        return rating;
    }

    public Comment getComment() {
        return comment;
    }

    public ReviewDate getReviewDate() {
        return reviewDate;
    }

    public static Review create(ReviewId reviewId, UserId userId, MovieId movieId, Rating rating, Comment comment, ReviewDate reviewDate) {
        return new Review(reviewId, userId, movieId, rating, comment, reviewDate);
    }
}
