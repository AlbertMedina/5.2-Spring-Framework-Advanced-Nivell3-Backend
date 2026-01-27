package com.videostore.videostore.domain.repository;

import com.videostore.videostore.domain.model.favourite.Favourite;

import java.util.List;
import java.util.Optional;

public interface FavouriteRepository {

    Optional<Favourite> findByUserIdAndMovieId(Long userId, Long movieId);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    List<Favourite> findAllByUser(Long userId);

    Favourite addFavourite(Favourite favourite);

    void removeFavourite(Favourite favourite);
}
