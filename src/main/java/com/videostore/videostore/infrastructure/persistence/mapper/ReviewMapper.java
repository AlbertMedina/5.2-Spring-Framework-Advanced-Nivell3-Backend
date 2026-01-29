package com.videostore.videostore.infrastructure.persistence.mapper;

import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.review.Review;
import com.videostore.videostore.domain.model.review.valueobject.Comment;
import com.videostore.videostore.domain.model.review.valueobject.Rating;
import com.videostore.videostore.domain.model.review.valueobject.ReviewDate;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.infrastructure.persistence.entity.MovieEntity;
import com.videostore.videostore.infrastructure.persistence.entity.ReviewEntity;
import com.videostore.videostore.infrastructure.persistence.entity.UserEntity;

public final class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewEntity toEntity(Review review, UserEntity userEntity, MovieEntity movieEntity) {
        return new ReviewEntity(
                userEntity,
                movieEntity,
                review.getRating().value(),
                review.getComment().value(),
                review.getReviewDate().value()
        );
    }

    public static Review toDomain(ReviewEntity entity) {
        return Review.create(
                new UserId(entity.getUser().getId()),
                new MovieId(entity.getMovie().getId()),
                new Rating(entity.getRating()),
                new Comment(entity.getComment()),
                new ReviewDate(entity.getReviewDate())
        );
    }
}
