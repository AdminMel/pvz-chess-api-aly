package com.upiiz.pvz_chess_api.dto;

public class AcceptMatchRequest {

    private Long accepterId;

    public AcceptMatchRequest() {
    }

    public AcceptMatchRequest(Long accepterId) {
        this.accepterId = accepterId;
    }

    public Long getAccepterId() {
        return accepterId;
    }

    public void setAccepterId(Long accepterId) {
        this.accepterId = accepterId;
    }
}
