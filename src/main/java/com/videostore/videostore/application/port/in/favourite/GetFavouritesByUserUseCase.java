package com.videostore.videostore.application.port.in.favourite;

import com.videostore.videostore.domain.model.favourite.Favourite;

import java.util.List;

public interface GetFavouritesByUserUseCase {
    List<Favourite> execute(Long userId);
}
