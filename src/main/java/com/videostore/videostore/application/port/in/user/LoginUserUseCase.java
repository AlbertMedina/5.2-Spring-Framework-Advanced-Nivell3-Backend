package com.videostore.videostore.application.port.in.user;

import com.videostore.videostore.application.command.user.LoginUserCommand;
import com.videostore.videostore.domain.model.user.User;

public interface LoginUserUseCase {
    User execute(LoginUserCommand command);
}
