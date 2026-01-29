package com.videostore.videostore.domain.model.rental;

import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.rental.valueobject.RentalDate;
import com.videostore.videostore.domain.model.rental.valueobject.RentalId;
import com.videostore.videostore.domain.model.user.valueobject.UserId;

public class Rental {

    private final RentalId rentalId;
    private final UserId userId;
    private final MovieId movieId;
    private final RentalDate rentalDate;

    public Rental(RentalId rentalId, UserId userId, MovieId movieId, RentalDate rentalDate) {
        this.rentalId = rentalId;
        this.userId = userId;
        this.movieId = movieId;
        this.rentalDate = rentalDate;
    }

    public RentalId getRentalId() {
        return rentalId;
    }

    public UserId getUserId() {
        return userId;
    }

    public MovieId getMovieId() {
        return movieId;
    }

    public RentalDate getRentalDate() {
        return rentalDate;
    }

    public static Rental create(RentalId rentalId, UserId userId, MovieId movieId, RentalDate rentalDate) {
        return new Rental(rentalId, userId, movieId, rentalDate);
    }
}
