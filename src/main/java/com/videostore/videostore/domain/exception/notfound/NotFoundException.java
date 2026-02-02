package com.videostore.videostore.domain.exception.notfound;

import com.videostore.videostore.domain.exception.DomainException;

public abstract class NotFoundException extends DomainException {

    protected NotFoundException(String entity, Long id) {
        super(entity + " " + id + " not found");
    }

    protected NotFoundException(String message) {
        super(message);
    }
}
