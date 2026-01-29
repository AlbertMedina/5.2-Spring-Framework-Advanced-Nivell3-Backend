package com.videostore.videostore.infrastructure.persistence.adapter;

import com.videostore.videostore.domain.exception.MovieNotFoundException;
import com.videostore.videostore.domain.exception.UserNotFoundException;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.repository.RentalRepository;
import com.videostore.videostore.infrastructure.persistence.entity.MovieEntity;
import com.videostore.videostore.infrastructure.persistence.entity.RentalEntity;
import com.videostore.videostore.infrastructure.persistence.entity.UserEntity;
import com.videostore.videostore.infrastructure.persistence.mapper.RentalMapper;
import com.videostore.videostore.infrastructure.persistence.repository.MovieRepositoryJPA;
import com.videostore.videostore.infrastructure.persistence.repository.RentalRepositoryJPA;
import com.videostore.videostore.infrastructure.persistence.repository.UserRepositoryJPA;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RentalRepositoryImpl implements RentalRepository {

    private final RentalRepositoryJPA rentalRepositoryJPA;
    private final UserRepositoryJPA userRepositoryJPA;
    private final MovieRepositoryJPA movieRepositoryJPA;

    public RentalRepositoryImpl(RentalRepositoryJPA rentalRepositoryJPA, UserRepositoryJPA userRepositoryJPA, MovieRepositoryJPA movieRepositoryJPA) {
        this.rentalRepositoryJPA = rentalRepositoryJPA;
        this.userRepositoryJPA = userRepositoryJPA;
        this.movieRepositoryJPA = movieRepositoryJPA;
    }

    @Override
    public Optional<Rental> findByUserIdAndMovieId(UserId userId, MovieId movieId) {
        return rentalRepositoryJPA.findByUserIdAndMovieId(userId.value(), movieId.value())
                .map(RentalMapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndMovieId(UserId userId, MovieId movieId) {
        return rentalRepositoryJPA.existsByUserIdAndMovieId(userId.value(), movieId.value());
    }

    @Override
    public List<Rental> findAllByUser(UserId userId) {
        return rentalRepositoryJPA.findAllByUserId(userId.value())
                .stream().map(RentalMapper::toDomain).toList();
    }

    @Override
    public List<Rental> findAllByMovie(MovieId movieId) {
        return rentalRepositoryJPA.findAllByMovieId(movieId.value())
                .stream().map(RentalMapper::toDomain).toList();
    }

    @Override
    public int countRentalsByMovie(MovieId movieId) {
        return rentalRepositoryJPA.countByMovieId(movieId.value());
    }

    @Override
    public Rental addRental(Rental rental) {
        UserEntity userEntity = getUserEntity(rental.getUserId());
        MovieEntity movieEntity = getMovieEntity(rental.getMovieId());

        RentalEntity entity = RentalMapper.toEntity(rental, userEntity, movieEntity);

        return RentalMapper.toDomain(rentalRepositoryJPA.save(entity));
    }

    @Override
    public void removeRental(Rental rental) {
        UserEntity userEntity = getUserEntity(rental.getUserId());
        MovieEntity movieEntity = getMovieEntity(rental.getMovieId());

        RentalEntity entity = RentalMapper.toEntity(rental, userEntity, movieEntity);

        rentalRepositoryJPA.delete(entity);
    }

    @Override
    public void returnAllByUser(UserId userId) {
        rentalRepositoryJPA.findAllByUserId(userId.value()).forEach(rentalRepositoryJPA::delete);
    }

    private UserEntity getUserEntity(UserId userId) {
        return userRepositoryJPA.findById(userId.value())
                .orElseThrow(() -> new UserNotFoundException(userId.value()));
    }

    private MovieEntity getMovieEntity(MovieId movieId) {
        return movieRepositoryJPA.findById(movieId.value())
                .orElseThrow(() -> new MovieNotFoundException(movieId.value()));
    }
}
