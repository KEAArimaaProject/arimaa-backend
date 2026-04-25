package com.example.arimaabackend.services;

import com.example.arimaabackend.dto.PlayerResponse;

import java.util.List;

public interface PlayerService {
    PlayerResponse getByUsername(String username);
    PlayerResponse getById(Integer id);
    List<PlayerResponse> getAll();
    List<PlayerResponse> getByCountryName(String countryName);
}
