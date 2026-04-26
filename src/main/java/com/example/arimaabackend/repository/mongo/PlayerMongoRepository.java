package com.example.arimaabackend.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.arimaabackend.model.mongo.PlayerDocument;

public interface PlayerMongoRepository extends MongoRepository<PlayerDocument, Integer> {}

