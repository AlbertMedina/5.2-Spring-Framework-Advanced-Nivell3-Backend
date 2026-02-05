package com.videostore.videostore.application.command.auth;

public record RegisterUserCommand(
        String name,
        String surname,
        String username,
        String email,
        String password
) {
}
