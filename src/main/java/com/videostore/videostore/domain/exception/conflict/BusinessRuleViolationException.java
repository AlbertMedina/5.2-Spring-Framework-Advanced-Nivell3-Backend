package com.videostore.videostore.domain.exception.conflict;

import com.videostore.videostore.domain.exception.DomainException;

public abstract class BusinessRuleViolationException extends DomainException {

    protected BusinessRuleViolationException(String message) {
        super(message);
    }
}
