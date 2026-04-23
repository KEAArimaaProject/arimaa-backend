package com.example.arimaabackend.model.neo4j;

import java.time.Instant;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Player")
public class PlayerNode {

    @Id
    private Integer id;

    private String username;
    private String email;
    private String password;
    private Instant createTime;
    private Integer rating;
    private Integer ru;
    private Integer gamesPlayed;

    @Relationship(type = "IN_COUNTRY")
    private CountryNode country;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getRu() {
        return ru;
    }

    public void setRu(Integer ru) {
        this.ru = ru;
    }

    public Integer getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(Integer gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public CountryNode getCountry() {
        return country;
    }

    public void setCountry(CountryNode country) {
        this.country = country;
    }
}

