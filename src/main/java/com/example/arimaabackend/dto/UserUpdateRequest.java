package com.example.arimaabackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(max = 50) String username,
        @Email @Size(max = 254) String email
) {}

