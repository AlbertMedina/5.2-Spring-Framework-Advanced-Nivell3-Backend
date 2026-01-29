package com.videostore.videostore.infrastructure.persistence.adapter;

import com.videostore.videostore.domain.exception.MovieNotFoundException;
import com.videostore.videostore.domain.exception.UserNotFoundException;
import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.repository.FavouriteRepository;
import com.videostore.videostore.infrastructure.persistence.entity.FavouriteEntity;
import com.videostore.videostore.infrastructure.persistence.entity.MovieEntity;
import com.videostore.videostore.infrastructure.persistence.entity.UserEntity;
import com.videostore.videostore.infrastructure.persistence.mapper.FavouriteMapper;
import com.videostore.videostore.infrastructure.persistence.repository.FavouriteRepositoryJPA;
import com.videostore.videostore.infrastructure.persistence.repository.MovieRepositoryJPA;
import com.videostore.videostore.infrastructure.persistence.repository.UserRepositoryJPA;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FavouriteRepositoryImpl implements FavouriteRepository {

    private final FavouriteRepositoryJPA favouriteRepositoryJPA;
    private final UserRepositoryJPA userRepositoryJPA;
    private final MovieRepositoryJPA movieRepositoryJPA;

    public FavouriteRepositoryImpl(FavouriteRepositoryJPA favouriteRepositoryJPA, UserRepositoryJPA userRepositoryJPA, MovieRepositoryJPA movieRepositoryJPA) {
        this.favouriteRepositoryJPA = favouriteRepositoryJPA;
        this.userRepositoryJPA = userRepositoryJPA;
        this.movieRepositoryJPA = movieRepositoryJPA;
    }

    @Override
    public Optional<Favourite> findByUserIdAndMovieId(UserId userId, MovieId movieId) {
        return favouriteRepositoryJPA.findByUserIdAndMovieId(userId.value(), movieId.value())
                .map(FavouriteMapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndMovieId(UserId userId, MovieId movieId) {
        return favouriteRepositoryJPA.existsByUserIdAndMovieId(userId.value(), movieId.value());
    }

    @Override
    public List<Favourite> findAllByUser(UserId userId) {
        return favouriteRepositoryJPA.findAllByUserId(userId.value())
                .stream().map(FavouriteMapper::toDomain).toList();
    }

    @Override
    public Favourite addFavourite(Favourite favourite) {
        UserEntity userEntity = getUserEntity(favourite.getUserId());
        MovieEntity movieEntity = getMovieEntity(favourite.getMovieId());

        FavouriteEntity entity = FavouriteMapper.toEntity(favourite, userEntity, movieEntity);

        return FavouriteMapper.toDomain(favouriteRepositoryJPA.save(entity));
    }

    @Override
    public void removeFavourite(Favourite favourite) {
        UserEntity userEntity = getUserEntity(favourite.getUserId());
        MovieEntity movieEntity = getMovieEntity(favourite.getMovieId());

        FavouriteEntity entity = FavouriteMapper.toEntity(favourite, userEntity, movieEntity);

        favouriteRepositoryJPA.delete(entity);
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
