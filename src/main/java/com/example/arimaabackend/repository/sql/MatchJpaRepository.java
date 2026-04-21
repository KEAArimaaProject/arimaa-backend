package com.example.arimaabackend.repository.sql;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.arimaabackend.model.sql.MatchEntity;

public interface MatchJpaRepository extends JpaRepository<MatchEntity, Integer> {
    List<MatchEntity> findBySilverPlayer_Id(Integer playerId);

    List<MatchEntity> findByGoldPlayer_Id(Integer playerId);

    List<MatchEntity> findByEvent_Id(Integer eventId);

    List<MatchEntity> findByGameType_Id(Integer gameTypeId);
}