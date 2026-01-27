package com.videostore.videostore.application.command.favourite;

public record RemoveFavouriteCommand(
        Long userId,
        Long movieId
) {
}
