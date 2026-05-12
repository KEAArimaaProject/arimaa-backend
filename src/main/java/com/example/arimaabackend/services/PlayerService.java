package com.example.arimaabackend.services;

import com.example.arimaabackend.dto.PlayerCreateRequest;
import com.example.arimaabackend.dto.PlayerResponse;

import java.util.List;

public interface PlayerService {
    PlayerResponse create(PlayerCreateRequest request);
    PlayerResponse getByUsername(String username);
    PlayerResponse getById(Integer id);
    List<PlayerResponse> getAll();
    List<PlayerResponse> getByCountryName(String countryName);
    void deleteById(Integer id);
}
