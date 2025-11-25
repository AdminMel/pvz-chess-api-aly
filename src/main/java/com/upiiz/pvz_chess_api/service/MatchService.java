package com.upiiz.pvz_chess_api.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.upiiz.pvz_chess_api.dto.AcceptMatchRequest;
import com.upiiz.pvz_chess_api.dto.ChallengeRequest;
import com.upiiz.pvz_chess_api.dto.MatchResponse;
import com.upiiz.pvz_chess_api.model.Match;
import com.upiiz.pvz_chess_api.model.MatchStatus;
import com.upiiz.pvz_chess_api.model.Player;
import com.upiiz.pvz_chess_api.repository.MatchRepository;
import com.upiiz.pvz_chess_api.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

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
        match = matchRepository.save(match);

        // Notificación FCM al rival
        sendNotificationSafe(
                rival.getFcmToken(),
                "Nuevo reto en PvZ Chess",
                challenger.getUsername() + " quiere jugar una partida contigo",
                "challenge",
                match.getId(),
                challengerId,
                rivalId
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
        match = matchRepository.save(match);

        // Notificación FCM al challenger
        sendNotificationSafe(
                challenger.getFcmToken(),
                "Reto aceptado",
                rival.getUsername() + " aceptó tu reto, ¡prepárate para jugar!",
                "challenge_accepted",
                match.getId(),
                match.getChallengerId(),
                match.getRivalId()
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
        return new MatchResponse(
                m.getId(),
                m.getChallengerId(),
                m.getRivalId(),
                m.getStatus().name(),
                m.getCreatedAt()
        );
    }

    private void sendNotificationSafe(
            String fcmToken,
            String title,
            String body,
            String type,
            Long matchId,
            Long challengerId,
            Long rivalId
    ) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            return;
        }

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

        try {
            firebaseMessaging.send(builder.build());
        } catch (Exception e) {
            // No tumbar la app por un fallo enviando notificación
            e.printStackTrace();
        }
    }
}
