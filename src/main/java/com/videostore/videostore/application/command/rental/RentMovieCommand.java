package com.videostore.videostore.application.command.rental;

public class RentMovieCommand {

    private final Long userId;
    private final Long movieId;

    public RentMovieCommand(Long userId, Long movieId) {
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
