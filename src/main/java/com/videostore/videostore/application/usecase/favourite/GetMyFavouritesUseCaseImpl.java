package com.videostore.videostore.application.usecase.favourite;

import com.videostore.videostore.application.port.in.favourite.GetMyFavouritesUseCase;
import com.videostore.videostore.domain.exception.notfound.UserNotFoundException;
import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.Username;
import com.videostore.videostore.domain.repository.FavouriteRepository;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetMyFavouritesUseCaseImpl implements GetMyFavouritesUseCase {

    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;

    public GetMyFavouritesUseCaseImpl(FavouriteRepository favouriteRepository, UserRepository userRepository) {
        this.favouriteRepository = favouriteRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favourite> execute(String username) {
        Username usernameVo = new Username(username);
        User user = userRepository.findByUsername(usernameVo)
                .orElseThrow(() -> new UserNotFoundException(username));
        return favouriteRepository.findAllByUser(user.getId());
    }
}
