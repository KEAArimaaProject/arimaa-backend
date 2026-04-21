package com.example.arimaabackend.repository.sql;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.arimaabackend.model.sql.OpeningByPuzzleEntity;

public interface OpeningByPuzzleJpaRepository extends JpaRepository<OpeningByPuzzleEntity, Integer> {
    List<OpeningByPuzzleEntity> findByPuzzle_Id(Integer puzzleId);
}