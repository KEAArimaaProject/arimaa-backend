package com.example.arimaabackend.controllers;

import com.example.arimaabackend.dto.PlayerResponse;
import com.example.arimaabackend.services.PlayerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<PlayerResponse> getAll() {
        return playerService.getAll();
    }

    @GetMapping("/by-username/{username}")
    public PlayerResponse getByUsername(@PathVariable String username) {
        return playerService.getByUsername(username);
    }

    @GetMapping("/{id}")
    public PlayerResponse getById(@PathVariable Integer id) {
        return playerService.getById(id);
    }

    @GetMapping("/by-country/{countryName}")
    public List<PlayerResponse> getByCountryName(@PathVariable String countryName) {
        return playerService.getByCountryName(countryName);
    }
}
