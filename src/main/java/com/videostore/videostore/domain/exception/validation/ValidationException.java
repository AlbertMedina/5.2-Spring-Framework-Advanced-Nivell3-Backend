package com.videostore.videostore.domain.exception.validation;

import com.videostore.videostore.domain.exception.DomainException;

public abstract class ValidationException extends DomainException {

    protected ValidationException(String message) {
        super(message);
    }
}

