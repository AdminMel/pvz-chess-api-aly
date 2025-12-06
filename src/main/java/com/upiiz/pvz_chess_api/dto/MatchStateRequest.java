// pvz_chess_api/dto/MatchStateRequest.java
package com.upiiz.pvz_chess_api.dto;

public class MatchStateRequest {

    private String boardState;
    private Long currentTurnPlayerId;
    private Long lastTurnStartTime; // epoch millis

    public MatchStateRequest() {}

    public String getBoardState() {
        return boardState;
    }

    public void setBoardState(String boardState) {
        this.boardState = boardState;
    }

    public Long getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }

    public void setCurrentTurnPlayerId(Long currentTurnPlayerId) {
        this.currentTurnPlayerId = currentTurnPlayerId;
    }

    public Long getLastTurnStartTime() {
        return lastTurnStartTime;
    }

    public void setLastTurnStartTime(Long lastTurnStartTime) {
        this.lastTurnStartTime = lastTurnStartTime;
    }
}
