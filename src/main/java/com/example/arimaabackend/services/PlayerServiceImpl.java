package com.example.arimaabackend.services;

import com.example.arimaabackend.dto.PlayerResponse;
import com.example.arimaabackend.dto.UserResponse;
import com.example.arimaabackend.model.sql.PlayerEntity;
import com.example.arimaabackend.model.sql.UserEntity;
import com.example.arimaabackend.repository.sql.PlayerJpaRepository;
import com.example.arimaabackend.repository.sql.UserJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class PlayerServiceImpl implements PlayerService {

    private final PlayerJpaRepository playerJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public PlayerServiceImpl(PlayerJpaRepository playerJpaRepository,
                             UserJpaRepository userJpaRepository,
                             PasswordEncoder passwordEncoder) {
        this.playerJpaRepository = playerJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerResponse getByUsername(String username) {

        UserEntity user = userJpaRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User '%s' not found".formatted(username)));

        PlayerResponse player = playerJpaRepository
                .findByUser_Id(user.getId()).map(PlayerServiceImpl::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player '%s' not found".formatted(username)));

        return new PlayerResponse(
                player.id(),
                user.getUsername(),
                user.getEmail(),
                player.rating(),
                player.ru(),
                player.gamesPlayed(),
                player.createTime(),
                player.countryId()
        );
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
        UserEntity user = entity.getUser();
        return new PlayerResponse(
                entity.getId(),
                user != null ? user.getUsername() : "",
                user != null ? user.getEmail() : "",
                entity.getRating(),
                entity.getRu(),
                entity.getGamesPlayed(),
                null,
                entity.getCountry() != null ? entity.getCountry().getId() : null
        );
    }
}
