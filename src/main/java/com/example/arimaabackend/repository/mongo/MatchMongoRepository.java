package com.example.arimaabackend.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.arimaabackend.model.mongo.MatchDocument;

public interface MatchMongoRepository extends MongoRepository<MatchDocument, Integer> {}
