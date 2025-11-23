package com.upiiz.pvz_chess_api.controller;

import com.upiiz.pvz_chess_api.dto.PlayerResponse;
import com.upiiz.pvz_chess_api.dto.RegisterPlayerRequest;
import com.upiiz.pvz_chess_api.model.Player;
import com.upiiz.pvz_chess_api.repository.PlayerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/v1/multiplayer/players")
@CrossOrigin(origins = "*")
@Tag(name = "Players", description = "Gestión de jugadores y sus tokens FCM")
public class PlayerController {

    private final PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Operation(
            summary = "Registrar o actualizar jugador",
            description = """
                    Registra un nuevo jugador o actualiza su token FCM si el username ya existe.
                    
                    - **username**: nombre visible en el juego.
                    - **email**: opcional.
                    - **fcmToken**: token de Firebase Cloud Messaging del dispositivo.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Jugador registrado/actualizado correctamente",
            content = @Content(schema = @Schema(implementation = PlayerResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos en la petición"
    )
    @PostMapping("/register")
    public ResponseEntity<PlayerResponse> registerOrUpdate(
            @Valid @RequestBody RegisterPlayerRequest req) {

        Player player = playerRepository.findByUsername(req.getUsername())
                .orElseGet(() -> new Player(
                        req.getUsername(),
                        req.getEmail(),
                        req.getFcmToken()
                ));

        player.setEmail(req.getEmail());
        player.setFcmToken(req.getFcmToken());

        Player saved = playerRepository.save(player);

        PlayerResponse resp = new PlayerResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getGamesPlayed(),
                saved.getWins(),
                saved.getLosses()
        );

        return ResponseEntity.ok(resp);
    }

    @Operation(
            summary = "Listar todos los jugadores",
            description = "Devuelve la lista de jugadores registrados y sus estadísticas básicas."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de jugadores",
            content = @Content(schema = @Schema(implementation = PlayerResponse.class))
    )
    @GetMapping
    public ResponseEntity<List<PlayerResponse>> listPlayers() {
        List<PlayerResponse> list = playerRepository.findAll().stream()
                .map(p -> new PlayerResponse(
                        p.getId(),
                        p.getUsername(),
                        p.getEmail(),
                        p.getGamesPlayed(),
                        p.getWins(),
                        p.getLosses()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    @Operation(
            summary = "Obtener un jugador por ID",
            description = "Devuelve la información y estadísticas de un jugador."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Jugador encontrado",
            content = @Content(schema = @Schema(implementation = PlayerResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Jugador no encontrado"
    )
    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getById(@PathVariable Long id) {
        return playerRepository.findById(id)
                .map(p -> new PlayerResponse(
                        p.getId(),
                        p.getUsername(),
                        p.getEmail(),
                        p.getGamesPlayed(),
                        p.getWins(),
                        p.getLosses()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
