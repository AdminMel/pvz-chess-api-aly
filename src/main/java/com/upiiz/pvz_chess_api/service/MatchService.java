package com.upiiz.pvz_chess_api.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.upiiz.pvz_chess_api.dto.AcceptMatchRequest;
import com.upiiz.pvz_chess_api.dto.ChallengeRequest;
import com.upiiz.pvz_chess_api.dto.MatchResponse;
import com.upiiz.pvz_chess_api.dto.MatchStateRequest;
import com.upiiz.pvz_chess_api.model.Match;
import com.upiiz.pvz_chess_api.model.MatchStatus;
import com.upiiz.pvz_chess_api.model.Player;
import com.upiiz.pvz_chess_api.repository.MatchRepository;
import com.upiiz.pvz_chess_api.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final FirebaseMessaging firebaseMessaging;

    public MatchService(
            MatchRepository matchRepository,
            PlayerRepository playerRepository,
            FirebaseMessaging firebaseMessaging
    ) {
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.firebaseMessaging = firebaseMessaging;
    }

    // Crear reto (PENDING) + notificación al rival
    public MatchResponse createChallenge(ChallengeRequest request) {
        Long challengerId = request.getChallengerId();
        Long rivalId = request.getRivalId();

        if (challengerId == null || rivalId == null || challengerId.equals(rivalId)) {
            throw new IllegalArgumentException("challengerId y rivalId son obligatorios y deben ser distintos");
        }

        Player challenger = playerRepository.findById(challengerId)
                .orElseThrow(() -> new IllegalArgumentException("Challenger not found: " + challengerId));

        Player rival = playerRepository.findById(rivalId)
                .orElseThrow(() -> new IllegalArgumentException("Rival not found: " + rivalId));

        Match match = new Match(
            challengerId,
            rivalId,
            MatchStatus.PENDING,
            Instant.now()
        );
        
        // ✅ Guardar estado inicial que viene desde Android
        match.setBoardState(request.getBoardState());
        
        // ✅ turno inicial (por default: challenger)
        match.setCurrentTurnPlayerId(
                request.getCurrentTurnPlayerId() != null ? request.getCurrentTurnPlayerId() : challengerId
        );
        
        // ✅ tiempo inicial
        if (request.getLastTurnStartTime() != null) {
            match.setLastTurnStartTime(Instant.ofEpochMilli(request.getLastTurnStartTime()));
        } else {
            match.setLastTurnStartTime(Instant.now());
        }
        match = matchRepository.save(match);

        // Notificación FCM al rival
        sendNotificationSafe(
                rival.getFcmToken(),
                "Nuevo reto en PvZ Chess",
                challenger.getUsername() + " quiere jugar una partida contigo",
                "CHALLENGE",                         // 🔥 IMPORTANTE: coincide con Android
                match.getId(),
                challengerId,
                rivalId,
                challenger.getUsername()             // para challengerName en el cliente
        );

        return toResponse(match);
    }

    // Aceptar reto (PENDING -> ACCEPTED) + notificación al challenger
    public MatchResponse acceptMatch(Long matchId, AcceptMatchRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + matchId));

        if (match.getStatus() != MatchStatus.PENDING) {
            throw new IllegalArgumentException("Solo se pueden aceptar partidas en estado PENDING");
        }

        Long accepterId = request.getAccepterId();
        if (accepterId == null || !accepterId.equals(match.getRivalId())) {
            throw new IllegalArgumentException("Solo el jugador retado puede aceptar este match");
        }

        Match finalMatch1 = match;
        Player challenger = playerRepository.findById(match.getChallengerId())
                .orElseThrow(() -> new IllegalArgumentException("Challenger not found: " + finalMatch1.getChallengerId()));

        Match finalMatch = match;
        Player rival = playerRepository.findById(match.getRivalId())
                .orElseThrow(() -> new IllegalArgumentException("Rival not found: " + finalMatch.getRivalId()));

        match.setStatus(MatchStatus.ACCEPTED);

        if (match.getBoardState() == null || match.getBoardState().isEmpty()) {
            match.setBoardState("");
        }
        if (match.getCurrentTurnPlayerId() == null) {
            match.setCurrentTurnPlayerId(match.getChallengerId());
        }
        if (match.getLastTurnStartTime() == null) {
            match.setLastTurnStartTime(Instant.now());
        }
        
        match = matchRepository.save(match);

        // Notificación FCM al challenger (si luego quieres manejar CHALLENGE_ACCEPTED en Android)
        sendNotificationSafe(
                challenger.getFcmToken(),
                "Reto aceptado",
                rival.getUsername() + " aceptó tu reto, ¡prepárate para jugar!",
                "CHALLENGE_ACCEPTED",
                match.getId(),
                match.getChallengerId(),
                match.getRivalId(),
                rival.getUsername()
        );

        return toResponse(match);
    }

    // Obtener detalle de un match
    public MatchResponse getMatch(Long id) {
        Match m = matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + id));
        return toResponse(m);
    }

    // Listar todas las partidas en las que participa un jugador
    public List<MatchResponse> listMatchesForPlayer(Long playerId) {
        List<Match> matches = matchRepository.findByChallengerIdOrRivalId(playerId, playerId);
        List<MatchResponse> result = new java.util.ArrayList<>();
        for (Match m : matches) {
            result.add(toResponse(m));
        }
        return result;
    }

    // ================== Helpers privados ==================

    private MatchResponse toResponse(Match m) {
    Long lastMillis = (m.getLastTurnStartTime() != null)
            ? m.getLastTurnStartTime().toEpochMilli()
            : null;

    return new MatchResponse(
            m.getId(),
            m.getChallengerId(),
            m.getRivalId(),
            m.getStatus().name(),
            m.getCreatedAt(),
            m.getBoardState(),
            m.getCurrentTurnPlayerId(),
            lastMillis
    );
}



    private void sendNotificationSafe(
            String fcmToken,
            String title,
            String body,
            String type,
            Long matchId,
            Long challengerId,
            Long rivalId,
            String challengerName
    ) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            System.out.println("⚠ No se envía FCM: token vacío o nulo");
            return;
        }

        System.out.println("📨 Enviando FCM a token: " + fcmToken
                + " type=" + type + " matchId=" + matchId);

        Notification notif = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message.Builder builder = Message.builder()
                .setToken(fcmToken)
                .setNotification(notif)
                .putData("type", type);

        if (matchId != null) {
            builder.putData("matchId", String.valueOf(matchId));
        }
        if (challengerId != null) {
            builder.putData("challengerId", String.valueOf(challengerId));
        }
        if (rivalId != null) {
            builder.putData("rivalId", String.valueOf(rivalId));
        }
        if (challengerName != null) {
            builder.putData("challengerName", challengerName);
        }

        try {
            String resp = firebaseMessaging.send(builder.build());
            System.out.println("FCM enviada ok: " + resp);
        } catch (Exception e) {
            System.out.println("Error enviando FCM: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // MatchService.java
    public MatchResponse updateState(Long matchId, MatchStateRequest body) {
        Match m = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found " + matchId));
    
        // Solo partidas activas
        if (m.getStatus() != MatchStatus.ACCEPTED) {
            return toResponse(m);
        }
    
        Long actorId = body.getActorId();
        if (actorId == null) {
            throw new IllegalArgumentException("actorId es obligatorio (manda header X-Player-Id)");
        }
    
        // actor debe pertenecer al match
        boolean isPlayer = actorId.equals(m.getChallengerId()) || actorId.equals(m.getRivalId());
        if (!isPlayer) {
            throw new IllegalArgumentException("actorId no pertenece a este match");
        }
    
        // Si no hay turno en server, default challenger
        if (m.getCurrentTurnPlayerId() == null) {
            m.setCurrentTurnPlayerId(m.getChallengerId());
        }
        if (m.getLastTurnStartTime() == null) {
            m.setLastTurnStartTime(Instant.now());
        }
    
        Long serverTurn = m.getCurrentTurnPlayerId();
    
        // ✅ REGLA #1: SOLO puede escribir el que tiene el turno
        if (!actorId.equals(serverTurn)) {
            // No rompemos: simplemente devolvemos estado actual del server
            return toResponse(m);
        }
    
        // ✅ REGLA #2: No aceptar updates viejos (anti “pisadas”)
        // Si el cliente manda un lastTurnStartTime más viejo que el del server, lo ignoramos.
        if (body.getLastTurnStartTime() != null && m.getLastTurnStartTime() != null) {
            long incoming = body.getLastTurnStartTime();
            long server = m.getLastTurnStartTime().toEpochMilli();
            if (incoming < server) {
                return toResponse(m);
            }
        }
    
        // ✅ Aplicar boardState
        if (body.getBoardState() != null) {
            m.setBoardState(body.getBoardState());
        }
    
        // ✅ El server calcula el siguiente turno (para que el cliente no lo “invente”)
        Long nextTurn = actorId.equals(m.getChallengerId()) ? m.getRivalId() : m.getChallengerId();
        m.setCurrentTurnPlayerId(nextTurn);
    
        // ✅ Reinicia tiempo del turno en server
        m.setLastTurnStartTime(Instant.now());
    
        m = matchRepository.save(m);
        return toResponse(m);
    }
}
