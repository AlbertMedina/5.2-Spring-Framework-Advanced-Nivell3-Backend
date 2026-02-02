package com.videostore.videostore.domain.exception.conflict;

public class EmailAlreadyExistsException extends BusinessRuleViolationException {
    public EmailAlreadyExistsException(String email) {
        super("User with email " + email + " already exists");
    }
}
