package com.videostore.videostore.infrastructure.persistence.adapter;

import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.repository.FavouriteRepository;

import java.util.List;
import java.util.Optional;

public class FavouriteRepositoryImpl implements FavouriteRepository {

    @Override
    public Optional<Favourite> findByUserIdAndMovieId(UserId userId, MovieId movieId) {
        return Optional.empty();
    }

    @Override
    public boolean existsByUserIdAndMovieId(UserId userId, MovieId movieId) {
        return false;
    }

    @Override
    public List<Favourite> findAllByUser(UserId userId) {
        return List.of();
    }

    @Override
    public Favourite addFavourite(Favourite favourite) {
        return null;
    }

    @Override
    public void removeFavourite(Favourite favourite) {

    }
}
