package com.example.arimaabackend.repository.sql;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.arimaabackend.model.sql.CountryEntity;

public interface CountryJpaRepository extends JpaRepository<CountryEntity, Integer> {
}