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
import com.example.arimaabackend.model.mongo.MatchDocument;
import com.example.arimaabackend.model.sql.MatchEntity;
import com.example.arimaabackend.model.sql.MoveEntity;
import com.example.arimaabackend.repository.mongo.MatchMongoRepository;
import com.example.arimaabackend.repository.sql.MatchJpaRepository;
import com.example.arimaabackend.repository.sql.MoveJpaRepository;

@Service
@Profile("migration")
public class MatchMongoMigration implements MigrationStep {

    private static final Logger log = LoggerFactory.getLogger(MatchMongoMigration.class);

    private final MatchMongoRepository matchMongoRepository;
    private final MatchJpaRepository matchJpaRepository;
    private final MoveJpaRepository moveJpaRepository;
    private final JdbcTemplate jdbcTemplate;

    public MatchMongoMigration(
        MatchMongoRepository matchMongoRepository,
        MatchJpaRepository matchJpaRepository,
        MoveJpaRepository moveJpaRepository,
        JdbcTemplate jdbcTemplate
    ) { 
        this.matchMongoRepository = matchMongoRepository;
        this.matchJpaRepository = matchJpaRepository;
        this.moveJpaRepository = moveJpaRepository;
        this.jdbcTemplate = jdbcTemplate;
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
    public int getOrder() {
        return 123;
    }

    @Override
    @Transactional(readOnly = true)
    public void migrate(MigrationContext context) {
        if (context.dryRun()) {
            log.info("[{}] dry-run: would migrate {} rows", stepName(), matchJpaRepository.count());
            return;
        }
        Map<Integer, List<MatchDocument.Position>> openingByMatchId = loadMatchOpenings();
        List<MatchDocument> documents = matchJpaRepository.findAll().stream()
            .map(m -> toDocument(m, openingByMatchId.get(m.getId())))
            .toList();
        matchMongoRepository.saveAll(documents);
        log.info("[{}] migrated {} matches", stepName(), documents.size());
    }

    private MatchDocument toDocument(MatchEntity e, List<MatchDocument.Position> opening) {
        var d = new MatchDocument();
        d.setId(e.getId());
        d.setTerminationType(e.getTerminationType());
        d.setMatchResult(e.getMatchResult());
        d.setTimestamp(e.getTimestamp());

        d.setPlayers(List.of(
            playerRef(e.getGoldPlayer().getId(), "GOLD"),
            playerRef(e.getSilverPlayer().getId(), "SILVER")
        ));

        d.setEvent(event(e));
        d.setGameType(gameType(e));
        d.setOpening(opening);

        List<MoveEntity> moveEntities = moveJpaRepository.findByMatch_IdOrderByTurnAscSequenceAsc(e.getId());
        d.setMoves(moveEntities.stream().map(this::move).toList());
        return d;
    }

    private MatchDocument.PlayerRef playerRef(Integer playerId, String color) {
        var p = new MatchDocument.PlayerRef();
        p.setPlayerId(playerId);
        p.setColor(color);
        return p;
    }

    private MatchDocument.Event event(MatchEntity e) {
        var src = e.getEvent();
        if (src == null) {
            return null;
        }
        var ev = new MatchDocument.Event();
        ev.setId(src.getId());
        ev.setName(src.getName());
        ev.setIsRated(src.getRated());
        return ev;
    }

    private MatchDocument.GameType gameType(MatchEntity e) {
        var src = e.getGameType();
        if (src == null) {
            return null;
        }
        var gt = new MatchDocument.GameType();
        gt.setId(src.getId());
        gt.setName(src.getName());
        gt.setTimeIncrement(src.getTimeIncrement());
        gt.setTimeReserve(src.getTimeReserve());
        return gt;
    }

    private MatchDocument.Move move(MoveEntity e) {
        var m = new MatchDocument.Move();
        m.setTurn(e.getTurn());
        m.setSequence(e.getSequence());
        m.setDirection(e.getDirection());
        m.setStatus(e.getStatus());
        m.setPosition(position(e.getPosition()));
        return m;
    }

    private MatchDocument.Position position(com.example.arimaabackend.model.sql.PositionEntity p) {
        if (p == null) {
            return null;
        }
        var pos = new MatchDocument.Position();
        pos.setColor(p.getColor());
        pos.setPiece(p.getPiece());
        pos.setCoordinate(p.getCoordinate());
        return pos;
    }

    private Map<Integer, List<MatchDocument.Position>> loadMatchOpenings() {
        var grouped = new HashMap<Integer, List<MatchDocument.Position>>();
        jdbcTemplate.query(
            """
            SELECT obm.matches_id, p.color, p.piece, p.cordinate
            FROM OpeningsByMatch obm
            JOIN Position p ON p.id = obm.position_id
            ORDER BY obm.id ASC
            """,
            rs -> {
                int matchId = rs.getInt("matches_id");
                grouped.computeIfAbsent(matchId, ignored -> new java.util.ArrayList<>()).add(positionRow(
                    rs.getString("color"),
                    rs.getString("piece"),
                    rs.getString("cordinate")
                ));
            }
        );
        return grouped;
    }

    private MatchDocument.Position positionRow(String color, String piece, String coordinate) {
        var p = new MatchDocument.Position();
        p.setColor(color);
        p.setPiece(piece);
        p.setCoordinate(coordinate);
        return p;
    }



    
    
}
