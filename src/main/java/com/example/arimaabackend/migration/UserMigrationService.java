package com.example.arimaabackend.migration;

import org.springframework.stereotype.Service;

import com.example.arimaabackend.repository.mongo.UserMongoRepository;
import com.example.arimaabackend.repository.neo4j.UserNeo4jRepository;
import com.example.arimaabackend.repository.sql.UserJpaRepository;

@Service
public class UserMigrationService {

    private final UserJpaRepository userJpaRepository;
    private final UserMongoRepository userMongoRepository;
    private final UserNeo4jRepository userNeo4jRepository;

    public UserMigrationService(
            UserJpaRepository userJpaRepository,
            UserMongoRepository userMongoRepository,
            UserNeo4jRepository userNeo4jRepository
    ) {
        this.userJpaRepository = userJpaRepository;
        this.userMongoRepository = userMongoRepository;
        this.userNeo4jRepository = userNeo4jRepository;
    }
    
    public void migrateAllUsers() {
    }
}

