package com.upiiz.pvz_chess_api.dto;

import java.time.Instant;

public class MatchResponse {

    private Long id;
    private Long challengerId;
    private Long rivalId;
    private String status;
    private Instant createdAt;

    // 🔹 Nuevos campos para sincronizar tablero y turnos
    private String boardState;          // estado serializado del tablero
    private Long currentTurnPlayerId;   // quién tiene el turno
    private Long lastTurnStartTime;     // millis desde epoch (más fácil para Android)

    public MatchResponse() {
    }

    // Constructor "simple" (por si lo sigues usando en algún lado)
    public MatchResponse(Long id,
                         Long challengerId,
                         Long rivalId,
                         String status,
                         Instant createdAt) {
        this.id = id;
        this.challengerId = challengerId;
        this.rivalId = rivalId;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ✅ Constructor COMPLETO, el que usaremos desde MatchService.toResponse(...)
    public MatchResponse(Long id,
                         Long challengerId,
                         Long rivalId,
                         String status,
                         Instant createdAt,
                         String boardState,
                         Long currentTurnPlayerId,
                         Instant lastTurnStartTime) {
        this.id = id;
        this.challengerId = challengerId;
        this.rivalId = rivalId;
        this.status = status;
        this.createdAt = createdAt;
        this.boardState = boardState;
        this.currentTurnPlayerId = currentTurnPlayerId;
        this.lastTurnStartTime = (lastTurnStartTime != null)
                ? lastTurnStartTime.toEpochMilli()
                : null;
    }

    // ===== Getters y setters =====

    public Long getId() {
        return id;
    }

    public Long getChallengerId() {
        return challengerId;
    }

    public Long getRivalId() {
        return rivalId;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getBoardState() {
        return boardState;
    }

    public Long getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }

    public Long getLastTurnStartTime() {
        return lastTurnStartTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setChallengerId(Long challengerId) {
        this.challengerId = challengerId;
    }

    public void setRivalId(Long rivalId) {
        this.rivalId = rivalId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setBoardState(String boardState) {
        this.boardState = boardState;
    }

    public void setCurrentTurnPlayerId(Long currentTurnPlayerId) {
        this.currentTurnPlayerId = currentTurnPlayerId;
    }

    public void setLastTurnStartTime(Long lastTurnStartTime) {
        this.lastTurnStartTime = lastTurnStartTime;
    }
}
