package com.example.arimaabackend.repository.sql;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.arimaabackend.model.sql.OpeningByMatchEntity;

public interface OpeningByMatchJpaRepository extends JpaRepository<OpeningByMatchEntity, Integer> {
    List<OpeningByMatchEntity> findByMatch_Id(Integer matchId);
}