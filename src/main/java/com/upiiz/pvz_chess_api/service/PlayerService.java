package com.upiiz.pvz_chess_api.service;

import com.upiiz.pvz_chess_api.dto.RegisterPlayerRequest;
import com.upiiz.pvz_chess_api.dto.PlayerResponse;
import com.upiiz.pvz_chess_api.model.Player;
import com.upiiz.pvz_chess_api.repository.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public PlayerResponse registerOrUpdate(RegisterPlayerRequest dto) {
        String username = dto.getUsername();
        String email = dto.getEmail();
        String fcmToken = dto.getFcmToken();

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username es obligatorio");
        }
        if (fcmToken == null || fcmToken.isBlank()) {
            throw new IllegalArgumentException("fcmToken es obligatorio");
        }

        Player player = playerRepository
                .findByUsername(username)
                .orElseGet(() -> new Player(username, email, fcmToken));

        // Si ya existía, solo actualizamos datos que pueden cambiar
        player.setEmail(email);
        player.setFcmToken(fcmToken);

        Player saved = playerRepository.save(player);

        // Mapea a PlayerResponse (puede ser como quieras)
        return new PlayerResponse   (
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getFcmToken(),
                saved.getGamesPlayed(),
                saved.getWins(),
                saved.getLosses()
        );
    }
}
