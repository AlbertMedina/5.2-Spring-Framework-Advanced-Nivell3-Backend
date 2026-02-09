package com.videostore.videostore.infrastructure.persistence.repository;

import com.videostore.videostore.domain.model.user.Role;
import com.videostore.videostore.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryJPA extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findAllByRole(Role role);
    
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
