package com.example.arimaabackend.repository.sql;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.arimaabackend.model.sql.PlayerEntity;

public interface PlayerJpaRepository extends JpaRepository<PlayerEntity, Integer> {
    Optional<PlayerEntity> findByUser_Username(String username);

    Optional<PlayerEntity> findByUser_Email(String email);

    List<PlayerEntity> findByCountry_Id(Integer countryId);
}