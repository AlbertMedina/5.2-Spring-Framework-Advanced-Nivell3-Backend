package com.videostore.videostore.application.command.movie;

public record UpdateMovieInfoCommand(
        String title,
        int year,
        String genre,
        int duration,
        String director,
        String synopsis
) {
}
