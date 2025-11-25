package com.upiiz.pvz_chess_api.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.upiiz.pvz_chess_api.dto.ChallengeRequest;
import com.upiiz.pvz_chess_api.dto.MatchResponse;
import com.upiiz.pvz_chess_api.model.Match;
import com.upiiz.pvz_chess_api.model.MatchStatus;
import com.upiiz.pvz_chess_api.model.Player;
import com.upiiz.pvz_chess_api.repository.MatchRepository;
import com.upiiz.pvz_chess_api.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

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

    public MatchResponse createChallenge(ChallengeRequest request) {
        Long challengerId = request.getChallengerId();
        Long rivalId = request.getRivalId();

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

        // Enviar notificación FCM al rival
        if (rival.getFcmToken() != null && !rival.getFcmToken().isEmpty()) {
            Notification notif = Notification.builder()
                    .setTitle("Nuevo reto en PvZ Chess")
                    .setBody(challenger.getUsername() + " quiere jugar una partida contigo")
                    .build();

            Message message = Message.builder()
                    .setToken(rival.getFcmToken())
                    .setNotification(notif)
                    .putData("type", "challenge")
                    .putData("matchId", String.valueOf(match.getId()))
                    .putData("challengerId", String.valueOf(challengerId))
                    .putData("rivalId", String.valueOf(rivalId))
                    .build();

            try {
                firebaseMessaging.send(message);
            } catch (Exception e) {
                // No tiramos la API por un fallo de notificación,
                // solo lo registramos en logs si quieres.
                e.printStackTrace();
            }
        }

        return new MatchResponse(
                match.getId(),
                challengerId,
                rivalId,
                match.getStatus().name(),
                match.getCreatedAt()
        );
    }

    public MatchResponse getMatch(Long id) {
        Match m = matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + id));

        return new MatchResponse(
                m.getId(),
                m.getChallengerId(),
                m.getRivalId(),
                m.getStatus().name(),
                m.getCreatedAt()
        );
    }
}
