package com.videostore.videostore.application.port.in.user;

import com.videostore.videostore.application.command.user.RegisterUserCommand;
import com.videostore.videostore.domain.model.user.User;

public interface RegisterUserUseCase {
    User execute(RegisterUserCommand command);
}
