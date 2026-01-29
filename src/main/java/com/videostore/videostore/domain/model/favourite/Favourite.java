package com.videostore.videostore.domain.model.favourite;

import com.videostore.videostore.domain.model.favourite.valueobject.FavouriteDate;
import com.videostore.videostore.domain.model.favourite.valueobject.FavouriteId;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.user.valueobject.UserId;

public class Favourite {

    private final FavouriteId favouriteId;
    private final UserId userId;
    private final MovieId movieId;
    private final FavouriteDate favouriteDate;

    public Favourite(FavouriteId favouriteId, UserId userId, MovieId movieId, FavouriteDate favouriteDate) {
        this.favouriteId = favouriteId;
        this.userId = userId;
        this.movieId = movieId;
        this.favouriteDate = favouriteDate;
    }

    public FavouriteId getFavouriteId() {
        return favouriteId;
    }

    public UserId getUserId() {
        return userId;
    }

    public MovieId getMovieId() {
        return movieId;
    }

    public FavouriteDate getFavouriteDate() {
        return favouriteDate;
    }

    public static Favourite create(FavouriteId favouriteId, UserId userId, MovieId movieId, FavouriteDate favouriteDate) {
        return new Favourite(favouriteId, userId, movieId, favouriteDate);
    }
}
