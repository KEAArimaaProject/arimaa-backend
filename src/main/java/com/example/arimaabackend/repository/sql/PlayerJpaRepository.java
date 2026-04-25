package com.example.arimaabackend.repository.sql;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.arimaabackend.model.sql.PlayerEntity;

public interface PlayerJpaRepository extends JpaRepository<PlayerEntity, Integer> {
    Optional<PlayerEntity> findByUsername(String username);

    Optional<PlayerEntity> findByEmail(String email);

    List<PlayerEntity> findByCountryName(String countryName);

}