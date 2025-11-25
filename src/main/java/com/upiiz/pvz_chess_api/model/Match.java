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

    public Match() {
    }

    public Match(Long challengerId, Long rivalId, MatchStatus status, Instant createdAt) {
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
}

