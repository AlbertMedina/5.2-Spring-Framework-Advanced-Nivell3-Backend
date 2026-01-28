package com.videostore.videostore.application.usecase.favourite;

import com.videostore.videostore.application.port.in.favourite.GetFavouritesByUserUseCase;
import com.videostore.videostore.domain.model.favourite.Favourite;
import com.videostore.videostore.domain.repository.FavouriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetFavouritesByUserUseCaseImpl implements GetFavouritesByUserUseCase {

    private final FavouriteRepository favouriteRepository;

    public GetFavouritesByUserUseCaseImpl(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favourite> execute(Long userId) {
        return favouriteRepository.findAllByUser(userId);
    }
}
