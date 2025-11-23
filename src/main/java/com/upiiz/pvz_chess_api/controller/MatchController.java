package com.upiiz.pvz_chess_api.controller;

import com.upiiz.pvz_chess_api.dto.MatchChallengeRequest;
import com.upiiz.pvz_chess_api.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/v1/multiplayer/matches")
@CrossOrigin(origins = "*")
@Tag(name = "Matches", description = "Gestión de retos de partida y notificaciones")
public class MatchController {

    private final NotificationService notificationService;

    public MatchController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(
            summary = "Enviar reto de partida",
            description = """
                    Envía una notificación push (FCM) a un jugador retado.
                    
                    - **challengerId**: jugador que inicia el reto (será ZOMBIE en la partida).
                    - **challengedId**: jugador retado (será PLANT en la partida).
                    - **matchId**: identificador de la partida (definido por la app/juego).
                    
                    La notificación incluye datos en `data`: `type=CHALLENGE`, `matchId`, `fromId`, `fromName`.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Notificación de reto enviada correctamente"
    )
    @ApiResponse(
            responseCode = "400",
            description = "IDs inválidos o jugador sin token FCM registrado"
    )
    @ApiResponse(
            responseCode = "500",
            description = "Error interno al enviar la notificación"
    )
    @PostMapping("/challenge")
    public ResponseEntity<?> challenge(@Valid @RequestBody MatchChallengeRequest req) {
        try {
            notificationService.sendChallengeNotification(
                    req.getChallengerId(),
                    req.getChallengedId(),
                    req.getMatchId()
            );
            return ResponseEntity.ok().body("Notificación de reto enviada correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al enviar notificación: " + e.getMessage());
        }
    }
}
