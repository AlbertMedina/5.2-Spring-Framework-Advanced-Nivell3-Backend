package com.videostore.videostore.application.usecase.favourite;

import com.videostore.videostore.application.port.in.favourite.UserHasFavouriteMovieUseCase;
import com.videostore.videostore.domain.exception.notfound.UserNotFoundException;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.Username;
import com.videostore.videostore.domain.repository.FavouriteRepository;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserHasFavouriteMovieUseCaseImpl implements UserHasFavouriteMovieUseCase {

    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;

    public UserHasFavouriteMovieUseCaseImpl(FavouriteRepository favouriteRepository, UserRepository userRepository) {
        this.favouriteRepository = favouriteRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public boolean execute(String username, Long movieId) {
        Username usernameVO = new Username(username);
        User user = userRepository.findByUsername(usernameVO)
                .orElseThrow(() -> new UserNotFoundException(username));

        return favouriteRepository.existsByUserIdAndMovieId(user.getId(), new MovieId(movieId));
    }
}
