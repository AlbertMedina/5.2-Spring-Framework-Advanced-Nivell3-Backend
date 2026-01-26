package com.videostore.videostore.application.command.rental;

public record ReturnMovieCommand(
        Long userId,
        Long movieId
) {
}
