package com.example.arimaabackend.migration.support;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

/**
 * Parameterized Cypher for edges not modeled from the child node side.
 */
@Component
@Profile("migration")
public class Neo4jLinkSupport {

    private final Neo4jClient neo4jClient;

    public Neo4jLinkSupport(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public void mergeHasMoveEdges(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return;
        }
        neo4jClient
                .query(
                        """
                        UNWIND $rows AS row
                        MATCH (m:Match {id: row.matchId})
                        MATCH (v:Move {id: row.moveId})
                        MERGE (m)-[:HAS_MOVE]->(v)
                        """
                )
                .bindAll(Map.of("rows", rows))
                .run();
    }

    public void mergeMovePositionEdges(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return;
        }
        neo4jClient
                .query(
                        """
                        UNWIND $rows AS row
                        MATCH (v:Move {id: row.moveId})
                        MATCH (p:Position {id: row.positionId})
                        MERGE (v)-[:AT_POSITION]->(p)
                        """
                )
                .bindAll(Map.of("rows", rows))
                .run();
    }

    public void mergeHasSolutionEdges(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return;
        }
        neo4jClient
                .query(
                        """
                        UNWIND $rows AS row
                        MATCH (z:Puzzle {id: row.puzzleId})
                        MATCH (s:Solution {id: row.solutionId})
                        MERGE (z)-[:HAS_SOLUTION]->(s)
                        """
                )
                .bindAll(Map.of("rows", rows))
                .run();
    }

    public void mergeSolutionPositionEdges(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return;
        }
        neo4jClient
                .query(
                        """
                        UNWIND $rows AS row
                        MATCH (s:Solution {id: row.solutionId})
                        MATCH (p:Position {id: row.positionId})
                        MERGE (s)-[:AT_POSITION]->(p)
                        """
                )
                .bindAll(Map.of("rows", rows))
                .run();
    }

    public void mergePlayerCountryEdges(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return;
        }
        neo4jClient
                .query(
                        """
                        UNWIND $rows AS row
                        MATCH (p:Player {id: row.playerId})
                        MATCH (c:Country {id: row.countryId})
                        MERGE (p)-[:IN_COUNTRY]->(c)
                        """
                )
                .bindAll(Map.of("rows", rows))
                .run();
    }

    public void mergeMatchReferenceEdges(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return;
        }
        neo4jClient
                .query(
                        """
                        UNWIND $rows AS row
                        MATCH (m:Match {id: row.matchId})
                        MATCH (silver:Player {id: row.silverPlayerId})
                        MATCH (gold:Player {id: row.goldPlayerId})
                        MATCH (e:Event {id: row.eventId})
                        MATCH (g:GameType {id: row.gameTypeId})
                        MERGE (m)-[:SILVER_PLAYER]->(silver)
                        MERGE (m)-[:GOLD_PLAYER]->(gold)
                        MERGE (m)-[:IN_EVENT]->(e)
                        MERGE (m)-[:HAS_GAMETYPE]->(g)
                        """
                )
                .bindAll(Map.of("rows", rows))
                .run();
    }

    public void mergeOpeningByMatch(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return;
        }
        neo4jClient
                .query(
                        """
                        UNWIND $rows AS row
                        MATCH (m:Match {id: row.matchId})
                        MATCH (p:Position {id: row.positionId})
                        MERGE (m)-[r:HAS_OPENING]->(p)
                        SET r.id = row.openingId
                        """
                )
                .bindAll(Map.of("rows", rows))
                .run();
    }

    public void mergeOpeningByPuzzle(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return;
        }
        neo4jClient
                .query(
                        """
                        UNWIND $rows AS row
                        MATCH (z:Puzzle {id: row.puzzleId})
                        MATCH (p:Position {id: row.positionId})
                        MERGE (z)-[r:HAS_OPENING]->(p)
                        SET r.id = row.openingId
                        """
                )
                .bindAll(Map.of("rows", rows))
                .run();
    }
}
