package com.videostore.videostore.web.controller.movie;

import com.videostore.videostore.application.command.movie.AddMovieCommand;
import com.videostore.videostore.application.command.movie.UpdateMovieInfoCommand;
import com.videostore.videostore.application.model.MovieDetails;
import com.videostore.videostore.application.port.in.movie.*;
import com.videostore.videostore.application.command.movie.GetAllMoviesCommand;
import com.videostore.videostore.domain.common.PagedResult;
import com.videostore.videostore.domain.common.RatingSummary;
import com.videostore.videostore.domain.model.movie.MovieSortBy;
import com.videostore.videostore.web.controller.movie.dto.request.AddMovieRequest;
import com.videostore.videostore.web.controller.movie.dto.request.UpdateMovieInfoRequest;
import com.videostore.videostore.web.controller.movie.dto.response.MovieResponse;
import com.videostore.videostore.web.controller.movie.dto.response.PagedMovieResponse;
import com.videostore.videostore.web.controller.movie.dto.response.RatingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Validated
@RestController
@Tag(name = "Movies", description = "Operations related to movies")
public class MovieController {

    private static final Logger log = LoggerFactory.getLogger(MovieController.class);

    private final AddMovieUseCase addMovieUseCase;
    private final UpdateMovieInfoUseCase updateMovieInfoUseCase;
    private final RemoveMovieUseCase removeMovieUseCase;
    private final GetMovieUseCase getMovieUseCase;
    private final GetAllMoviesUseCase getAllMoviesUseCase;
    private final GetAllGenresUseCase getAllGenresUseCase;
    private final GetMovieRatingUseCase getMovieRatingUseCase;

    public MovieController(
            AddMovieUseCase addMovieUseCase,
            UpdateMovieInfoUseCase updateMovieInfoUseCase,
            RemoveMovieUseCase removeMovieUseCase,
            GetMovieUseCase getMovieUseCase,
            GetAllMoviesUseCase getAllMoviesUseCase,
            GetAllGenresUseCase getAllGenresUseCase,
            GetMovieRatingUseCase getMovieRatingUseCase
    ) {
        this.addMovieUseCase = addMovieUseCase;
        this.updateMovieInfoUseCase = updateMovieInfoUseCase;
        this.removeMovieUseCase = removeMovieUseCase;
        this.getMovieUseCase = getMovieUseCase;
        this.getAllMoviesUseCase = getAllMoviesUseCase;
        this.getAllGenresUseCase = getAllGenresUseCase;
        this.getMovieRatingUseCase = getMovieRatingUseCase;
    }

    @Operation(summary = "Add a movie to the video store")
    @PostMapping(value = "/movies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "genres", allEntries = true)
    public ResponseEntity<MovieResponse> addMovie(
            @RequestPart("movie") @Valid AddMovieRequest request,
            @RequestPart(value = "poster", required = false) @Nullable MultipartFile poster
    ) throws IOException {
        log.info("Admin requested to add movie '{}'", request.title());

        boolean hasPoster = poster != null && !poster.isEmpty();

        AddMovieCommand command = new AddMovieCommand(
                request.title(),
                request.year(),
                request.genre(),
                request.duration(),
                request.director(),
                request.synopsis(),
                request.numberOfCopies(),
                hasPoster ? poster.getBytes() : null,
                hasPoster ? poster.getOriginalFilename() : null
        );

        MovieDetails movieDetails = addMovieUseCase.execute(command);

        log.info("Movie '{}' successfully added with id {}", movieDetails.title(), movieDetails.id());

        MovieResponse response = MovieResponse.from(movieDetails);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Update the info for a movie in the video store")
    @PutMapping("/movies/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Caching(evict = {
            @CacheEvict(value = "movies", key = "#movieId"),
            @CacheEvict(value = "genres", allEntries = true)
    })
    public ResponseEntity<MovieResponse> updateMovieInfo(@PathVariable @Positive Long movieId, @RequestBody @Valid UpdateMovieInfoRequest request) {
        log.info("Admin requested to update movie id {}", movieId);

        UpdateMovieInfoCommand command = new UpdateMovieInfoCommand(
                request.title(),
                request.year(),
                request.genre(),
                request.duration(),
                request.director(),
                request.synopsis()
        );
        MovieDetails movie = updateMovieInfoUseCase.execute(movieId, command);

        log.info("Movie id {} successfully updated", movieId);

        MovieResponse response = MovieResponse.from(movie);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove a movie from the video store")
    @DeleteMapping("/movies/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Caching(evict = {
            @CacheEvict(value = "movies", key = "#movieId"),
            @CacheEvict(value = "genres", allEntries = true)
    })
    public ResponseEntity<Void> removeMovie(@PathVariable @Positive Long movieId) {
        log.info("Admin requested to remove movie id {}", movieId);

        removeMovieUseCase.execute(movieId);

        log.info("Movie id {} successfully removed", movieId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get details of a movie in the video store")
    @GetMapping("/movies/{movieId}")
    @Cacheable(value = "movies", key = "#movieId")
    public ResponseEntity<MovieResponse> getMovie(@PathVariable @Positive Long movieId) {
        log.info("Request received to get movie id {}", movieId);

        MovieDetails movie = getMovieUseCase.execute(movieId);

        log.info("Successfully retrieved movie id {}", movieId);

        MovieResponse response = MovieResponse.from(movie);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all movies in the video store with filters and sorted")
    @GetMapping("/movies")
    public ResponseEntity<PagedMovieResponse> getAllMovies(@RequestParam int page,
                                                           @RequestParam int size,
                                                           @RequestParam(required = false) String genre,
                                                           @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                                           @RequestParam(required = false) String title,
                                                           @RequestParam MovieSortBy sortBy,
                                                           @RequestParam(defaultValue = "true") boolean ascending) {

        log.info("Request received to get all movies, page {}, size {}, genre '{}', onlyAvailable {}, title '{}', sortBy {}, ascending {}",
                page, size, genre, onlyAvailable, title, sortBy, ascending);

        GetAllMoviesCommand getAllMoviesCommand = new GetAllMoviesCommand(page, size, genre, onlyAvailable, title, sortBy, ascending);
        PagedResult<MovieDetails> result = getAllMoviesUseCase.execute(getAllMoviesCommand);

        log.info("Successfully retrieved {} movies", result.getTotalElements());

        PagedMovieResponse response = PagedMovieResponse.from(result);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all genres of movies in the video store")
    @GetMapping("/movies/genres")
    @Cacheable(value = "genres")
    public ResponseEntity<List<String>> getAllGenres() {
        log.info("Request received to get all genres");

        List<String> genres = getAllGenresUseCase.execute();

        log.info("Successfully retrieved {} genres", genres.size());

        return ResponseEntity.ok(genres);
    }

    @Operation(summary = "Get the average rating for a movie")
    @GetMapping("/movies/{movieId}/rating")
    @Cacheable(value = "movieRating", key = "#movieId")
    public ResponseEntity<RatingResponse> getRating(@PathVariable @Positive Long movieId) {
        log.info("Request received to get the average rating for movie {}", movieId);

        RatingSummary ratingSummary = getMovieRatingUseCase.execute(movieId);

        log.info("Successfully retrieved a rating of {} from {} reviews for movie {}", ratingSummary.average(), ratingSummary.count(), movieId);

        RatingResponse response = new RatingResponse(ratingSummary.average(), ratingSummary.count());
        return ResponseEntity.ok(response);
    }
}
