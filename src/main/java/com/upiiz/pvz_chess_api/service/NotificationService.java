package com.upiiz.pvz_chess_api.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.upiiz.pvz_chess_api.model.Player;
import com.upiiz.pvz_chess_api.repository.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final PlayerRepository playerRepository;

    public NotificationService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Envía una notificación FCM al jugador "challengedId"
     * indicando que "challengerId" quiere jugar una partida
     * con id "matchId".
     */
    public void sendChallengeNotification(Long challengerId, Long challengedId, Long matchId) throws Exception {
        Player challenger = playerRepository.findById(challengerId)
                .orElseThrow(() -> new IllegalArgumentException("Challenger no encontrado: " + challengerId));

        Player challenged = playerRepository.findById(challengedId)
                .orElseThrow(() -> new IllegalArgumentException("Challenged no encontrado: " + challengedId));

        String targetToken = challenged.getFcmToken();
        if (targetToken == null || targetToken.isBlank()) {
            throw new IllegalStateException("El jugador retado no tiene token FCM registrado");
        }

        String title = "Nueva invitación de partida";
        String body  = challenger.getUsername() + " quiere una partida contigo";

        // Notificación visible
        Notification notif = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        // Datos adicionales para que Android sepa qué hacer
        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(notif)
                .putData("type", "CHALLENGE")
                .putData("matchId", matchId.toString())
                .putData("fromId", challengerId.toString())
                .putData("fromName", challenger.getUsername())
                .build();

        // Enviar (sincrónico; puedes usar sendAsync si quieres)
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Notificación de reto enviada. FCM response: " + response);
    }
}
