package com.videostore.videostore.application.port.in.rental;

import com.videostore.videostore.application.model.RentalDetails;

import java.util.List;

public interface GetRentalsByMovieUseCase {
    List<RentalDetails> execute(Long movieId);
}
