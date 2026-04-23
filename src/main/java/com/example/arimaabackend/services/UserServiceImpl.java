package com.example.arimaabackend.services;

import com.example.arimaabackend.enums.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserJpaRepository userJpaRepository,
                           PasswordEncoder passwordEncoder) {
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

@Override
public UserResponse create(UserCreateRequest request) {
    if (userJpaRepository.existsByUsername(request.username())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Username '%s' is already taken".formatted(request.username()));
    }
    if (userJpaRepository.existsByEmail(request.email())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Email '%s' is already registered".formatted(request.email()));
    }

    var entity = new UserEntity();
    entity.setUsername(request.username());
    entity.setEmail(request.email());
    entity.setPassword(passwordEncoder.encode(request.password())); // BCrypt hash
    entity.setRole(UserRole.PLAYER);

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


