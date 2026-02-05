package com.videostore.videostore.application.command.auth;

public record LoginUserCommand(
        String loginIdentifier,
        String password
) {
}
