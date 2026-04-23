package com.example.arimaabackend.repository.mongo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.arimaabackend.model.mongo.MatchDocument;

public interface MatchMongoRepository extends JpaRepository<MatchDocument, String> {}
