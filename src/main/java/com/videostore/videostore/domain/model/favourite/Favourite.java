package com.videostore.videostore.domain.model.favourite;

import com.videostore.videostore.domain.model.favourite.valueobject.FavouriteDate;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.user.User;

public class Favourite {

    private final User user;
    private final Movie movie;
    private final FavouriteDate favouriteDate;

    public Favourite(User user, Movie movie, FavouriteDate favouriteDate) {
        this.user = user;
        this.movie = movie;
        this.favouriteDate = favouriteDate;
    }

    public User getUser() {
        return user;
    }

    public Movie getMovie() {
        return movie;
    }

    public FavouriteDate getFavouriteDate() {
        return favouriteDate;
    }
}
