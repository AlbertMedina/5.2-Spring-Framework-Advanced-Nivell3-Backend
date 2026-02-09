package com.videostore.videostore.application.usecase.user;

import com.videostore.videostore.application.port.in.user.GetUserUseCase;
import com.videostore.videostore.domain.exception.notfound.UserNotFoundException;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class GetUserUseCaseImpl implements GetUserUseCase {

    private final UserRepository userRepository;

    public GetUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User execute(Long id) {
        return userRepository.findById(new UserId(id))
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
