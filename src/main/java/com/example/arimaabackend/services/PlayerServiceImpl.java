package com.example.arimaabackend.services;

import com.example.arimaabackend.dto.PlayerResponse;
import com.example.arimaabackend.model.sql.PlayerEntity;
import com.example.arimaabackend.repository.sql.PlayerJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class PlayerServiceImpl implements PlayerService {

    private final PlayerJpaRepository playerJpaRepository;

    public PlayerServiceImpl(PlayerJpaRepository playerJpaRepository) {
        this.playerJpaRepository = playerJpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerResponse getByUsername(String username) {
        return playerJpaRepository.findByUsername(username)
                .map(PlayerServiceImpl::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player '%s' not found".formatted(username)));
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerResponse getById(Integer id) {
        return playerJpaRepository.findById(id)
                .map(PlayerServiceImpl::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player %d not found".formatted(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponse> getAll() {
        return playerJpaRepository.findAll().stream()
                .map(PlayerServiceImpl::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponse> getByCountryName(String countryName) {
        return playerJpaRepository.findByCountryName(countryName).stream()
                .map(PlayerServiceImpl::toResponse)
                .toList();
    }

    private static PlayerResponse toResponse(PlayerEntity entity) {
        return new PlayerResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getRating(),
                entity.getRu(),
                entity.getGamesPlayed(),
                entity.getCreateTime(),
                entity.getCountry() != null ? entity.getCountry().getId() : null
        );
    }
}
