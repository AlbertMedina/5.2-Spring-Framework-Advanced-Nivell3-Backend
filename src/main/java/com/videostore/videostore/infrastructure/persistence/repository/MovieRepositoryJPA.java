package com.videostore.videostore.infrastructure.persistence.repository;

import com.videostore.videostore.infrastructure.persistence.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepositoryJPA extends JpaRepository<MovieEntity, Long>, JpaSpecificationExecutor<MovieEntity> {

    @Query("SELECT DISTINCT LOWER(m.genre) FROM MovieEntity m ORDER BY LOWER(m.genre)")
    List<String> findAllGenres();
}
