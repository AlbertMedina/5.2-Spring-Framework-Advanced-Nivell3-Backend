package com.videostore.videostore.application.usecase.movie;

import com.videostore.videostore.application.port.in.movie.RemoveMovieUseCase;
import com.videostore.videostore.domain.exception.BusinessRuleViolationException;
import com.videostore.videostore.domain.exception.MovieNotFoundException;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveMovieUseCaseImpl implements RemoveMovieUseCase {

    private final MovieRepository movieRepository;
    private final RentalRepository rentalRepository;

    public RemoveMovieUseCaseImpl(MovieRepository movieRepository, RentalRepository rentalRepository) {
        this.movieRepository = movieRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional
    public void execute(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        validateMovieRemoval(movieId);

        movieRepository.removeMovie(movie);
    }

    private void validateMovieRemoval(Long movieId) {
        if (rentalRepository.activeRentalsByMovie(movieId) > 0) {
            throw new BusinessRuleViolationException("Cannot remove movie with active rentals");
        }
    }
}
