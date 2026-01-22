package com.videostore.videostore.domain.model;

import java.time.LocalDate;

public class Rental {

    private final User user;
    private final Movie movie;
    private final LocalDate date;

    public Rental(User user, Movie movie, LocalDate date) {
        this.user = user;
        this.movie = movie;
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public Movie getMovie() {
        return movie;
    }

    public LocalDate getDate() {
        return date;
    }
}
