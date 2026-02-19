package com.videostore.videostore.application.usecase.rental;

import com.videostore.videostore.application.port.in.rental.UserHasRentedMovieUseCase;
import com.videostore.videostore.domain.exception.notfound.UserNotFoundException;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.Username;
import com.videostore.videostore.domain.repository.RentalRepository;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserHasRentedMovieUseCaseImpl implements UserHasRentedMovieUseCase {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public UserHasRentedMovieUseCaseImpl(RentalRepository rentalRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public boolean execute(String username, Long movieId) {
        Username usernameVO = new Username(username);
        User user = userRepository.findByUsername(usernameVO)
                .orElseThrow(() -> new UserNotFoundException(username));

        return rentalRepository.existsByUserIdAndMovieId(user.getId(), new MovieId(movieId));
    }
}
