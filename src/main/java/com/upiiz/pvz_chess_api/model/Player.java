package com.upiiz.pvz_chess_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre visible en el juego (único)
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // Opcional: correo u otro identificador
    @Column(nullable = true, length = 100)
    private String email;

    // Token FCM del dispositivo actual
    @Column(nullable = false, length = 500)
    private String fcmToken;

    // Stats básicos
    @Column(nullable = false)
    private int gamesPlayed = 0;

    @Column(nullable = false)
    private int wins = 0;

    @Column(nullable = false)
    private int losses = 0;

    public Player() {}

    public Player(String username, String email, String fcmToken) {
        this.username = username;
        this.email = email;
        this.fcmToken = fcmToken;
    }

    // Getters y setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getFcmToken() { return fcmToken; }

    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    public int getGamesPlayed() { return gamesPlayed; }

    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }

    public int getWins() { return wins; }

    public void setWins(int wins) { this.wins = wins; }

    public int getLosses() { return losses; }

    public void setLosses(int losses) { this.losses = losses; }
}
