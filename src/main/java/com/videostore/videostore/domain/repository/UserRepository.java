package com.videostore.videostore.domain.repository;

import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.Email;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.model.user.valueobject.Username;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(UserId id);

    boolean existsById(UserId id);

    Optional<User> findByUsername(Username username);

    boolean existsByUsername(Username username);

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);

    User registerUser(User user);

    void removeUser(UserId userId);
}
