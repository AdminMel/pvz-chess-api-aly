package com.upiiz.pvz_chess_api.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.upiiz.pvz_chess_api.dto.AcceptMatchRequest;
import com.upiiz.pvz_chess_api.dto.ChallengeRequest;
import com.upiiz.pvz_chess_api.dto.MatchResponse;
import com.upiiz.pvz_chess_api.entities.Match;
import com.upiiz.pvz_chess_api.entities.Player;
import com.upiiz.pvz_chess_api.repositories.MatchRepository;
import com.upiiz.pvz_chess_api.repositories.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchService {

    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final FirebaseMessaging firebaseMessaging;

    // ====================================================
    // 1) Crear reto (challenge)
    // ====================================================
    @Transactional
    public MatchResponse createChallenge(ChallengeRequest request) {
        Long challengerId = request.getChallengerId();
        Long opponentId   = request.getOpponentId();

        if (challengerId == null || opponentId == null) {
            throw new IllegalArgumentException("challengerId y opponentId son obligatorios");
        }
        if (challengerId.equals(opponentId)) {
            throw new IllegalArgumentException("No puedes retarte a ti mismo");
        }

        Player challenger = playerRepository.findById(challengerId)
                .orElseThrow(() -> new IllegalArgumentException("Challenger no encontrado: " + challengerId));

        Player opponent = playerRepository.findById(opponentId)
                .orElseThrow(() -> new IllegalArgumentException("Oponente no encontrado: " + opponentId));

        Match match = new Match();
        match.setChallengerId(challenger.getId());
        match.setChallengedId(opponent.getId());
        match.setStatus("PENDING");               // PENDING | ACCEPTED | FINISHED
        match.setCreatedAt(Instant.now());
        match.setUpdatedAt(Instant.now());

        match = matchRepository.save(match);

        // Notificación FCM al oponente
        sendChallengeNotification(match, challenger, opponent);

        return toResponse(match);
    }

    // ====================================================
    // 2) Aceptar reto
    // ====================================================
    @Transactional
    public MatchResponse acceptMatch(Long matchId, AcceptMatchRequest request) {
        Long accepterId = request.getAccepterId();
        if (accepterId == null) {
            throw new IllegalArgumentException("accepterId es obligatorio");
        }

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match no encontrado: " + matchId));

        if (!"PENDING".equalsIgnoreCase(match.getStatus())) {
            throw new IllegalStateException("El match no está en estado PENDING");
        }

        if (!accepterId.equals(match.getChallengedId())) {
            throw new IllegalArgumentException("Solo el jugador retado puede aceptar la partida");
        }

        Player accepter = playerRepository.findById(accepterId)
                .orElseThrow(() -> new IllegalArgumentException("Jugador que acepta no encontrado: " + accepterId));

        Player challenger = playerRepository.findById(match.getChallengerId())
                .orElseThrow(() -> new IllegalArgumentException("Challenger no encontrado: " + match.getChallengerId()));

        match.setStatus("ACCEPTED");
        match.setUpdatedAt(Instant.now());
        match = matchRepository.save(match);

        sendAcceptedNotification(match, challenger, accepter);

        return toResponse(match);
    }

    // ====================================================
    // 3) Listar matches de un jugador
    // ====================================================
    @Transactional(readOnly = true)
    public List<MatchResponse> listMatchesForPlayer(Long playerId) {
        // Asumiendo un método en el repo:
        // List<Match> findByChallengerIdOrChallengedId(Long cId, Long chId);
        List<Match> matches = matchRepository
                .findByChallengerIdOrChallengedId(playerId, playerId);

        return matches.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ====================================================
    // 4) (Opcional) Marcar match como FINALIZADO y actualizar stats
    // ====================================================
    @Transactional
    public MatchResponse finishMatch(Long matchId, Long winnerPlayerId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match no encontrado: " + matchId));

        if (!"ACCEPTED".equalsIgnoreCase(match.getStatus())) {
            throw new IllegalStateException("Solo se pueden finalizar partidas en estado ACCEPTED");
        }

        Player challenger = playerRepository.findById(match.getChallengerId())
                .orElseThrow(() -> new IllegalArgumentException("Challenger no encontrado: " + match.getChallengerId()));
        Player challenged = playerRepository.findById(match.getChallengedId())
                .orElseThrow(() -> new IllegalArgumentException("Challenged no encontrado: " + match.getChallengedId()));

        // Actualiza stats sencillos
        if (winnerPlayerId != null) {
            if (winnerPlayerId.equals(challenger.getId())) {
                challenger.setWins(challenger.getWins() + 1);
                challenged.setLosses(challenged.getLosses() + 1);
            } else if (winnerPlayerId.equals(challenged.getId())) {
                challenged.setWins(challenged.getWins() + 1);
                challenger.setLosses(challenger.getLosses() + 1);
            }
        }

        challenger.setGamesPlayed(challenger.getGamesPlayed() + 1);
        challenged.setGamesPlayed(challenged.getGamesPlayed() + 1);

        playerRepository.save(challenger);
        playerRepository.save(challenged);

        match.setStatus("FINISHED");
        match.setUpdatedAt(Instant.now());
        match = matchRepository.save(match);

        return toResponse(match);
    }

    // ====================================================
    // Helpers de notificación FCM
    // ====================================================
    private void sendChallengeNotification(Match match, Player challenger, Player opponent) {
        if (opponent.getFcmToken() == null || opponent.getFcmToken().isBlank()) {
            log.warn("El jugador {} no tiene fcmToken, no se envía notificación de reto", opponent.getUsername());
            return;
        }

        Notification notification = Notification
                .builder()
                .setTitle("Nuevo reto en PVZ Chess")
                .setBody(challenger.getUsername() + " quiere jugar contigo 🌱🧟")
                .build();

        Message message = Message.builder()
                .setToken(opponent.getFcmToken())
                .setNotification(notification)
                .putData("type", "CHALLENGE")
                .putData("matchId", String.valueOf(match.getId()))
                .putData("challengerId", String.valueOf(challenger.getId()))
                .putData("challengerName", challenger.getUsername())
                .build();

        try {
            firebaseMessaging.send(message);
            log.info("Notificación de reto enviada a {}", opponent.getUsername());
        } catch (FirebaseMessagingException e) {
            log.error("Error enviando notificación de reto", e);
        }
    }

    private void sendAcceptedNotification(Match match, Player challenger, Player accepter) {
        if (challenger.getFcmToken() == null || challenger.getFcmToken().isBlank()) {
            log.warn("El challenger {} no tiene fcmToken, no se envía notificación de aceptación",
                    challenger.getUsername());
            return;
        }

        Notification notification = Notification
                .builder()
                .setTitle("Reto aceptado 🎮")
                .setBody(accepter.getUsername() + " aceptó tu reto, ¡prepárate para jugar!")
                .build();

        Message message = Message.builder()
                .setToken(challenger.getFcmToken())
                .setNotification(notification)
                .putData("type", "CHALLENGE_ACCEPTED")
                .putData("matchId", String.valueOf(match.getId()))
                .putData("opponentId", String.valueOf(accepter.getId()))
                .putData("opponentName", accepter.getUsername())
                .build();

        try {
            firebaseMessaging.send(message);
            log.info("Notificación de reto aceptado enviada a {}", challenger.getUsername());
        } catch (FirebaseMessagingException e) {
            log.error("Error enviando notificación de reto aceptado", e);
        }
    }

    // ====================================================
    // Helper de mapeo entidad → DTO
    // ====================================================
    private MatchResponse toResponse(Match match) {
        // Ajusta esto a tu DTO real
        return new MatchResponse(
                match.getId(),
                match.getChallengerId(),
                match.getChallengedId(),
                match.getStatus(),
                match.getCreatedAt(),
                match.getUpdatedAt()
        );
    }
}
