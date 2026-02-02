package com.videostore.videostore.domain.exception.validation;

public class InvalidCredentialsException extends ValidationException {
    public InvalidCredentialsException() {
        super("Wrong username/email or password");
    }
}
