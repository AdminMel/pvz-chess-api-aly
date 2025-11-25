package com.upiiz.pvz_chess_api.dto;

import java.time.Instant;

public class MatchResponse {

    private Long id;
    private Long challengerId;
    private Long rivalId;
    private String status;
    private Instant createdAt;

    public MatchResponse() {
    }

    public MatchResponse(Long id, Long challengerId, Long rivalId, String status, Instant createdAt) {
        this.id = id;
        this.challengerId = challengerId;
        this.rivalId = rivalId;
        this.status = status;
        this.createdAt = createdAt;
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

    public String getStatus() {
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

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
