package com.example.arimaabackend.repository.mongo;

import com.example.arimaabackend.model.mongo.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<UserDocument, Long> {}

