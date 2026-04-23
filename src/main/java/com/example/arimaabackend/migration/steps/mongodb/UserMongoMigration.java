package com.example.arimaabackend.migration.steps.mongodb;

import java.util.EnumSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.example.arimaabackend.migration.MigrationContext;
import com.example.arimaabackend.migration.spi.MigrationStep;
import com.example.arimaabackend.migration.spi.MigrationTarget;
import com.example.arimaabackend.model.mongo.UserDocument;
import com.example.arimaabackend.model.sql.UserEntity;
import com.example.arimaabackend.repository.mongo.UserMongoRepository;
import com.example.arimaabackend.repository.sql.UserJpaRepository;

@Service
@Profile("migration")
public class UserMongoMigration implements MigrationStep {

    private static final Logger log = LoggerFactory.getLogger(UserMongoMigration.class);

    private final UserJpaRepository userJpaRepository;
    private final UserMongoRepository userMongoRepository;

    public UserMongoMigration(UserJpaRepository userJpaRepository, UserMongoRepository userMongoRepository) {
        this.userJpaRepository = userJpaRepository;
        this.userMongoRepository = userMongoRepository;
    }

    @Override
    public String stepName() {
        return "user-mongo";
    }

    @Override
    public Set<MigrationTarget> targets() {
        return EnumSet.of(MigrationTarget.MONGODB);
    }

    @Override
    public int getOrder() {
        return 121;
    }

    @Override
    public void migrate(MigrationContext context) {
        if (context.dryRun()) {
            log.info("[{}] dry-run: would migrate {} rows", stepName(), userJpaRepository.count());
            return;
        }
        var documents = userJpaRepository.findAll().stream().map(this::toDocument).toList();
        userMongoRepository.saveAll(documents);
        log.info("[{}] migrated {} users", stepName(), documents.size());
    }

    private UserDocument toDocument(UserEntity entity) {
        var document = new UserDocument();
        document.setId(String.valueOf(entity.getId()));
        document.setSqlId(entity.getId());
        document.setUsername(entity.getUsername());
        document.setEmail(entity.getEmail());
        return document;
    }
}
