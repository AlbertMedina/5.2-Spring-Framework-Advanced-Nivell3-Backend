package com.videostore.videostore.infrastructure.persistence.mapper;

import com.videostore.videostore.domain.model.user.Role;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.*;
import com.videostore.videostore.infrastructure.persistence.entity.UserEntity;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserEntity toEntity(User user) {
        return new UserEntity(
                user.getName().value(),
                user.getSurname().value(),
                user.getUsername().value(),
                user.getEmail().value(),
                user.getPassword().value(),
                user.getRole()
        );
    }

    public static User toDomain(UserEntity entity) {
        return User.create(
                new UserId(entity.getId()),
                new Name(entity.getName()),
                new Surname(entity.getSurname()),
                new Username(entity.getUsername()),
                new Email(entity.getEmail()),
                new Password(entity.getPassword()),
                entity.getRole()
        );
    }
}
