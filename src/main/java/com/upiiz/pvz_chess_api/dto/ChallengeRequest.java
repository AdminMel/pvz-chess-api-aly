package com.upiiz.pvz_chess_api.dto;

public class ChallengeRequest {

    private Long challengerId;
    private Long rivalId;

    // ✅ nuevos
    private String boardState;
    private Long currentTurnPlayerId;
    private Long lastTurnStartTime; // epoch millis

    public ChallengeRequest() {}

    public ChallengeRequest(Long challengerId, Long rivalId,
                            String boardState, Long currentTurnPlayerId, Long lastTurnStartTime) {
        this.challengerId = challengerId;
        this.rivalId = rivalId;
        this.boardState = boardState;
        this.currentTurnPlayerId = currentTurnPlayerId;
        this.lastTurnStartTime = lastTurnStartTime;
    }

    public Long getChallengerId() { return challengerId; }
    public void setChallengerId(Long challengerId) { this.challengerId = challengerId; }

    public Long getRivalId() { return rivalId; }
    public void setRivalId(Long rivalId) { this.rivalId = rivalId; }

    public String getBoardState() { return boardState; }
    public void setBoardState(String boardState) { this.boardState = boardState; }

    public Long getCurrentTurnPlayerId() { return currentTurnPlayerId; }
    public void setCurrentTurnPlayerId(Long currentTurnPlayerId) { this.currentTurnPlayerId = currentTurnPlayerId; }

    public Long getLastTurnStartTime() { return lastTurnStartTime; }
    public void setLastTurnStartTime(Long lastTurnStartTime) { this.lastTurnStartTime = lastTurnStartTime; }
}
