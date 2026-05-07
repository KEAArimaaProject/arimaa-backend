package com.example.arimaabackend;

import com.example.arimaabackend.model.sql.CountryEntity;
import com.example.arimaabackend.model.sql.PlayerEntity;
import com.example.arimaabackend.model.sql.UserEntity;
import com.example.arimaabackend.repository.sql.CountryJpaRepository;
import com.example.arimaabackend.repository.sql.PlayerJpaRepository;
import com.example.arimaabackend.repository.sql.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PlayerJpaRepositoryTest {

    @Autowired
    private PlayerJpaRepository playerJpaRepository;

    @Autowired
    private CountryJpaRepository countryJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    void shouldSaveAndLoadPlayerByUsername() {
        var country = new CountryEntity();
        country.setId(1);
        country.setName("US");
        countryJpaRepository.save(country);

        var user = new UserEntity();
        user.setUsername("Matthias");
        user.setEmail("matthias@example.com");
        user.setPasswordHash("secret");
        user = userJpaRepository.save(user);

        var player = new PlayerEntity();
        player.setId(4803);
        player.setUser(user);
        player.setCountry(country);

        playerJpaRepository.save(player);

        var loaded = playerJpaRepository.findByUser_Username("Matthias");

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(4803);
        assertThat(loaded.get().getUser().getUsername()).isEqualTo("Matthias");
        assertThat(loaded.get().getCountry().getName()).isEqualTo("US");
    }

    @Test
    void shouldFindPlayersByCountryName() {
        var countryUs = new CountryEntity();
        countryUs.setId(1);
        countryUs.setName("US");
        countryJpaRepository.save(countryUs);

        var countryAu = new CountryEntity();
        countryAu.setId(2);
        countryAu.setName("AU");
        countryJpaRepository.save(countryAu);

        var user1 = new UserEntity();
        user1.setUsername("Matthias");
        user1.setEmail("matthias@example.com");
        user1.setPasswordHash("secret");
        user1 = userJpaRepository.save(user1);

        var player1 = new PlayerEntity();
        player1.setId(4803);
        player1.setUser(user1);
        player1.setCountry(countryUs);
        playerJpaRepository.save(player1);

        var user2 = new UserEntity();
        user2.setUsername("bot_GnoBot2006P1");
        user2.setEmail("gnobot@example.com");
        user2.setPasswordHash("secret");
        user2 = userJpaRepository.save(user2);

        var player2 = new PlayerEntity();
        player2.setId(4613);
        player2.setUser(user2);
        player2.setCountry(countryAu);
        playerJpaRepository.save(player2);

        var usPlayers = playerJpaRepository.findByCountryName("US");

        assertThat(usPlayers).hasSize(1);
        assertThat(usPlayers.get(0).getUser().getUsername()).isEqualTo("Matthias");
    }
}