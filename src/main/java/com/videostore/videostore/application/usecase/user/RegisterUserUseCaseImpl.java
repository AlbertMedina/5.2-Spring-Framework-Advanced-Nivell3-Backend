package com.videostore.videostore.application.usecase.user;

import com.videostore.videostore.application.command.user.RegisterUserCommand;
import com.videostore.videostore.application.port.in.user.RegisterUserUseCase;
import com.videostore.videostore.domain.exception.conflict.EmailAlreadyExistsException;
import com.videostore.videostore.domain.exception.conflict.UsernameAlreadyExistsException;
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
        Username username = new Username(command.username());
        Email email = new Email(command.email());

        validateRegisterUser(username, email);

        User user = User.create(
                null,
                new Name(command.name()),
                new Surname(command.surname()),
                username,
                email,
                new Password(command.password())
        );

        return userRepository.registerUser(user);
    }

    private void validateRegisterUser(Username username, Email email) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username.value());
        }

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email.value());
        }
    }
}
