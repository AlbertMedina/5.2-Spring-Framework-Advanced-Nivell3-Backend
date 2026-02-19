package com.videostore.videostore.application.port.in.favourite;

public interface UserHasFavouriteMovieUseCase {
    boolean execute(String username, Long movieId);
}
