package com.videostore.videostore.domain.repository;

import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.user.valueobject.UserId;

import java.util.List;
import java.util.Optional;

public interface FavouriteRepository {

    Optional<Favourite> findByUserIdAndMovieId(UserId userId, MovieId movieId);

    boolean existsByUserIdAndMovieId(UserId userId, MovieId movieId);

    List<Favourite> findAllByUser(UserId userId);

    Favourite addFavourite(Favourite favourite);

    void removeFavourite(Favourite favourite);
}
