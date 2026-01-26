package com.videostore.videostore.domain.model.movie;

import com.videostore.videostore.domain.model.movie.valueobject.*;

public class Movie {

    private Title title;
    private Year year;
    private Genre genre;
    private Duration duration;
    private Director director;
    private Synopsis synopsis;
    private final NumberOfCopies numberOfCopies;

    public Movie(Title title, Year year, Genre genre, Duration duration, Director director, Synopsis synopsis, NumberOfCopies numberOfCopies) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.duration = duration;
        this.director = director;
        this.synopsis = synopsis;
        this.numberOfCopies = numberOfCopies;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

    public Synopsis getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(Synopsis synopsis) {
        this.synopsis = synopsis;
    }

    public NumberOfCopies getNumberOfCopies() {
        return numberOfCopies;
    }

    public static Movie create(Title title, Year year, Genre genre, Duration duration, Director director, Synopsis synopsis, NumberOfCopies numberOfCopies) {
        return new Movie(title, year, genre, duration, director, synopsis, numberOfCopies);
    }
}
