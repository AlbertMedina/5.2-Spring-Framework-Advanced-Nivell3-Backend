package com.videostore.videostore.domain.repository;

import com.videostore.videostore.domain.model.Favourite;
import com.videostore.videostore.domain.model.Movie;
import com.videostore.videostore.domain.model.User;

import java.util.List;

public interface FavouriteRepository {

    List<Favourite> findByUser(Long userId);

    boolean exists(User user, Movie movie);

    Favourite save(Favourite favourite);

    void deleteById(Long id);
}
