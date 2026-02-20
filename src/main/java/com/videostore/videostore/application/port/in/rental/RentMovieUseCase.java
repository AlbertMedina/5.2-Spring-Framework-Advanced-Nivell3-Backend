package com.videostore.videostore.application.port.in.rental;

import com.videostore.videostore.application.command.rental.RentMovieCommand;
import com.videostore.videostore.application.model.RentalDetails;

public interface RentMovieUseCase {
    RentalDetails execute(RentMovieCommand command);
}
