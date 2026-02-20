package com.videostore.videostore.infrastructure.persistence.adapter;

import com.videostore.videostore.domain.common.RatingSummary;
import com.videostore.videostore.domain.exception.notfound.MovieNotFoundException;
import com.videostore.videostore.domain.exception.notfound.UserNotFoundException;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.review.Review;
import com.videostore.videostore.domain.model.review.valueobject.ReviewId;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.repository.ReviewRepository;
import com.videostore.videostore.infrastructure.persistence.entity.MovieEntity;
import com.videostore.videostore.infrastructure.persistence.entity.ReviewEntity;
import com.videostore.videostore.infrastructure.persistence.entity.UserEntity;
import com.videostore.videostore.infrastructure.persistence.mapper.ReviewMapper;
import com.videostore.videostore.infrastructure.persistence.repository.MovieRepositoryJPA;
import com.videostore.videostore.infrastructure.persistence.repository.ReviewRepositoryJPA;
import com.videostore.videostore.infrastructure.persistence.repository.UserRepositoryJPA;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ReviewRepositoryImpl implements ReviewRepository {

    private final ReviewRepositoryJPA reviewRepositoryJPA;
    private final UserRepositoryJPA userRepositoryJPA;
    private final MovieRepositoryJPA movieRepositoryJPA;

    public ReviewRepositoryImpl(ReviewRepositoryJPA reviewRepositoryJPA, UserRepositoryJPA userRepositoryJPA, MovieRepositoryJPA movieRepositoryJPA) {
        this.reviewRepositoryJPA = reviewRepositoryJPA;
        this.userRepositoryJPA = userRepositoryJPA;
        this.movieRepositoryJPA = movieRepositoryJPA;
    }

    @Override
    public Optional<Review> findById(ReviewId reviewId) {
        return reviewRepositoryJPA.findById(reviewId.value())
                .map(ReviewMapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndMovieId(UserId userId, MovieId movieId) {
        return reviewRepositoryJPA.existsByUserIdAndMovieId(userId.value(), movieId.value());
    }

    @Override
    public List<Review> findAllByMovie(MovieId movieId) {
        return reviewRepositoryJPA.findAllByMovieId(movieId.value())
                .stream().map(ReviewMapper::toDomain).toList();
    }

    @Override
    public Review addReview(Review review) {
        UserEntity userEntity = getUserEntity(review.getUserId());
        MovieEntity movieEntity = getMovieEntity(review.getMovieId());

        ReviewEntity entity = ReviewMapper.toEntity(review, userEntity, movieEntity);

        return ReviewMapper.toDomain(reviewRepositoryJPA.save(entity));
    }

    @Override
    public void removeReview(ReviewId reviewId) {
        reviewRepositoryJPA.deleteById(reviewId.value());
    }

    @Override
    public Optional<RatingSummary> getAverageRatingByMovieId(MovieId movieId) {
        Object result = reviewRepositoryJPA.findAverageRatingByMovieId(movieId.value());

        if (result instanceof Object[] resultArr && resultArr.length == 2) {
            Number avg = (Number) resultArr[0];
            Number cnt = (Number) resultArr[1];

            double average = (avg != null) ? avg.doubleValue() : 0.0;
            int count = (cnt != null) ? cnt.intValue() : 0;

            return Optional.of(new RatingSummary(average, count));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Map<Long, RatingSummary> getAverageRatingsByMovieIds(List<MovieId> movieIds) {
        List<Object> results = reviewRepositoryJPA.findAverageRatingsByMovieIds(movieIds.stream().map(MovieId::value).toList());

        Map<Long, RatingSummary> ratingsMap = new HashMap<>();

        for (Object result : results) {
            if (result instanceof Object[] resultArr && resultArr.length == 3) {
                Number movieIdNum = (Number) resultArr[0];
                Number avg = (Number) resultArr[1];
                Number cnt = (Number) resultArr[2];

                long movieId = (movieIdNum != null) ? movieIdNum.longValue() : 0L;
                double average = (avg != null) ? avg.doubleValue() : 0.0;
                int count = (cnt != null) ? cnt.intValue() : 0;

                ratingsMap.put(movieId, new RatingSummary(average, count));
            }
        }

        return ratingsMap;
    }

    private UserEntity getUserEntity(UserId userId) {
        return userRepositoryJPA.findById(userId.value())
                .orElseThrow(() -> new UserNotFoundException(userId.value()));
    }

    private MovieEntity getMovieEntity(MovieId movieId) {
        return movieRepositoryJPA.findById(movieId.value())
                .orElseThrow(() -> new MovieNotFoundException(movieId.value()));
    }
}
