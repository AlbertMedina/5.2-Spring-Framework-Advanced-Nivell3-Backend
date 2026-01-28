package com.videostore.videostore.application.usecase.user;

import com.videostore.videostore.application.command.user.LoginUserCommand;
import com.videostore.videostore.application.port.in.user.LoginUserUseCase;
import com.videostore.videostore.domain.exception.InvalidCredentialsException;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginUserUseCaseImpl implements LoginUserUseCase {

    private final UserRepository userRepository;

    public LoginUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public User execute(LoginUserCommand command) {
        User user = userRepository.findByUsername(command.usernameOrEmail())
                .or(() -> userRepository.findByEmail(command.usernameOrEmail()))
                .orElseThrow(InvalidCredentialsException::new);

        if (!command.password().equals(user.getPassword().value())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }
}
