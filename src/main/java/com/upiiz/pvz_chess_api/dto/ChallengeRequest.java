package com.upiiz.pvz_chess_api.dto;

public class ChallengeRequest {

    private Long challengerId;
    private Long rivalId;

    public ChallengeRequest() {
    }

    public ChallengeRequest(Long challengerId, Long rivalId) {
        this.challengerId = challengerId;
        this.rivalId = rivalId;
    }

    public Long getChallengerId() {
        return challengerId;
    }

    public void setChallengerId(Long challengerId) {
        this.challengerId = challengerId;
    }

    public Long getRivalId() {
        return rivalId;
    }

    public void setRivalId(Long rivalId) {
        this.rivalId = rivalId;
    }
}
