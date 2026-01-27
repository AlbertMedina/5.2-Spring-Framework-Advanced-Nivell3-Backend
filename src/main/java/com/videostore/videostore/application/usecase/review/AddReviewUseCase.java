package com.videostore.videostore.application.usecase.review;

import com.videostore.videostore.application.command.review.AddReviewCommand;
import com.videostore.videostore.domain.exception.*;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.review.Review;
import com.videostore.videostore.domain.model.review.valueobject.Comment;
import com.videostore.videostore.domain.model.review.valueobject.Rating;
import com.videostore.videostore.domain.model.review.valueobject.ReviewDate;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.RentalRepository;
import com.videostore.videostore.domain.repository.ReviewRepository;
import com.videostore.videostore.domain.repository.UserRepository;

import java.time.LocalDate;

public class AddReviewUseCase {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final RentalRepository rentalRepository;

    public AddReviewUseCase(ReviewRepository reviewRepository, MovieRepository movieRepository, UserRepository userRepository, RentalRepository rentalRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.rentalRepository = rentalRepository;
    }

    public Review execute(AddReviewCommand addReviewCommand) {
        Long userId = addReviewCommand.userId();
        Long movieId = addReviewCommand.movieId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        if (!rentalRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new RentalNotFoundException("Users must have the movie rented to add a review");
        }

        if (reviewRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new MovieAlreadyReviewedException(userId, movieId);
        }

        return reviewRepository.addReview(new Review(user, movie, new Rating(addReviewCommand.rating()), new Comment(addReviewCommand.comment()), new ReviewDate(LocalDate.now())));
    }
}
