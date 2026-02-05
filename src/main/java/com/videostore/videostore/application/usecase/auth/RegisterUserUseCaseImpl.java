package com.videostore.videostore.application.usecase.auth;

import com.videostore.videostore.application.command.auth.RegisterUserCommand;
import com.videostore.videostore.application.port.in.auth.RegisterUserUseCase;
import com.videostore.videostore.domain.exception.conflict.EmailAlreadyExistsException;
import com.videostore.videostore.domain.exception.conflict.UsernameAlreadyExistsException;
import com.videostore.videostore.domain.model.user.Role;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.*;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCaseImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
                new Password(passwordEncoder.encode(command.password())),
                Role.USER
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
