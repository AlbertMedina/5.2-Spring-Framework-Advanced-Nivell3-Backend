package com.videostore.videostore.application.usecase.review;

import com.videostore.videostore.application.model.ReviewDetails;
import com.videostore.videostore.application.port.in.review.GetReviewsByMovieUseCase;
import com.videostore.videostore.domain.exception.notfound.MovieNotFoundException;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.review.Review;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.ReviewRepository;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GetReviewsByMovieUseCaseImpl implements GetReviewsByMovieUseCase {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public GetReviewsByMovieUseCaseImpl(ReviewRepository reviewRepository,
                                        UserRepository userRepository,
                                        MovieRepository movieRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDetails> execute(Long movieId) {
        Movie movie = movieRepository.findById(new MovieId(movieId))
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        List<Review> reviews = reviewRepository.findAllByMovie(movie.getId());

        List<UserId> userIds = reviews.stream().map(Review::getUserId).toList();

        List<User> users = userRepository.findAllByIds(userIds);

        Map<UserId, String> userIdToUsername = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u.getUsername().value()));

        return reviews.stream()
                .map(r -> new ReviewDetails(
                        r.getId().value(),
                        r.getRating().value(),
                        r.getComment().value(),
                        r.getReviewDate().value(),
                        userIdToUsername.getOrDefault(r.getUserId(), "Unknown"))
                )
                .toList();
    }
}
