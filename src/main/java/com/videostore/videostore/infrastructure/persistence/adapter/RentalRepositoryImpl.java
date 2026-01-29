package com.videostore.videostore.infrastructure.persistence.adapter;

import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.repository.RentalRepository;

import java.util.List;
import java.util.Optional;

public class RentalRepositoryImpl implements RentalRepository {
    
    @Override
    public Optional<Rental> findByUserIdAndMovieId(UserId userId, MovieId movieId) {
        return Optional.empty();
    }

    @Override
    public boolean existsByUserIdAndMovieId(UserId userId, MovieId movieId) {
        return false;
    }

    @Override
    public List<Rental> findAllByUser(UserId userId) {
        return List.of();
    }

    @Override
    public List<Rental> findAllByMovie(MovieId movieId) {
        return List.of();
    }

    @Override
    public int activeRentalsByMovie(MovieId movieId) {
        return 0;
    }

    @Override
    public Rental addRental(Rental rental) {
        return null;
    }

    @Override
    public void removeRental(Rental rental) {

    }

    @Override
    public void returnAllByUser(UserId userId) {

    }
}
