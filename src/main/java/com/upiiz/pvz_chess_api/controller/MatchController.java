package com.upiiz.pvz_chess_api.controller;

import com.upiiz.pvz_chess_api.dto.AcceptMatchRequest;
import com.upiiz.pvz_chess_api.dto.ChallengeRequest;
import com.upiiz.pvz_chess_api.dto.MatchResponse;
import com.upiiz.pvz_chess_api.dto.MatchStateRequest;
import com.upiiz.pvz_chess_api.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // =========================================================
    // POST /api/matches/challenge
    // Crea un reto entre dos jugadores y envía notificación FCM
    // =========================================================
    @Operation(
            summary = "Crear un reto entre dos jugadores",
            description = """
                    Crea una nueva partida en estado <b>PENDING</b> entre el jugador que lanza el reto
                    (<code>challengerId</code>) y el jugador retado (<code>rivalId</code>).<br><br>
                    Además de registrar el match en la base de datos, el servidor intenta enviar una
                    notificación <b>Firebase Cloud Messaging</b> al jugador retado, utilizando su token FCM
                    registrado previamente.<br><br>
                    Por ahora, el flujo es:<br>
                    1. La app cliente obtiene la lista de jugadores remotos.<br>
                    2. El jugador A selecciona a un rival B y llama a este endpoint.<br>
                    3. El API crea el match y manda la notificación al jugador B.<br>
                    4. La app del jugador B muestra una pantalla de reto recibido.<br>
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reto creado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MatchResponse.class),
                            examples = @ExampleObject(
                                    name = "Match pendiente",
                                    summary = "Ejemplo de respuesta exitosa",
                                    value = """
                                            {
                                              "id": 10,
                                              "challengerId": 1,
                                              "rivalId": 2,
                                              "status": "PENDING",
                                              "createdAt": "2025-11-23T05:32:10.123Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos (por ejemplo, IDs nulos o challengerId = rivalId)",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    name = "Error de validación",
                                    value = "challengerId y rivalId son obligatorios y deben ser distintos"
                            )
                    )
            )
    })
    @PostMapping("/challenge")
    public ResponseEntity<MatchResponse> challenge(
            @RequestBody(
                    required = true,
                    description = "Información del reto: quién reta y a quién",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChallengeRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Reto básico",
                                            summary = "Jugador 1 reta al jugador 2",
                                            value = """
                                                    {
                                                      "challengerId": 1,
                                                      "rivalId": 2
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody ChallengeRequest request
    ) {
        MatchResponse response = matchService.createChallenge(request);
        return ResponseEntity.ok(response);
    }

    // =========================================================
    // POST /api/matches/{id}/accept
    // El jugador retado acepta la partida
    // =========================================================
    @Operation(
            summary = "Aceptar un reto existente",
            description = """
                    Marca un match en estado <b>PENDING</b> como <b>ACCEPTED</b>.<br><br>
                    Este endpoint debe ser llamado por la app del jugador que fue retado
                    (es decir, el que aparece como <code>rivalId</code> en el match).<br><br>
                    Además de actualizar el estado del match, el servidor intentará enviar
                    una notificación FCM al jugador que lanzó el reto para avisar que el
                    reto fue aceptado.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Match aceptado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MatchResponse.class),
                            examples = @ExampleObject(
                                    name = "Match aceptado",
                                    summary = "Respuesta con estado ACCEPTED",
                                    value = """
                                            {
                                              "id": 10,
                                              "challengerId": 1,
                                              "rivalId": 2,
                                              "status": "ACCEPTED",
                                              "createdAt": "2025-11-23T05:32:10.123Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El jugador que intenta aceptar no coincide con el rival del match o el match no está en estado PENDING",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    name = "Match no pendiente o jugador incorrecto",
                                    value = "Solo el jugador retado puede aceptar partidas en estado PENDING"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Match no encontrado",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    name = "Match inexistente",
                                    value = "Match no encontrado: 999"
                            )
                    )
            )
    })
    @PostMapping("/{id}/accept")
    public ResponseEntity<MatchResponse> accept(
            @PathVariable("id") Long matchId,
            @RequestBody(
                    required = true,
                    description = "Datos del jugador que acepta el reto",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AcceptMatchRequest.class),
                            examples = @ExampleObject(
                                    name = "Aceptar match",
                                    summary = "El jugador 2 acepta el match 10",
                                    value = """
                                            {
                                              "accepterId": 2
                                            }
                                            """
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody AcceptMatchRequest request
    ) {
        MatchResponse response = matchService.acceptMatch(matchId, request);
        return ResponseEntity.ok(response);
    }

    // =========================================================
    // GET /api/matches/{id}
    // Obtener detalle de un match
    // =========================================================
    @Operation(
            summary = "Obtener detalle de un match",
            description = """
                    Devuelve la información básica de un match: ids de jugadores, estado actual
                    y fecha de creación.<br><br>
                    Útil para que el cliente móvil recupere el estado de una partida después
                    de recibir una notificación o al reabrir la app.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Match encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MatchResponse.class),
                            examples = @ExampleObject(
                                    name = "Match existente",
                                    summary = "Ejemplo de match en estado PENDING",
                                    value = """
                                            {
                                              "id": 10,
                                              "challengerId": 1,
                                              "rivalId": 2,
                                              "status": "PENDING",
                                              "createdAt": "2025-11-23T05:32:10.123Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Match no encontrado",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    name = "Match inexistente",
                                    value = "Match no encontrado: 10"
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatch(
            @PathVariable("id") Long matchId
    ) {
        MatchResponse response = matchService.getMatch(matchId);
        return ResponseEntity.ok(response);
    }

    // =========================================================
    // GET /api/matches/player/{playerId}
    // Listar matches relacionados con un jugador
    // =========================================================
    @Operation(
            summary = "Listar matches de un jugador",
            description = """
                    Devuelve todas las partidas en las que participa el jugador indicado por
                    <code>playerId</code>, ya sea como retador (<code>challengerId</code>)
                    o como retado (<code>rivalId</code>).<br><br>
                    Puedes usar este endpoint para mostrar el historial de partidas en la app
                    o para depurar el flujo de retos.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de matches (puede estar vacía)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MatchResponse.class),
                            examples = @ExampleObject(
                                    name = "Lista de partidas",
                                    summary = "Ejemplo de historial de un jugador",
                                    value = """
                                            [
                                              {
                                                "id": 10,
                                                "challengerId": 1,
                                                "rivalId": 2,
                                                "status": "PENDING",
                                                "createdAt": "2025-11-23T05:32:10.123Z"
                                              },
                                              {
                                                "id": 11,
                                                "challengerId": 3,
                                                "rivalId": 1,
                                                "status": "ACCEPTED",
                                                "createdAt": "2025-11-23T05:40:01.987Z"
                                              }
                                            ]
                                            """
                            )
                    )
            )
    })
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<MatchResponse>> listMatchesForPlayer(
            @PathVariable("playerId") Long playerId
    ) {
        List<MatchResponse> matches = matchService.listMatchesForPlayer(playerId);
        return ResponseEntity.ok(matches);
    }

    // MatchController.java
    @PutMapping("/{id}/state")
    public MatchResponse updateState(
            @PathVariable Long id,
            @RequestBody MatchStateRequest body
    ) {
        return matchService.updateState(id, body);
    }

}
