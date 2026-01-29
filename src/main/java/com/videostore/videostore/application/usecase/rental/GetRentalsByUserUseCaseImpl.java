package com.videostore.videostore.application.usecase.rental;

import com.videostore.videostore.application.port.in.rental.GetRentalsByUserUseCase;
import com.videostore.videostore.domain.model.rental.Rental;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetRentalsByUserUseCaseImpl implements GetRentalsByUserUseCase {

    private final RentalRepository rentalRepository;

    public GetRentalsByUserUseCaseImpl(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> execute(Long userId) {
        return rentalRepository.findAllByUser(new UserId(userId));
    }
}
