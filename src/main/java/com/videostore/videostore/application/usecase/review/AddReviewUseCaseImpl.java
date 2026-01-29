package com.videostore.videostore.application.usecase.review;

import com.videostore.videostore.application.command.review.AddReviewCommand;
import com.videostore.videostore.application.port.in.review.AddReviewUseCase;
import com.videostore.videostore.domain.exception.*;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.review.Review;
import com.videostore.videostore.domain.model.review.valueobject.Comment;
import com.videostore.videostore.domain.model.review.valueobject.Rating;
import com.videostore.videostore.domain.model.review.valueobject.ReviewDate;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.RentalRepository;
import com.videostore.videostore.domain.repository.ReviewRepository;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AddReviewUseCaseImpl implements AddReviewUseCase {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final RentalRepository rentalRepository;

    public AddReviewUseCaseImpl(ReviewRepository reviewRepository, MovieRepository movieRepository, UserRepository userRepository, RentalRepository rentalRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional
    public Review execute(AddReviewCommand command) {
        Long userId = command.userId();
        Long movieId = command.movieId();

        User user = userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));

        Movie movie = movieRepository.findById(new MovieId(movieId))
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        validateReview(userId, movieId);

        Review review = Review.create(
                user.getId(),
                movie.getId(),
                new Rating(command.rating()),
                new Comment(command.comment()),
                new ReviewDate(LocalDate.now())
        );

        return reviewRepository.addReview(review);
    }

    private void validateReview(Long userId, Long movieId) {
        if (!rentalRepository.existsByUserIdAndMovieId(new UserId(userId), new MovieId(movieId))) {
            throw new RentalNotFoundException("Users must have the movie rented to add a review");
        }

        if (reviewRepository.existsByUserIdAndMovieId(new UserId(userId), new MovieId(movieId))) {
            throw new MovieAlreadyReviewedException(userId, movieId);
        }
    }
}
