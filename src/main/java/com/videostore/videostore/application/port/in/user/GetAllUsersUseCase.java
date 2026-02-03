package com.videostore.videostore.application.port.in.user;

import com.videostore.videostore.domain.model.user.User;

import java.util.List;

public interface GetAllUsersUseCase {
    List<User> execute();
}
