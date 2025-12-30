package com.upiiz.pvz_chess_api.dto;

import java.time.Instant;

public class MatchResponse {

    private Long id;
    private Long challengerId;
    private Long rivalId;
    private String status;
    private Instant createdAt;

    private String boardState;
    private Long currentTurnPlayerId;
    private Long lastTurnStartTime; // ✅ epoch millis

    public MatchResponse() {}

    public MatchResponse(Long id,
                         Long challengerId,
                         Long rivalId,
                         String status,
                         Instant createdAt,
                         String boardState,
                         Long currentTurnPlayerId,
                         Long lastTurnStartTime) {
        this.id = id;
        this.challengerId = challengerId;
        this.rivalId = rivalId;
        this.status = status;
        this.createdAt = createdAt;
        this.boardState = boardState;
        this.currentTurnPlayerId = currentTurnPlayerId;
        this.lastTurnStartTime = lastTurnStartTime;
    }

    public Long getId() { return id; }
    public Long getChallengerId() { return challengerId; }
    public Long getRivalId() { return rivalId; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public String getBoardState() { return boardState; }
    public Long getCurrentTurnPlayerId() { return currentTurnPlayerId; }
    public Long getLastTurnStartTime() { return lastTurnStartTime; }

    public void setId(Long id) { this.id = id; }
    public void setChallengerId(Long challengerId) { this.challengerId = challengerId; }
    public void setRivalId(Long rivalId) { this.rivalId = rivalId; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public void setBoardState(String boardState) { this.boardState = boardState; }
    public void setCurrentTurnPlayerId(Long currentTurnPlayerId) { this.currentTurnPlayerId = currentTurnPlayerId; }
    public void setLastTurnStartTime(Long lastTurnStartTime) { this.lastTurnStartTime = lastTurnStartTime; }
}
