package com.upiiz.pvz_chess_api.dto;

import java.time.Instant;

// MatchStateRequest.java
public record MatchStateRequest(
        String boardState,
        Long currentTurnPlayerId,
        Instant lastTurnStartTime
) {}
