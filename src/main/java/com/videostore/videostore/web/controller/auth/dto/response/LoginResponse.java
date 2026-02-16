package com.videostore.videostore.web.controller.auth.dto.response;

import com.videostore.videostore.domain.model.user.Role;

public record LoginResponse(
        String token,
        Role role
) {
}
