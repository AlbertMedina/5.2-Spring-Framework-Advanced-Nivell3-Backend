package com.videostore.videostore.application.usecase.user;

import com.videostore.videostore.application.port.in.user.GetAllUsersUseCase;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllUsersUseCaseImpl implements GetAllUsersUseCase {

    private final UserRepository userRepository;

    public GetAllUsersUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> execute() {
        return userRepository.findAll();
    }
}
