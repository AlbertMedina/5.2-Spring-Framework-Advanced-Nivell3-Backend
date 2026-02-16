package com.videostore.videostore.web.controller.auth.dto.response;

import com.videostore.videostore.domain.model.user.Role;
import com.videostore.videostore.domain.model.user.User;

public record AuthUserResponse(
        Long id,
        String username,
        Role role
) {
    public static AuthUserResponse fromDomain(User user) {
        return new AuthUserResponse(
                user.getId().value(),
                user.getUsername().value(),
                user.getRole()
        );
    }
}
