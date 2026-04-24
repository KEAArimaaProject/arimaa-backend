package com.example.arimaabackend.migration.steps.mongodb;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.arimaabackend.migration.MigrationContext;
import com.example.arimaabackend.migration.spi.MigrationStep;
import com.example.arimaabackend.migration.spi.MigrationTarget;
import com.example.arimaabackend.model.mongo.PlayerDocument;
import com.example.arimaabackend.model.sql.PlayerEntity;
import com.example.arimaabackend.repository.mongo.PlayerMongoRepository;
import com.example.arimaabackend.repository.sql.PlayerJpaRepository;

@Service
@Profile("migration")
public class PlayerMongoMigration implements MigrationStep {

    private static final Logger log = LoggerFactory.getLogger(PlayerMongoMigration.class);

    private final PlayerJpaRepository playerJpaRepository;
    private final PlayerMongoRepository playerMongoRepository;

    public PlayerMongoMigration(PlayerJpaRepository playerJpaRepository, PlayerMongoRepository playerMongoRepository) {
        this.playerJpaRepository = playerJpaRepository;
        this.playerMongoRepository = playerMongoRepository;
    }

    @Override
    public String stepName() {
        return "player-mongo";
    }

    @Override
    public Set<MigrationTarget> targets() {
        return EnumSet.of(MigrationTarget.MONGODB);
    }

    @Override
    public int getOrder() {
        return 122;
    }

    @Override
    @Transactional(readOnly = true)
    public void migrate(MigrationContext context) {
        if (context.dryRun()) {
            log.info("[{}] dry-run: would migrate {} rows", stepName(), playerJpaRepository.count());
            return;
        }
        List<PlayerDocument> documents = playerJpaRepository.findAll().stream().map(this::toDocument).toList();
        playerMongoRepository.saveAll(documents);
        log.info("[{}] migrated {} players", stepName(), documents.size());
    }

    private PlayerDocument toDocument(PlayerEntity entity) {
        var d = new PlayerDocument();
        d.setId(entity.getId());
        var user = entity.getUser();
        d.setUsername(user != null ? user.getUsername() : null);
        d.setEmail(user != null ? user.getEmail() : null);
        Instant createdAt = user != null ? user.getCreatedAt() : null;
        Instant updatedAt = user != null ? user.getUpdatedAt() : null;
        d.setCreatedAt(createdAt);
        d.setUpdatedAt(updatedAt != null ? updatedAt : createdAt);
        d.setRating(entity.getRating());
        d.setRu(entity.getRu());
        d.setGamesPlayed(entity.getGamesPlayed());
        d.setCountry(entity.getCountry() != null ? entity.getCountry().getName() : null);
        return d;
    }
}

