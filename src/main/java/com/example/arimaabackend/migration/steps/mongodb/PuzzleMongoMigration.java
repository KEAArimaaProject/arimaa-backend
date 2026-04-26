package com.example.arimaabackend.migration.steps.mongodb;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.arimaabackend.migration.MigrationContext;
import com.example.arimaabackend.migration.spi.MigrationStep;
import com.example.arimaabackend.migration.spi.MigrationTarget;
import com.example.arimaabackend.model.mongo.PuzzleDocument;
import com.example.arimaabackend.model.sql.PuzzleEntity;
import com.example.arimaabackend.model.sql.SolutionEntity;
import com.example.arimaabackend.repository.mongo.PuzzleMongoRepository;
import com.example.arimaabackend.repository.sql.PuzzleJpaRepository;
import com.example.arimaabackend.repository.sql.SolutionJpaRepository;

@Service
@Profile("migration")
public class PuzzleMongoMigration implements MigrationStep {

    private static final Logger log = LoggerFactory.getLogger(PuzzleMongoMigration.class);

    private final PuzzleJpaRepository puzzleJpaRepository;
    private final SolutionJpaRepository solutionJpaRepository;
    private final PuzzleMongoRepository puzzleMongoRepository;
    private final JdbcTemplate jdbcTemplate;

    public PuzzleMongoMigration(
        PuzzleJpaRepository puzzleJpaRepository,
        SolutionJpaRepository solutionJpaRepository,
        PuzzleMongoRepository puzzleMongoRepository,
        JdbcTemplate jdbcTemplate
    ) {
        this.puzzleJpaRepository = puzzleJpaRepository;
        this.solutionJpaRepository = solutionJpaRepository;
        this.puzzleMongoRepository = puzzleMongoRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String stepName() {
        return "puzzle-mongo";
    }

    @Override
    public Set<MigrationTarget> targets() {
        return EnumSet.of(MigrationTarget.MONGODB);
    }

    @Override
    public int getOrder() {
        return 124;
    }

    @Override
    @Transactional(readOnly = true)
    public void migrate(MigrationContext context) {
        if (context.dryRun()) {
            log.info("[{}] dry-run: would migrate {} rows", stepName(), puzzleJpaRepository.count());
            return;
        }
        Map<Integer, List<PuzzleDocument.Position>> openingByPuzzleId = loadPuzzleOpenings();
        List<PuzzleDocument> documents = puzzleJpaRepository.findAll().stream()
            .map(p -> toDocument(p, openingByPuzzleId.get(p.getId())))
            .toList();
        puzzleMongoRepository.saveAll(documents);
        log.info("[{}] migrated {} puzzles", stepName(), documents.size());
    }

    private PuzzleDocument toDocument(PuzzleEntity e, List<PuzzleDocument.Position> opening) {
        var d = new PuzzleDocument();
        d.setId(e.getId());
        d.setName(e.getName());
        d.setObjective(e.getObjective());
        d.setPlayerSide(e.getPlayerSide());
        d.setRounds(e.getRounds());
        d.setOpening(opening);

        List<SolutionEntity> solutionEntities = solutionJpaRepository.findByPuzzle_IdOrderByTurnAscSequenceAsc(e.getId());
        d.setSolution(solutionEntities.stream().map(this::solutionMove).toList());
        return d;
    }

    private PuzzleDocument.SolutionMove solutionMove(SolutionEntity e) {
        var m = new PuzzleDocument.SolutionMove();
        m.setTurn(e.getTurn());
        m.setSequence(e.getSequence());
        m.setDirection(e.getDirection());
        m.setStatus(e.getStatus());
        m.setPosition(position(e.getPosition()));
        return m;
    }

    private PuzzleDocument.Position position(com.example.arimaabackend.model.sql.PositionEntity p) {
        if (p == null) {
            return null;
        }
        var pos = new PuzzleDocument.Position();
        pos.setColor(p.getColor());
        pos.setPiece(p.getPiece());
        pos.setCoordinate(p.getCoordinate());
        return pos;
    }

    private Map<Integer, List<PuzzleDocument.Position>> loadPuzzleOpenings() {
        var grouped = new HashMap<Integer, List<PuzzleDocument.Position>>();
        jdbcTemplate.query(
            """
            SELECT obp.puzzles_id, p.color, p.piece, p.cordinate
            FROM OpeningsByPuzzle obp
            JOIN Position p ON p.id = obp.position_id
            ORDER BY obp.id ASC
            """,
            rs -> {
                int puzzleId = rs.getInt("puzzles_id");
                grouped.computeIfAbsent(puzzleId, ignored -> new java.util.ArrayList<>()).add(positionRow(
                    rs.getString("color"),
                    rs.getString("piece"),
                    rs.getString("cordinate")
                ));
            }
        );
        return grouped;
    }

    private PuzzleDocument.Position positionRow(String color, String piece, String coordinate) {
        var p = new PuzzleDocument.Position();
        p.setColor(color);
        p.setPiece(piece);
        p.setCoordinate(coordinate);
        return p;
    }
}

