package com.videostore.videostore.application.port.in.favourite;

import com.videostore.videostore.application.command.favourite.AddFavouriteCommand;
import com.videostore.videostore.domain.model.favourite.Favourite;

public interface AddFavouriteUseCase {
    Favourite execute(AddFavouriteCommand addFavouriteCommand);
}
