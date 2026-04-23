package com.example.arimaabackend.model.neo4j;

import java.time.Instant;
import java.util.List;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Match")
public class MatchNode {

    @Id
    private Integer id;

    private String terminationType;
    private String matchResult;
    private Instant timestamp;

    @Relationship(type = "SILVER_PLAYER")
    private PlayerNode silverPlayer;

    @Relationship(type = "GOLD_PLAYER")
    private PlayerNode goldPlayer;

    @Relationship(type = "IN_EVENT")
    private EventNode event;

    @Relationship(type = "HAS_GAMETYPE")
    private GameTypeNode gameType;

    @Relationship(type = "HAS_MOVE")
    private List<MoveNode> moves;

    @Relationship(type = "HAS_OPENING")
    private List<OpeningByMatchRelationship> openings;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTerminationType() {
        return terminationType;
    }

    public void setTerminationType(String terminationType) {
        this.terminationType = terminationType;
    }

    public String getMatchResult() {
        return matchResult;
    }

    public void setMatchResult(String matchResult) {
        this.matchResult = matchResult;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public PlayerNode getSilverPlayer() {
        return silverPlayer;
    }

    public void setSilverPlayer(PlayerNode silverPlayer) {
        this.silverPlayer = silverPlayer;
    }

    public PlayerNode getGoldPlayer() {
        return goldPlayer;
    }

    public void setGoldPlayer(PlayerNode goldPlayer) {
        this.goldPlayer = goldPlayer;
    }

    public EventNode getEvent() {
        return event;
    }

    public void setEvent(EventNode event) {
        this.event = event;
    }

    public GameTypeNode getGameType() {
        return gameType;
    }

    public void setGameType(GameTypeNode gameType) {
        this.gameType = gameType;
    }

    public List<MoveNode> getMoves() {
        return moves;
    }

    public void setMoves(List<MoveNode> moves) {
        this.moves = moves;
    }

    public List<OpeningByMatchRelationship> getOpenings() {
        return openings;
    }

    public void setOpenings(List<OpeningByMatchRelationship> openings) {
        this.openings = openings;
    }
}

