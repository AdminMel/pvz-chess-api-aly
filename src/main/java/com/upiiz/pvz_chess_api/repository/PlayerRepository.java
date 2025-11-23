package com.upiiz.pvz_chess_api.repository;

import com.upiiz.pvz_chess_api.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByUsername(String username);
}
