package com.videostore.videostore.application.port.in.rental;

import com.videostore.videostore.application.command.rental.ReturnMovieCommand;

public interface ReturnMovieUseCase {
    void execute(ReturnMovieCommand command);
}
