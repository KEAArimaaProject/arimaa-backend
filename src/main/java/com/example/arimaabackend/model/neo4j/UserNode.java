package com.example.arimaabackend.model.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("User")
public class UserNode {

    @Id
    private Long sqlId;

    private String username;
    private String email;

    public Long getSqlId() {
        return sqlId;
    }

    public void setSqlId(Long sqlId) {
        this.sqlId = sqlId;
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
}

