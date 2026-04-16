package com.example.arimaabackend.service;

import com.example.arimaabackend.dto.UserCreateRequest;
import com.example.arimaabackend.dto.UserResponse;

public interface UserService {
    UserResponse create(UserCreateRequest request);

    UserResponse getById(Long id);
}

