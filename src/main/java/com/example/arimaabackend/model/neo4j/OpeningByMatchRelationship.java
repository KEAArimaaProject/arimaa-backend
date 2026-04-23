package com.example.arimaabackend.model.neo4j;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class OpeningByMatchRelationship {

    @Id
    @GeneratedValue
    private Long internalId;

    private Integer id;

    @TargetNode
    private PositionNode position;

    public Long getInternalId() {
        return internalId;
    }

    public void setInternalId(Long internalId) {
        this.internalId = internalId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PositionNode getPosition() {
        return position;
    }

    public void setPosition(PositionNode position) {
        this.position = position;
    }
}
