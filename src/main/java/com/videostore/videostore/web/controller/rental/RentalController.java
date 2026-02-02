package com.videostore.videostore.web.controller.rental;

import com.videostore.videostore.application.command.rental.RentMovieCommand;
import com.videostore.videostore.application.command.rental.ReturnMovieCommand;
import com.videostore.videostore.application.port.in.rental.GetRentalsByMovieUseCase;
import com.videostore.videostore.application.port.in.rental.GetRentalsByUserUseCase;
import com.videostore.videostore.application.port.in.rental.RentMovieUseCase;
import com.videostore.videostore.application.port.in.rental.ReturnMovieUseCase;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.web.controller.rental.dto.request.RentMovieRequest;
import com.videostore.videostore.web.controller.rental.dto.response.RentalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RentalController {

    private final RentMovieUseCase rentMovieUseCase;
    private final ReturnMovieUseCase returnMovieUseCase;
    private final GetRentalsByUserUseCase getRentalsByUserUseCase;
    private final GetRentalsByMovieUseCase getRentalsByMovieUseCase;

    public RentalController(RentMovieUseCase rentMovieUseCase, ReturnMovieUseCase returnMovieUseCase, GetRentalsByUserUseCase getRentalsByUserUseCase, GetRentalsByMovieUseCase getRentalsByMovieUseCase) {
        this.rentMovieUseCase = rentMovieUseCase;
        this.returnMovieUseCase = returnMovieUseCase;
        this.getRentalsByUserUseCase = getRentalsByUserUseCase;
        this.getRentalsByMovieUseCase = getRentalsByMovieUseCase;
    }

    @PostMapping("/rentals")
    public ResponseEntity<RentalResponse> rentMovie(@RequestBody RentMovieRequest request) {
        RentMovieCommand command = new RentMovieCommand(request.userId(), request.movieId());
        Rental rental = rentMovieUseCase.execute(command);

        RentalResponse response = RentalResponse.fromDomain(rental);
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/rentals/{userId}/{movieId}")
    public ResponseEntity<Void> returnMovie(@PathVariable Long userId, @PathVariable Long movieId) {
        ReturnMovieCommand command = new ReturnMovieCommand(userId, movieId);
        returnMovieUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/rentals")
    public ResponseEntity<List<RentalResponse>> getRentalsByUser(@PathVariable Long userId) {
        List<RentalResponse> response = getRentalsByUserUseCase.execute(userId)
                .stream().map(RentalResponse::fromDomain).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/movies/{movieId}/rentals")
    public ResponseEntity<List<RentalResponse>> getRentalsByMovie(@PathVariable Long movieId) {
        List<RentalResponse> response = getRentalsByMovieUseCase.execute(movieId)
                .stream().map(RentalResponse::fromDomain).toList();

        return ResponseEntity.ok(response);
    }
}
