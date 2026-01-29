package com.videostore.videostore.infrastructure.persistence.adapter;

import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.MovieSortBy;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.infrastructure.persistence.entity.MovieEntity;
import com.videostore.videostore.infrastructure.persistence.mapper.MovieMapper;
import com.videostore.videostore.infrastructure.persistence.repository.MovieRepositoryJPA;
import com.videostore.videostore.infrastructure.persistence.specification.movie.MovieSpecifications;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public class MovieRepositoryImpl implements MovieRepository {

    private final MovieRepositoryJPA movieRepositoryJPA;

    public MovieRepositoryImpl(MovieRepositoryJPA movieRepositoryJPA) {
        this.movieRepositoryJPA = movieRepositoryJPA;
    }

    @Override
    public Optional<Movie> findById(MovieId id) {
        return movieRepositoryJPA.findById(id.value())
                .map(MovieMapper::toDomain);
    }

    @Override
    public boolean existsById(MovieId id) {
        return movieRepositoryJPA.existsById(id.value());
    }

    @Override
    public List<Movie> findAll(int page, int amount, String genre, boolean onlyAvailable, String title, MovieSortBy sortBy, boolean ascending) {
        Specification<MovieEntity> spec = MovieSpecifications.genreEquals(genre)
                .and(MovieSpecifications.titleContains(title))
                .and(MovieSpecifications.onlyAvailable(onlyAvailable))
                .and(MovieSpecifications.applySorting(sortBy, ascending));

        Pageable pageable = PageRequest.of(page, amount);

        return movieRepositoryJPA.findAll(spec, pageable)
                .stream()
                .map(MovieMapper::toDomain)
                .toList();
    }

    @Override
    public Movie addMovie(Movie movie) {
        MovieEntity entity = MovieMapper.toEntity(movie);

        return MovieMapper.toDomain(movieRepositoryJPA.save(entity));
    }

    @Override
    public Movie updateMovie(Movie movie) {
        MovieEntity entity = MovieMapper.toEntity(movie);

        return MovieMapper.toDomain(movieRepositoryJPA.save(entity));
    }

    @Override
    public void removeMovie(MovieId movieId) {
        movieRepositoryJPA.deleteById(movieId.value());
    }
}
