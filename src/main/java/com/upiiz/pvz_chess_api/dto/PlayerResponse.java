package com.upiiz.pvz_chess_api.dto;

public class PlayerResponse {

    private Long id;
    private String username;
    private String email;
    private int gamesPlayed;
    private int wins;
    private int losses;

    public PlayerResponse() {}

    public PlayerResponse(Long id, String username, String email,
                          int gamesPlayed, int wins, int losses) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.losses = losses;
    }

    public PlayerResponse(Long id, String username, String email, String fcmToken, int gamesPlayed, int wins, int losses) {
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public int getGamesPlayed() { return gamesPlayed; }

    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }

    public int getWins() { return wins; }

    public void setWins(int wins) { this.wins = wins; }

    public int getLosses() { return losses; }

    public void setLosses(int losses) { this.losses = losses; }
}
