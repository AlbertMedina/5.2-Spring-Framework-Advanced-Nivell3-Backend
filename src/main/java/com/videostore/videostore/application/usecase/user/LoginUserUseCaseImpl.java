package com.videostore.videostore.application.usecase.user;

import com.videostore.videostore.application.auth.LoginIdentifier;
import com.videostore.videostore.application.command.user.LoginUserCommand;
import com.videostore.videostore.application.port.in.user.LoginUserUseCase;
import com.videostore.videostore.domain.exception.validation.InvalidCredentialsException;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.Email;
import com.videostore.videostore.domain.model.user.valueobject.Username;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LoginUserUseCaseImpl implements LoginUserUseCase {

    private final UserRepository userRepository;

    public LoginUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public User execute(LoginUserCommand command) {

        try {
            LoginIdentifier loginIdentifier = new LoginIdentifier(command.usernameOrEmail());

            User user = (loginIdentifier.isEmail()
                    ? userRepository.findByEmail(new Email(loginIdentifier.value()))
                    : userRepository.findByUsername(new Username(loginIdentifier.value())))
                    .orElseThrow(InvalidCredentialsException::new);

            if (!command.password().equals(user.getPassword().value())) {
                throw new InvalidCredentialsException();
            }

            return user;

        } catch (IllegalArgumentException e) {
            throw new InvalidCredentialsException();
        }
    }
}
