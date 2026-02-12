package com.videostore.videostore.application.command.movie;

public record AddMovieCommand(
        String title,
        int year,
        String genre,
        int duration,
        String director,
        String synopsis,
        int numberOfCopies,
        byte[] poster,
        String posterFilename
) {
}
