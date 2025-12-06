package com.upiiz.pvz_chess_api.dto;

import jakarta.validation.constraints.NotNull;

public class MatchChallengeRequest {

    @NotNull
    private Long challengerId;   // el que manda el reto

    @NotNull
    private Long challengedId;   // el que recibe el reto

    @NotNull
    private Long matchId;        // id de la partida (Realtime DB / app)

    public MatchChallengeRequest() {}

    public MatchChallengeRequest(Long challengerId, Long challengedId, Long matchId) {
        this.challengerId = challengerId;
        this.challengedId = challengedId;
        this.matchId = matchId;
    }

    public Long getChallengerId() { return challengerId; }

    public void setChallengerId(Long challengerId) { this.challengerId = challengerId; }

    public Long getChallengedId() { return challengedId; }

    public void setChallengedId(Long challengedId) { this.challengedId = challengedId; }

    public Long getMatchId() { return matchId; }

    public void setMatchId(Long matchId) { this.matchId = matchId; }
}
