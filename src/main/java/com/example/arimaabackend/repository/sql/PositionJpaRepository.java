package com.example.arimaabackend.repository.sql;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.arimaabackend.model.sql.PositionEntity;

public interface PositionJpaRepository extends JpaRepository<PositionEntity, Integer> {
}