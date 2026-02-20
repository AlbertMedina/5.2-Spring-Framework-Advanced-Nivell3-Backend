package com.videostore.videostore.domain.exception.notfound;

public class ReviewNotFoundException extends NotFoundException {
    public ReviewNotFoundException(Long id) {
        super("Review", id);
    }
}
