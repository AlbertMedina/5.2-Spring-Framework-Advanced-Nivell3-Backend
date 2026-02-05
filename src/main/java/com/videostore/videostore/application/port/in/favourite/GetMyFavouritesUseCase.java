package com.videostore.videostore.application.port.in.favourite;

import com.videostore.videostore.domain.model.favourite.Favourite;

import java.util.List;

public interface GetMyFavouritesUseCase {
    List<Favourite> execute(String username);
}
