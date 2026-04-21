package com.example.arimaabackend;

import com.example.arimaabackend.model.sql.CountryEntity;
import com.example.arimaabackend.model.sql.PlayerEntity;
import com.example.arimaabackend.repository.sql.CountryJpaRepository;
import com.example.arimaabackend.repository.sql.PlayerJpaRepository;
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

    @Test
    void shouldSaveAndLoadPlayerByUsername() {
        var country = new CountryEntity();
        country.setId(1);
        country.setName("US");
        countryJpaRepository.save(country);

        var player = new PlayerEntity();
        player.setId(4803);
        player.setUsername("Matthias");
        player.setPassword("secret");
        player.setCountry(country);

        playerJpaRepository.save(player);

        var loaded = playerJpaRepository.findByUsername("Matthias");

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(4803);
        assertThat(loaded.get().getUsername()).isEqualTo("Matthias");
        assertThat(loaded.get().getCountry().getName()).isEqualTo("US");
    }

    @Test
    void shouldFindPlayersByCountryId() {
        var countryUs = new CountryEntity();
        countryUs.setId(1);
        countryUs.setName("US");
        countryJpaRepository.save(countryUs);

        var countryAu = new CountryEntity();
        countryAu.setId(2);
        countryAu.setName("AU");
        countryJpaRepository.save(countryAu);

        var player1 = new PlayerEntity();
        player1.setId(4803);
        player1.setUsername("Matthias");
        player1.setPassword("secret");
        player1.setCountry(countryUs);
        playerJpaRepository.save(player1);

        var player2 = new PlayerEntity();
        player2.setId(4613);
        player2.setUsername("bot_GnoBot2006P1");
        player2.setPassword("secret");
        player2.setCountry(countryAu);
        playerJpaRepository.save(player2);

        var usPlayers = playerJpaRepository.findByCountry_Id(1);

        assertThat(usPlayers).hasSize(1);
        assertThat(usPlayers.get(0).getUsername()).isEqualTo("Matthias");
    }
}