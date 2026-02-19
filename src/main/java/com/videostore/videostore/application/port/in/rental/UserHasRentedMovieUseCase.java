package com.videostore.videostore.application.port.in.rental;

public interface UserHasRentedMovieUseCase {
    boolean execute(String username, Long movieId);
}
