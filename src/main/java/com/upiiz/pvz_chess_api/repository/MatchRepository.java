package com.upiiz.pvz_chess_api.repository;

import com.upiiz.pvz_chess_api.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    // Todas las partidas donde participa el jugador (como challenger o rival)
    List<Match> findByChallengerIdOrRivalId(Long challengerId, Long rivalId);
}
