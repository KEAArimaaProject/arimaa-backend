package com.example.arimaabackend.dto;

import java.time.Instant;

public record UserResponse(
        Long id,
        String username,
        String email,
        Instant createdAt,
        Instant updatedAt
) {}

