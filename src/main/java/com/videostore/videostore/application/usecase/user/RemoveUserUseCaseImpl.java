package com.videostore.videostore.application.usecase.user;

import com.videostore.videostore.application.port.in.user.RemoveUserUseCase;
import com.videostore.videostore.domain.exception.UserNotFoundException;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.UserId;
import com.videostore.videostore.domain.repository.RentalRepository;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveUserUseCaseImpl implements RemoveUserUseCase {

    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    public RemoveUserUseCaseImpl(UserRepository userRepository, RentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional
    public void execute(Long userId) {
        User user = userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));

        rentalRepository.returnAllByUser(new UserId(userId));
        userRepository.removeUser(user);
    }
}
