package com.example.arimaabackend.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.arimaabackend.dto.UserCreateRequest;
import com.example.arimaabackend.dto.UserResponse;
import com.example.arimaabackend.model.sql.UserEntity;
import com.example.arimaabackend.repository.sql.UserJpaRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userJpaRepository;

    public UserServiceImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public UserResponse create(UserCreateRequest request) {
        var entity = new UserEntity();
        entity.setUsername(request.username());
        entity.setEmail(request.email());
        entity = userJpaRepository.save(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return userJpaRepository.findById(id)
                .map(UserServiceImpl::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User %d not found".formatted(id)));
    }

    private static UserResponse toResponse(UserEntity entity) {
        return new UserResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

