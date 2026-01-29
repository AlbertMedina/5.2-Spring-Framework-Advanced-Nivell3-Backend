package com.videostore.videostore.infrastructure.persistence.mapper;

import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.domain.model.favourite.valueobject.FavouriteDate;
import com.videostore.videostore.infrastructure.persistence.entity.FavouriteEntity;
import com.videostore.videostore.infrastructure.persistence.entity.MovieEntity;
import com.videostore.videostore.infrastructure.persistence.entity.UserEntity;

public final class FavouriteMapper {

    private FavouriteMapper() {
    }

    public static FavouriteEntity toEntity(Favourite favourite, UserEntity userEntity, MovieEntity movieEntity) {
        return new FavouriteEntity(
                userEntity,
                movieEntity,
                favourite.getFavouriteDate().value()
        );
    }

    public static Favourite toDomain(FavouriteEntity entity) {
        return Favourite.create(
                UserMapper.toDomain(entity.getUser()),
                MovieMapper.toDomain(entity.getMovie()),
                new FavouriteDate(entity.getFavouriteDate())
        );
    }
}
