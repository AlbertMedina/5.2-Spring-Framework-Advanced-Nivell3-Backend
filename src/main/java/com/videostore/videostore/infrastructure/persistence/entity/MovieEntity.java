package com.videostore.videostore.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    private String director;

    @Column(length = 1000, nullable = false)
    private String synopsis;

    @Column(nullable = false)
    private int numberOfCopies;

    @Column(length = 1000)
    private String posterUrl;

    protected MovieEntity() {
    }

    public MovieEntity(String title, int year, String genre, int duration, String director, String synopsis, int numberOfCopies) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.duration = duration;
        this.director = director;
        this.synopsis = synopsis;
        this.numberOfCopies = numberOfCopies;
    }

    public Long getId() {
        return id;
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

    public int getNumberOfCopies() {
        return numberOfCopies;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}
