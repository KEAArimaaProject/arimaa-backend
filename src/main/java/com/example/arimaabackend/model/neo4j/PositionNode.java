package com.example.arimaabackend.model.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Position")
public class PositionNode {

    @Id
    private Integer id;

    private String color;
    private String piece;
    private String cordinate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPiece() {
        return piece;
    }

    public void setPiece(String piece) {
        this.piece = piece;
    }

    public String getCordinate() {
        return cordinate;
    }

    public void setCordinate(String cordinate) {
        this.cordinate = cordinate;
    }
}

