package com.videostore.videostore.application.command.rental;

public class ReturnMovieCommand {

    private final Long userId;
    private final Long movieId;

    public ReturnMovieCommand(Long userId, Long movieId) {
        this.userId = userId;
        this.movieId = movieId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getMovieId() {
        return movieId;
    }
}
