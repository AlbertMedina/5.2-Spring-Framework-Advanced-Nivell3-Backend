package com.videostore.videostore.web.controller.rental;

import com.videostore.videostore.application.command.rental.RentMovieCommand;
import com.videostore.videostore.application.command.rental.ReturnMovieCommand;
import com.videostore.videostore.application.port.in.rental.*;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.web.controller.rental.dto.request.RentMovieRequest;
import com.videostore.videostore.web.controller.rental.dto.response.RentalResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
public class RentalController {

    private final RentMovieUseCase rentMovieUseCase;
    private final ReturnMovieUseCase returnMovieUseCase;
    private final GetMyRentalsUseCase getMyRentalsUseCase;
    private final GetRentalsByUserUseCase getRentalsByUserUseCase;
    private final GetRentalsByMovieUseCase getRentalsByMovieUseCase;

    public RentalController(
            RentMovieUseCase rentMovieUseCase,
            ReturnMovieUseCase returnMovieUseCase,
            GetMyRentalsUseCase getMyRentalsUseCase,
            GetRentalsByUserUseCase getRentalsByUserUseCase,
            GetRentalsByMovieUseCase getRentalsByMovieUseCase
    ) {
        this.rentMovieUseCase = rentMovieUseCase;
        this.returnMovieUseCase = returnMovieUseCase;
        this.getMyRentalsUseCase = getMyRentalsUseCase;
        this.getRentalsByUserUseCase = getRentalsByUserUseCase;
        this.getRentalsByMovieUseCase = getRentalsByMovieUseCase;
    }

    @PostMapping("/rentals")
    public ResponseEntity<RentalResponse> rentMovie(@RequestBody @Valid RentMovieRequest request, Authentication authentication) {
        RentMovieCommand command = new RentMovieCommand(authentication.getName(), request.movieId());
        Rental rental = rentMovieUseCase.execute(command);

        RentalResponse response = RentalResponse.fromDomain(rental);
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/rentals/{movieId}")
    public ResponseEntity<Void> returnMovie(@PathVariable @Positive Long movieId, Authentication authentication) {
        ReturnMovieCommand command = new ReturnMovieCommand(authentication.getName(), movieId);
        returnMovieUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/rentals")
    public ResponseEntity<List<RentalResponse>> getMyRentals(Authentication authentication) {
        List<RentalResponse> response = getMyRentalsUseCase.execute(authentication.getName())
                .stream().map(RentalResponse::fromDomain).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/rentals")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RentalResponse>> getRentalsByUser(@PathVariable @Positive Long userId) {
        List<RentalResponse> response = getRentalsByUserUseCase.execute(userId)
                .stream().map(RentalResponse::fromDomain).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/movies/{movieId}/rentals")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RentalResponse>> getRentalsByMovie(@PathVariable @Positive Long movieId) {
        List<RentalResponse> response = getRentalsByMovieUseCase.execute(movieId)
                .stream().map(RentalResponse::fromDomain).toList();

        return ResponseEntity.ok(response);
    }
}
