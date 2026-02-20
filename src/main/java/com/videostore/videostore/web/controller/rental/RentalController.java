package com.videostore.videostore.web.controller.rental;

import com.videostore.videostore.application.command.rental.RentMovieCommand;
import com.videostore.videostore.application.command.rental.ReturnMovieCommand;
import com.videostore.videostore.application.model.RentalDetails;
import com.videostore.videostore.application.port.in.rental.*;
import com.videostore.videostore.web.controller.rental.dto.request.RentMovieRequest;
import com.videostore.videostore.web.controller.rental.dto.response.UserHasRentedMovieResponse;
import com.videostore.videostore.web.controller.rental.dto.response.RentalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@Tag(name = "Rentals", description = "Operations related to movie rentals")
public class RentalController {

    private static final Logger log = LoggerFactory.getLogger(RentalController.class);

    private final RentMovieUseCase rentMovieUseCase;
    private final ReturnMovieUseCase returnMovieUseCase;
    private final GetMyRentalsUseCase getMyRentalsUseCase;
    private final GetRentalsByUserUseCase getRentalsByUserUseCase;
    private final GetRentalsByMovieUseCase getRentalsByMovieUseCase;
    private final UserHasRentedMovieUseCase userHasRentedMovieUseCase;

    public RentalController(
            RentMovieUseCase rentMovieUseCase,
            ReturnMovieUseCase returnMovieUseCase,
            GetMyRentalsUseCase getMyRentalsUseCase,
            GetRentalsByUserUseCase getRentalsByUserUseCase,
            GetRentalsByMovieUseCase getRentalsByMovieUseCase,
            UserHasRentedMovieUseCase userHasRentedMovieUseCase
    ) {
        this.rentMovieUseCase = rentMovieUseCase;
        this.returnMovieUseCase = returnMovieUseCase;
        this.getMyRentalsUseCase = getMyRentalsUseCase;
        this.getRentalsByUserUseCase = getRentalsByUserUseCase;
        this.getRentalsByMovieUseCase = getRentalsByMovieUseCase;
        this.userHasRentedMovieUseCase = userHasRentedMovieUseCase;
    }

    @Operation(summary = "Rent a movie by the authenticated user")
    @PostMapping("/rentals")
    @Caching(evict = {
            @CacheEvict(value = "myRentals", key = "#authentication.name"),
            @CacheEvict(value = "rentalsByUser", allEntries = true),
            @CacheEvict(value = "rentalsByMovie", key = "#request.movieId")
    })
    public ResponseEntity<RentalResponse> rentMovie(@RequestBody @Valid RentMovieRequest request, Authentication authentication) {
        log.info("User {} requested to rent movie id {}", authentication.getName(), request.movieId());

        RentMovieCommand command = new RentMovieCommand(authentication.getName(), request.movieId());
        RentalDetails rental = rentMovieUseCase.execute(command);

        log.info("User {} successfully rented movie id {}", authentication.getName(), request.movieId());

        RentalResponse response = RentalResponse.from(rental);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Return a movie rented by the authenticated user")
    @DeleteMapping("/rentals/{movieId}")
    @Caching(evict = {
            @CacheEvict(value = "myRentals", key = "#authentication.name"),
            @CacheEvict(value = "rentalsByUser", allEntries = true),
            @CacheEvict(value = "rentalsByMovie", key = "#movieId")
    })
    public ResponseEntity<Void> returnMovie(@PathVariable @Positive Long movieId, Authentication authentication) {
        log.info("User {} requested to return movie id {}", authentication.getName(), movieId);

        ReturnMovieCommand command = new ReturnMovieCommand(authentication.getName(), movieId);
        returnMovieUseCase.execute(command);

        log.info("User {} successfully returned movie id {}", authentication.getName(), movieId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all active rentals by the authenticated user")
    @GetMapping("/me/rentals")
    @Cacheable(value = "myRentals", key = "#authentication.name")
    public ResponseEntity<List<RentalResponse>> getMyRentals(Authentication authentication) {
        log.info("User {} requested all their active rentals", authentication.getName());

        List<RentalDetails> rentals = getMyRentalsUseCase.execute(authentication.getName());

        log.info("User {} successfully retrieved {} active rentals", authentication.getName(), rentals.size());

        List<RentalResponse> response = rentals.stream().map(RentalResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all active rentals by a user")
    @GetMapping("/users/{userId}/rentals")
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(value = "rentalsByUser", key = "#userId")
    public ResponseEntity<List<RentalResponse>> getRentalsByUser(@PathVariable @Positive Long userId) {
        log.info("Admin requested all active rentals for user id {}", userId);

        List<RentalDetails> rentals = getRentalsByUserUseCase.execute(userId);

        log.info("Successfully retrieved {} rentals for user id {}", rentals.size(), userId);

        List<RentalResponse> response = rentals.stream().map(RentalResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all active rentals for a movie")
    @GetMapping("/movies/{movieId}/rentals")
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(value = "rentalsByMovie", key = "#movieId")
    public ResponseEntity<List<RentalResponse>> getRentalsByMovie(@PathVariable @Positive Long movieId) {
        log.info("Admin requested all active rentals for movie id {}", movieId);

        List<RentalDetails> rentals = getRentalsByMovieUseCase.execute(movieId);

        log.info("Successfully retrieved {} rentals for movie id {}", rentals.size(), movieId);

        List<RentalResponse> response = rentals.stream().map(RentalResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check if a movie is rented by the authenticated user")
    @GetMapping("/me/rentals/{movieId}")
    public ResponseEntity<UserHasRentedMovieResponse> userHasRentedMovie(@PathVariable @Positive Long movieId, Authentication authentication) {
        log.info("User {} requested if has rented movie id {}", authentication.getName(), movieId);

        boolean rented = userHasRentedMovieUseCase.execute(authentication.getName(), movieId);

        log.info("Successfully checked request with result {}", rented);

        UserHasRentedMovieResponse response = new UserHasRentedMovieResponse(rented);
        return ResponseEntity.ok(response);
    }
}
