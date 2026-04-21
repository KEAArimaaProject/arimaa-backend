package com.example.arimaabackend.repository.sql;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.arimaabackend.model.sql.EventEntity;

public interface EventJpaRepository extends JpaRepository<EventEntity, Integer> {
}