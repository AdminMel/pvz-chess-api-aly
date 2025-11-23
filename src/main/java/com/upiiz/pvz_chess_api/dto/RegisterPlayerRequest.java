package com.upiiz.pvz_chess_api.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterPlayerRequest {

    @NotBlank
    private String username;

    private String email;

    @NotBlank
    private String fcmToken;

    public RegisterPlayerRequest() {}

    public RegisterPlayerRequest(String username, String email, String fcmToken) {
        this.username = username;
        this.email = email;
        this.fcmToken = fcmToken;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getFcmToken() { return fcmToken; }

    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
}
