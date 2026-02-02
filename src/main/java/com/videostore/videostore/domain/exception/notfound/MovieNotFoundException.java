package com.videostore.videostore.domain.exception.notfound;

public class MovieNotFoundException extends NotFoundException {
    public MovieNotFoundException(Long id) {
        super("Movie", id);
    }
}
