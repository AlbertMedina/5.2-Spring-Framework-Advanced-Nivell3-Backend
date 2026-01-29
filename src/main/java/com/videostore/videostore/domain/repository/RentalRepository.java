package com.videostore.videostore.domain.repository;

import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.model.user.valueobject.UserId;

import java.util.List;
import java.util.Optional;

public interface RentalRepository {

    Optional<Rental> findByUserIdAndMovieId(UserId userId, MovieId movieId);

    boolean existsByUserIdAndMovieId(UserId userId, MovieId movieId);

    List<Rental> findAllByUser(UserId userId);

    List<Rental> findAllByMovie(MovieId movieId);

    int countRentalsByMovie(MovieId movieId);

    Rental addRental(Rental rental);

    void removeRental(Rental rental);

    void returnAllByUser(UserId userId);
}
