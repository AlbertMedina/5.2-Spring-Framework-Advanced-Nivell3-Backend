package com.videostore.videostore.application.port.in.auth;

import com.videostore.videostore.application.command.auth.LoginUserCommand;
import com.videostore.videostore.domain.model.user.User;

public interface LoginUserUseCase {
    User execute(LoginUserCommand command);
}
