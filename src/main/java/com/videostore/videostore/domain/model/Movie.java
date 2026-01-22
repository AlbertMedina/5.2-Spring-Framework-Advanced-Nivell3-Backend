package com.videostore.videostore.domain.model;

public class Movie {

    public String title;
    public int year;
    public String genre;
    public int duration;
    public String director;
    public String synopsis;

    public Movie(String title, int year, String genre, int duration, String director, String synopsis) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.duration = duration;
        this.director = director;
        this.synopsis = synopsis;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }

    public String getDirector() {
        return director;
    }

    public String getSynopsis() {
        return synopsis;
    }
}
