package com.videostore.videostore.application.port.in.rental;

import com.videostore.videostore.application.command.rental.RentMovieCommand;
import com.videostore.videostore.domain.model.rental.Rental;

public interface RentMovieUseCase {
    Rental execute(RentMovieCommand command);
}
