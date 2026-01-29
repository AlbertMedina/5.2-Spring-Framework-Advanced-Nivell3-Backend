package com.videostore.videostore.infrastructure.persistence.adapter;

import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.Email;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.model.user.valueobject.Username;
import com.videostore.videostore.domain.repository.UserRepository;
import com.videostore.videostore.infrastructure.persistence.entity.UserEntity;
import com.videostore.videostore.infrastructure.persistence.mapper.UserMapper;
import com.videostore.videostore.infrastructure.persistence.repository.UserRepositoryJPA;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserRepositoryJPA userRepositoryJPA;

    public UserRepositoryImpl(UserRepositoryJPA userRepositoryJPA) {
        this.userRepositoryJPA = userRepositoryJPA;
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userRepositoryJPA.findById(id.value())
                .map(UserMapper::toDomain);
    }

    @Override
    public boolean existsById(UserId id) {
        return userRepositoryJPA.existsById(id.value());
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        return userRepositoryJPA.findByUsername(username.value())
                .map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(Username username) {
        return userRepositoryJPA.existsByUsername(username.value());
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userRepositoryJPA.findByEmail(email.value())
                .map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userRepositoryJPA.existsByEmail(email.value());
    }

    @Override
    public User registerUser(User user) {
        UserEntity entity = UserMapper.toEntity(user);

        return UserMapper.toDomain(userRepositoryJPA.save(entity));
    }

    @Override
    public void removeUser(UserId userId) {
        userRepositoryJPA.deleteById(userId.value());
    }
}
