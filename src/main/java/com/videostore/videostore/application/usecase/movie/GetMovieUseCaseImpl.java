package com.videostore.videostore.application.usecase.movie;

import com.videostore.videostore.application.port.in.movie.GetMovieUseCase;
import com.videostore.videostore.domain.exception.MovieNotFoundException;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetMovieUseCaseImpl implements GetMovieUseCase {

    private final MovieRepository movieRepository;

    public GetMovieUseCaseImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Movie execute(Long id) {
        return movieRepository.findById(new MovieId(id)).orElseThrow(() -> new MovieNotFoundException(id));
    }
}
