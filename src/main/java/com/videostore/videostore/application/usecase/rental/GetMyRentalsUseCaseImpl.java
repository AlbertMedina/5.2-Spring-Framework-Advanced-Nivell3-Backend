package com.videostore.videostore.application.usecase.rental;

import com.videostore.videostore.application.model.RentalDetails;
import com.videostore.videostore.application.port.in.rental.GetMyRentalsUseCase;
import com.videostore.videostore.domain.exception.notfound.UserNotFoundException;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.Username;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.RentalRepository;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GetMyRentalsUseCaseImpl implements GetMyRentalsUseCase {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public GetMyRentalsUseCaseImpl(RentalRepository rentalRepository, UserRepository userRepository, MovieRepository movieRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDetails> execute(String username) {
        User user = userRepository.findByUsername(new Username(username))
                .orElseThrow(() -> new UserNotFoundException(username));

        List<Rental> rentals = rentalRepository.findAllByUser(user.getId());

        List<MovieId> movieIds = rentals.stream().map(Rental::getMovieId).toList();

        List<Movie> movies = movieRepository.findAllByIds(movieIds);

        Map<MovieId, String> movieIdToTitle = movies.stream()
                .collect(Collectors.toMap(Movie::getId, m -> m.getTitle().value()));

        return rentals.stream()
                .map(r -> new RentalDetails(r, user.getUsername().value(), movieIdToTitle.getOrDefault(r.getMovieId(), "Unknown")))
                .toList();
    }
}

