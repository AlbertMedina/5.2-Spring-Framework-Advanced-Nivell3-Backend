package com.videostore.videostore.application.usecase.favourite;

import com.videostore.videostore.application.command.favourite.AddFavouriteCommand;
import com.videostore.videostore.application.model.FavouriteDetails;
import com.videostore.videostore.application.model.MovieDetails;
import com.videostore.videostore.application.port.in.favourite.AddFavouriteUseCase;
import com.videostore.videostore.domain.common.RatingSummary;
import com.videostore.videostore.domain.exception.conflict.FavouriteAlreadyExistsException;
import com.videostore.videostore.domain.exception.notfound.MovieNotFoundException;
import com.videostore.videostore.domain.exception.notfound.UserNotFoundException;
import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.domain.model.favourite.valueobject.FavouriteDate;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.model.user.valueobject.Username;
import com.videostore.videostore.domain.repository.FavouriteRepository;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.ReviewRepository;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AddFavouriteUseCaseImpl implements AddFavouriteUseCase {

    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;

    public AddFavouriteUseCaseImpl(FavouriteRepository favouriteRepository,
                                   MovieRepository movieRepository,
                                   UserRepository userRepository,
                                   ReviewRepository reviewRepository
    ) {
        this.favouriteRepository = favouriteRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public FavouriteDetails execute(AddFavouriteCommand addFavouriteCommand) {
        Username username = new Username(addFavouriteCommand.username());
        MovieId movieId = new MovieId(addFavouriteCommand.movieId());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username.value()));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId.value()));

        validateFavourite(user.getId(), movieId);

        Favourite favourite = Favourite.create(
                null,
                user.getId(),
                movie.getId(),
                new FavouriteDate(LocalDate.now())
        );

        Favourite newFavourite = favouriteRepository.addFavourite(favourite);

        RatingSummary ratingSummary = reviewRepository.getAverageRatingByMovieId(movieId).orElse(new RatingSummary(0.0, 0));

        return new FavouriteDetails(
                newFavourite.getId().value(),
                newFavourite.getFavouriteDate().value(),
                new MovieDetails(
                        movie.getId().value(),
                        movie.getTitle().value(),
                        movie.getYear().value(),
                        movie.getGenre().value(),
                        movie.getDuration().value(),
                        movie.getDirector().value(),
                        movie.getSynopsis().value(),
                        movie.getNumberOfCopies().value(),
                        movie.getPosterUrl().value(),
                        ratingSummary
                )
        );
    }

    private void validateFavourite(UserId userId, MovieId movieId) {
        if (favouriteRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new FavouriteAlreadyExistsException(userId.value(), movieId.value());
        }
    }
}
