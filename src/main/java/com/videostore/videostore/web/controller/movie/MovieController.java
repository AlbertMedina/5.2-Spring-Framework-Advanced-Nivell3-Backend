package com.videostore.videostore.web.controller.movie;

import com.videostore.videostore.application.command.movie.AddMovieCommand;
import com.videostore.videostore.application.command.movie.UpdateMovieInfoCommand;
import com.videostore.videostore.application.port.in.movie.*;
import com.videostore.videostore.application.query.movie.GetAllMoviesQuery;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.MovieSortBy;
import com.videostore.videostore.web.controller.movie.dto.request.AddMovieRequest;
import com.videostore.videostore.web.controller.movie.dto.request.UpdateMovieInfoRequest;
import com.videostore.videostore.web.controller.movie.dto.response.MovieResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
public class MovieController {

    private final AddMovieUseCase addMovieUseCase;
    private final UpdateMovieInfoUseCase updateMovieInfoUseCase;
    private final RemoveMovieUseCase removeMovieUseCase;
    private final GetMovieUseCase getMovieUseCase;
    private final GetAllMoviesUseCase getAllMoviesUseCase;

    public MovieController(
            AddMovieUseCase addMovieUseCase,
            UpdateMovieInfoUseCase updateMovieInfoUseCase,
            RemoveMovieUseCase removeMovieUseCase,
            GetMovieUseCase getMovieUseCase,
            GetAllMoviesUseCase getAllMoviesUseCase
    ) {
        this.addMovieUseCase = addMovieUseCase;
        this.updateMovieInfoUseCase = updateMovieInfoUseCase;
        this.removeMovieUseCase = removeMovieUseCase;
        this.getMovieUseCase = getMovieUseCase;
        this.getAllMoviesUseCase = getAllMoviesUseCase;
    }

    @PostMapping("/movies")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponse> addMovie(@RequestBody @Valid AddMovieRequest request) {
        AddMovieCommand command = new AddMovieCommand(
                request.title(),
                request.year(),
                request.genre(),
                request.duration(),
                request.director(),
                request.synopsis(),
                request.numberOfCopies()
        );
        Movie movie = addMovieUseCase.execute(command);

        MovieResponse response = MovieResponse.fromDomain(movie);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/movies/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponse> updateMovieInfo(@PathVariable @Positive Long movieId, @RequestBody @Valid UpdateMovieInfoRequest request) {
        UpdateMovieInfoCommand command = new UpdateMovieInfoCommand(
                request.title(),
                request.year(),
                request.genre(),
                request.duration(),
                request.director(),
                request.synopsis()
        );
        Movie movie = updateMovieInfoUseCase.execute(movieId, command);

        MovieResponse response = MovieResponse.fromDomain(movie);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/movies/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeMovie(@PathVariable @Positive Long movieId) {
        removeMovieUseCase.execute(movieId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<MovieResponse> getMovie(@PathVariable @Positive Long movieId) {
        Movie movie = getMovieUseCase.execute(movieId);

        MovieResponse response = MovieResponse.fromDomain(movie);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/movies")
    public ResponseEntity<List<MovieResponse>> getAllMovies(@RequestParam int page,
                                                            @RequestParam int size,
                                                            @RequestParam(required = false) String genre,
                                                            @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                                            @RequestParam(required = false) String title,
                                                            @RequestParam MovieSortBy sortBy,
                                                            @RequestParam(defaultValue = "true") boolean ascending) {

        GetAllMoviesQuery getAllMovieQuery = new GetAllMoviesQuery(page, size, genre, onlyAvailable, title, sortBy, ascending);
        List<MovieResponse> response = getAllMoviesUseCase.execute(getAllMovieQuery)
                .stream().map(MovieResponse::fromDomain).toList();

        return ResponseEntity.ok(response);
    }
}
