package com.videostore.videostore.application.usecase.user;

import com.videostore.videostore.application.command.user.RegisterUserCommand;
import com.videostore.videostore.application.port.in.user.RegisterUserUseCase;
import com.videostore.videostore.domain.exception.EmailAlreadyExistsException;
import com.videostore.videostore.domain.exception.UsernameAlreadyExistsException;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.*;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepository userRepository;

    public RegisterUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User execute(RegisterUserCommand command) {
        validateRegisterUser(command.username(), command.email());

        User user = User.create(
                new Name(command.name()),
                new Surname(command.surname()),
                new Username(command.username()),
                new Email(command.email()),
                new Password(command.password())
        );

        return userRepository.registerUser(user);
    }

    private void validateRegisterUser(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }
}
