package com.upiiz.pvz_chess_api.repository;

import com.upiiz.pvz_chess_api.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
