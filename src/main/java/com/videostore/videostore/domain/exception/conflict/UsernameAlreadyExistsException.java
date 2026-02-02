package com.videostore.videostore.domain.exception.conflict;

public class UsernameAlreadyExistsException extends BusinessRuleViolationException {
    public UsernameAlreadyExistsException(String username) {
        super("Username " + username + " already exists");
    }
}
