package com.videostore.videostore.application.usecase.movie;

import com.videostore.videostore.application.port.in.movie.GetAllMoviesUseCase;
import com.videostore.videostore.application.command.movie.GetAllMoviesCommand;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetAllMoviesUseCaseImpl implements GetAllMoviesUseCase {

    private final MovieRepository movieRepository;

    public GetAllMoviesUseCaseImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> execute(GetAllMoviesCommand getAllMoviesCommand) {
        return movieRepository.findAll(
                getAllMoviesCommand.page(),
                getAllMoviesCommand.size(),
                getAllMoviesCommand.genre(),
                getAllMoviesCommand.onlyAvailable(),
                getAllMoviesCommand.title(),
                getAllMoviesCommand.sortBy(),
                getAllMoviesCommand.ascending()
        );
    }
}
