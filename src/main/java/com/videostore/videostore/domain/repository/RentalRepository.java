package com.videostore.videostore.domain.repository;

import com.videostore.videostore.domain.model.Rental;

import java.util.List;
import java.util.Optional;

public interface RentalRepository {

    Optional<Rental> findById(Long id);

    List<Rental> findAll();

    List<Rental> findByUser(Long userId);

    Rental save(Rental rental);

    void deleteById(Long id);
}
