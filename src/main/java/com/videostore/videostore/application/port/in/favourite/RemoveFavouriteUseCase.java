package com.videostore.videostore.application.port.in.favourite;

import com.videostore.videostore.application.command.favourite.RemoveFavouriteCommand;

public interface RemoveFavouriteUseCase {
    void execute(RemoveFavouriteCommand command);
}
