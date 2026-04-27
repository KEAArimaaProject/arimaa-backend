package com.example.arimaabackend.unittests;

import com.example.arimaabackend.dto.PlayerResponse;
import com.example.arimaabackend.model.sql.CountryEntity;
import com.example.arimaabackend.model.sql.PlayerEntity;
import com.example.arimaabackend.repository.sql.PlayerJpaRepository;
import com.example.arimaabackend.repository.sql.UserJpaRepository;
import com.example.arimaabackend.model.sql.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import com.example.arimaabackend.services.PlayerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerJpaRepository playerJpaRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PlayerServiceImpl playerService;

    private CountryEntity country;
    private UserEntity user1;
    private UserEntity user2;
    private PlayerEntity player1;
    private PlayerEntity player2;

    @BeforeEach
    void setUp() {
        country = new CountryEntity();
        country.setId(1);
        country.setName("US");

        user1 = new UserEntity();
        user1.setUsername("alice");
        user1.setEmail("alice@example.com");
        setField(user1, "id", 1L);

        player1 = new PlayerEntity();
        player1.setId(1);
        player1.setUser(user1);
        player1.setRating(1500);
        player1.setRu(100);
        player1.setGamesPlayed(10);
        player1.setCountry(country);

        user2 = new UserEntity();
        user2.setUsername("bob");
        user2.setEmail("bob@example.com");
        setField(user2, "id", 2L);

        player2 = new PlayerEntity();
        player2.setId(2);
        player2.setUser(user2);
        player2.setRating(1200);
        player2.setRu(50);
        player2.setGamesPlayed(5);
        player2.setCountry(country);
    }

    // --- getByUsername ---

    @Test
    void getByUsername_found_returnsMatchingPlayer() {
        when(userJpaRepository.findByUsername("alice")).thenReturn(Optional.of(user1));
        when(playerJpaRepository.findByUser_Id(1L)).thenReturn(Optional.of(player1));

        PlayerResponse response = playerService.getByUsername("alice");

        assertThat(response.id()).isEqualTo(1);
        assertThat(response.username()).isEqualTo("alice");
        assertThat(response.email()).isEqualTo("alice@example.com");
        assertThat(response.countryId()).isEqualTo(1);
    }

    @Test
    void getByUsername_notFound_throws404() {
        when(userJpaRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.getByUsername("unknown"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found");
    }

    // --- getById ---

    @Test
    void getById_found_returnsMatchingPlayer() {
        when(playerJpaRepository.findById(1)).thenReturn(Optional.of(player1));

        PlayerResponse response = playerService.getById(1);

        assertThat(response.id()).isEqualTo(1);
        assertThat(response.username()).isEqualTo("alice");
    }

    @Test
    void getById_notFound_throws404() {
        when(playerJpaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.getById(99))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found");
    }

    // --- getByCountryId ---

    @Test
    void getByCountryId_returnsPlayersFromCountry() {
        when(playerJpaRepository.findByCountryName("US")).thenReturn(List.of(player1, player2));

        List<PlayerResponse> responses = playerService.getByCountryName("US");

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(PlayerResponse::countryId).containsOnly(1);
        assertThat(responses).extracting(PlayerResponse::username).containsExactly("alice", "bob");
    }

    @Test
    void getByCountryId_noPlayersInCountry_returnsEmptyList() {
        when(playerJpaRepository.findByCountryName("XX")).thenReturn(List.of());

        List<PlayerResponse> responses = playerService.getByCountryName("XX");

        assertThat(responses).isEmpty();
    }

    // --- getAll ---

    @Test
    void getAll_returnsAllPlayers() {
        when(playerJpaRepository.findAll()).thenReturn(List.of(player1, player2));

        List<PlayerResponse> responses = playerService.getAll();

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(PlayerResponse::username).containsExactly("alice", "bob");
    }

    @Test
    void getAll_noPlayers_returnsEmptyList() {
        when(playerJpaRepository.findAll()).thenReturn(List.of());

        List<PlayerResponse> responses = playerService.getAll();

        assertThat(responses).isEmpty();
    }
}
