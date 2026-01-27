package com.videostore.videostore.application.usecase.favourite;

import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.domain.repository.FavouriteRepository;

import java.util.List;

public class GetFavouritesByUserUseCase {

    private final FavouriteRepository favouriteRepository;

    public GetFavouritesByUserUseCase(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
    }

    public List<Favourite> execute(Long userId) {
        return favouriteRepository.findAllByUser(userId);
    }
}
