package com.videostore.videostore.application.usecase.rental;

import com.videostore.videostore.application.model.RentalDetails;
import com.videostore.videostore.application.port.in.rental.GetRentalsByMovieUseCase;
import com.videostore.videostore.domain.exception.notfound.MovieNotFoundException;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.RentalRepository;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GetRentalsByMovieUseCaseImpl implements GetRentalsByMovieUseCase {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public GetRentalsByMovieUseCaseImpl(RentalRepository rentalRepository, UserRepository userRepository, MovieRepository movieRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDetails> execute(Long movieId) {
        Movie movie = movieRepository.findById(new MovieId(movieId))
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        List<Rental> rentals = rentalRepository.findAllByMovie(movie.getId());

        List<UserId> userIds = rentals.stream().map(Rental::getUserId).toList();

        List<User> users = userRepository.findAllByIds(userIds);

        Map<UserId, String> userIdToUsername = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u.getUsername().value()));

        return rentals.stream()
                .map(r -> new RentalDetails(
                        r.getId().value(),
                        r.getRentalDate().value(),
                        userIdToUsername.getOrDefault(r.getUserId(), "Unknown"),
                        movie.getId().value(),
                        movie.getTitle().value())
                )
                .toList();
    }
}
