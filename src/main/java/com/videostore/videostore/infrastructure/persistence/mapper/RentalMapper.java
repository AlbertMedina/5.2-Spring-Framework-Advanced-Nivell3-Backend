package com.videostore.videostore.infrastructure.persistence.mapper;

import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.model.rental.valueobject.RentalDate;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.infrastructure.persistence.entity.MovieEntity;
import com.videostore.videostore.infrastructure.persistence.entity.RentalEntity;
import com.videostore.videostore.infrastructure.persistence.entity.UserEntity;

public final class RentalMapper {

    private RentalMapper() {
    }

    public static RentalEntity toEntity(Rental rental, UserEntity userEntity, MovieEntity movieEntity) {
        return new RentalEntity(
                userEntity,
                movieEntity,
                rental.getRentalDate().value()
        );
    }

    public static Rental toDomain(RentalEntity entity) {
        return Rental.create(
                new UserId(entity.getUser().getId()),
                new MovieId(entity.getMovie().getId()),
                new RentalDate(entity.getRentalDate())
        );
    }
}
