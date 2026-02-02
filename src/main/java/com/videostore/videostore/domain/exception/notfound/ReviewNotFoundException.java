package com.videostore.videostore.domain.exception.notfound;

public class ReviewNotFoundException extends NotFoundException {
    public ReviewNotFoundException(String message) {
        super(message);
    }
}
