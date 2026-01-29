package com.videostore.videostore.application.usecase.rental;

import com.videostore.videostore.application.port.in.rental.GetRentalsByMovieUseCase;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetRentalsByMovieUseCaseImpl implements GetRentalsByMovieUseCase {

    private final RentalRepository rentalRepository;

    public GetRentalsByMovieUseCaseImpl(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> execute(Long movieId) {
        return rentalRepository.findAllByMovie(new MovieId(movieId));
    }
}
