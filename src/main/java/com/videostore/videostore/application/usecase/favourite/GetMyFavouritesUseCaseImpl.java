package com.videostore.videostore.application.usecase.favourite;

import com.videostore.videostore.application.model.FavouriteDetails;
import com.videostore.videostore.application.model.MovieDetails;
import com.videostore.videostore.application.model.RentalDetails;
import com.videostore.videostore.application.port.in.favourite.GetMyFavouritesUseCase;
import com.videostore.videostore.domain.common.RatingSummary;
import com.videostore.videostore.domain.exception.notfound.UserNotFoundException;
import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.domain.model.movie.Movie;
import com.videostore.videostore.domain.model.movie.valueobject.MovieId;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.Username;
import com.videostore.videostore.domain.repository.FavouriteRepository;
import com.videostore.videostore.domain.repository.MovieRepository;
import com.videostore.videostore.domain.repository.ReviewRepository;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GetMyFavouritesUseCaseImpl implements GetMyFavouritesUseCase {

    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;

    public GetMyFavouritesUseCaseImpl(FavouriteRepository favouriteRepository,
                                      UserRepository userRepository,
                                      MovieRepository movieRepository,
                                      ReviewRepository reviewRepository
    ) {
        this.favouriteRepository = favouriteRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavouriteDetails> execute(String username) {

        Username usernameVo = new Username(username);
        User user = userRepository.findByUsername(usernameVo)
                .orElseThrow(() -> new UserNotFoundException(username));

        List<Favourite> favourites = favouriteRepository.findAllByUser(user.getId());

        List<MovieId> movieIds = favourites.stream()
                .map(Favourite::getMovieId)
                .distinct()
                .toList();

        Map<MovieId, Movie> movieIdToMovie =
                movieRepository.findAllByIds(movieIds).stream()
                        .collect(Collectors.toMap(Movie::getId, m -> m));

        Map<Long, RatingSummary> movieIdToRating =
                reviewRepository.getAverageRatingsByMovieIds(movieIds);

        return favourites.stream()
                .map(f -> {
                    Movie movie = movieIdToMovie.get(f.getMovieId());

                    RatingSummary rating = movieIdToRating
                            .getOrDefault(f.getMovieId().value(), new RatingSummary(0.0, 0));

                    return new FavouriteDetails(
                            f.getId().value(),
                            f.getFavouriteDate().value(),
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
                                    rating
                            )
                    );
                })
                .toList();
    }
}
