package com.videostore.videostore.domain.model;

public class Review {

    private final User user;
    private final Movie movie;
    private final int score;
    private final String comment;

    public Review(User user, Movie movie, int score, String comment) {
        this.user = user;
        this.movie = movie;
        this.score = score;
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public Movie getMovie() {
        return movie;
    }

    public int getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }
}
