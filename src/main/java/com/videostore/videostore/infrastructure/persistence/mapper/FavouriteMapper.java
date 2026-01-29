package com.videostore.videostore.infrastructure.persistence.mapper;

import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.domain.model.favourite.valueobject.FavouriteDate;
import com.videostore.videostore.domain.model.favourite.valueobject.FavouriteId;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
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
                new FavouriteId(entity.getId()),
                new UserId(entity.getUser().getId()),
                new MovieId(entity.getMovie().getId()),
                new FavouriteDate(entity.getFavouriteDate())
        );
    }
}
