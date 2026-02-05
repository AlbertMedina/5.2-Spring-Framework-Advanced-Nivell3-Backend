package com.videostore.videostore.application.usecase.auth;

import com.videostore.videostore.application.auth.LoginIdentifier;
import com.videostore.videostore.application.command.auth.LoginUserCommand;
import com.videostore.videostore.application.port.in.auth.LoginUserUseCase;
import com.videostore.videostore.domain.exception.validation.InvalidCredentialsException;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.Email;
import com.videostore.videostore.domain.model.user.valueobject.Username;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginUserUseCaseImpl implements LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginUserUseCaseImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public User execute(LoginUserCommand command) {

        try {
            LoginIdentifier loginIdentifier = new LoginIdentifier(command.loginIdentifier());

            User user = (loginIdentifier.isEmail()
                    ? userRepository.findByEmail(new Email(loginIdentifier.value()))
                    : userRepository.findByUsername(new Username(loginIdentifier.value())))
                    .orElseThrow(InvalidCredentialsException::new);

            if (!passwordEncoder.matches(command.password(), user.getPassword().value())) {
                throw new InvalidCredentialsException();
            }

            return user;

        } catch (IllegalArgumentException e) {
            throw new InvalidCredentialsException();
        }
    }
}
