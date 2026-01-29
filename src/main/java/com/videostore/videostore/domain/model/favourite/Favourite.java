package com.videostore.videostore.domain.model.favourite;

import com.videostore.videostore.domain.model.favourite.valueobject.FavouriteDate;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.user.valueobject.UserId;

public class Favourite {

    private final UserId userId;
    private final MovieId movieId;
    private final FavouriteDate favouriteDate;

    public Favourite(UserId userId, MovieId movieId, FavouriteDate favouriteDate) {
        this.userId = userId;
        this.movieId = movieId;
        this.favouriteDate = favouriteDate;
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

    public static Favourite create(UserId userId, MovieId movieId, FavouriteDate favouriteDate) {
        return new Favourite(userId, movieId, favouriteDate);
    }
}
