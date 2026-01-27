package com.videostore.videostore.application.command.favourite;

public record AddFavouriteCommand(
        Long userId,
        Long movieId
) {
}
