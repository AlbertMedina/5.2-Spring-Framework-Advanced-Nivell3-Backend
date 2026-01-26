package com.videostore.videostore.application.command.rental;

public record RentMovieCommand(
        Long userId,
        Long movieId
) {
}
