package com.example.arimaabackend.services;

import com.example.arimaabackend.dto.UserCreateRequest;
import com.example.arimaabackend.dto.UserResponse;

public interface UserService {
    UserResponse create(UserCreateRequest request);
    UserResponse getById(Long id);

    void deleteById(Long id);
}

