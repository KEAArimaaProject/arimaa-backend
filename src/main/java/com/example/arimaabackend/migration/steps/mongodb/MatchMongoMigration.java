package com.example.arimaabackend.migration.steps.mongodb;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.example.arimaabackend.migration.MigrationContext;
import com.example.arimaabackend.migration.spi.MigrationStep;
import com.example.arimaabackend.migration.spi.MigrationTarget;
import com.example.arimaabackend.model.mongo.MatchDocument;
import com.example.arimaabackend.model.sql.MatchEntity;
import com.example.arimaabackend.repository.mongo.MatchMongoRepository;
import com.example.arimaabackend.repository.sql.MatchJpaRepository;

public class MatchMongoMigration implements MigrationStep {

    private final MatchMongoRepository matchMongoRepository;
    private final MatchJpaRepository matchJpaRepository; 

    public MatchMongoMigration(
        MatchMongoRepository matchMongoRepository,
        MatchJpaRepository matchJpaRepository
    ) { 
        this.matchMongoRepository = matchMongoRepository;
        this.matchJpaRepository = matchJpaRepository;
    }


    @Override
    public String stepName() {
        return "match-mongo";
    }

    @Override
    public Set<MigrationTarget> targets() {
        return EnumSet.of(MigrationTarget.MONGODB);
    }

    @Override
    public void migrate(MigrationContext context) {
        List<MatchEntity> sqlMatches = matchJpaRepository.findAll();
    }

    @Override
    public int getOrder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private MatchDocument toDocument(MatchEntity matchEntity) { 
        MatchDocument matchDocument = new MatchDocument();

        return matchDocument;

    }



    
    
}
