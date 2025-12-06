package com.upiiz.pvz_chess_api.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "challenger_id", nullable = false)
    private Long challengerId;

    @Column(name = "rival_id", nullable = false)
    private Long rivalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MatchStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // Match.java
    private String boardState;           // TEXT
    private Long currentTurnPlayerId;    // BIGINT
    private Instant lastTurnStartTime;


    public Match() {
    }

    public Match(Long id, Long challengerId, Long rivalId, MatchStatus status, Instant createdAt, String boardState, Long currentTurnPlayerId, Instant lastTurnStartTime) {
        this.id = id;
        this.challengerId = challengerId;
        this.rivalId = rivalId;
        this.status = status;
        this.createdAt = createdAt;
        this.boardState = boardState;
        this.currentTurnPlayerId = currentTurnPlayerId;
        this.lastTurnStartTime = lastTurnStartTime;
    }

    public Match(Long challengerId, Long rivalId, MatchStatus matchStatus, Instant now) {
    }

    public Long getId() {
        return id;
    }

    public Long getChallengerId() {
        return challengerId;
    }

    public Long getRivalId() {
        return rivalId;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
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

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

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

    public Instant getLastTurnStartTime() {
        return lastTurnStartTime;
    }

    public void setLastTurnStartTime(Instant lastTurnStartTime) {
        this.lastTurnStartTime = lastTurnStartTime;
    }
}

