package com.videostore.videostore.application.usecase.review;

import com.videostore.videostore.application.command.review.AddReviewCommand;
import com.videostore.videostore.application.model.ReviewDetails;
import com.videostore.videostore.application.port.in.review.AddReviewUseCase;
import com.videostore.videostore.domain.exception.conflict.MovieAlreadyReviewedException;
import com.videostore.videostore.domain.exception.conflict.MovieNotRentedException;
import com.videostore.videostore.domain.exception.notfound.MovieNotFoundException;
import com.videostore.videostore.domain.exception.notfound.UserNotFoundException;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.review.Review;
import com.videostore.videostore.domain.model.review.valueobject.Comment;
import com.videostore.videostore.domain.model.review.valueobject.Rating;
import com.videostore.videostore.domain.model.review.valueobject.ReviewDate;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.model.user.valueobject.Username;
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
    public ReviewDetails execute(AddReviewCommand command) {
        Username username = new Username(command.username());
        MovieId movieId = new MovieId(command.movieId());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username.value()));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId.value()));

        validateReview(user.getId(), movieId);

        Review review = Review.create(
                null,
                user.getId(),
                movie.getId(),
                new Rating(command.rating()),
                new Comment(command.comment()),
                new ReviewDate(LocalDate.now())
        );

        Review newReview = reviewRepository.addReview(review);

        return new ReviewDetails(
                newReview.getId().value(),
                newReview.getRating().value(),
                newReview.getComment().value(),
                newReview.getReviewDate().value(),
                username.value()
        );
    }

    private void validateReview(UserId userId, MovieId movieId) {
        if (!rentalRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new MovieNotRentedException(userId.value(), movieId.value());
        }

        if (reviewRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new MovieAlreadyReviewedException(userId.value(), movieId.value());
        }
    }
}
