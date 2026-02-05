package com.videostore.videostore.application.port.in.auth;

import com.videostore.videostore.application.command.auth.RegisterUserCommand;
import com.videostore.videostore.domain.model.user.User;

public interface RegisterUserUseCase {
    User execute(RegisterUserCommand command);
}
