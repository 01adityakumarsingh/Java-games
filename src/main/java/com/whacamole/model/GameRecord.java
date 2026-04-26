package com.whacamole.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_records")
public class GameRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Player's display name
    @Column(nullable = false)
    private String playerName;

    // Final score for this game
    @Column(nullable = false)
    private int score;

    // Difficulty played: Easy / Medium / Hard
    @Column(nullable = false)
    private String difficulty;

    // How long the game lasted in seconds
    @Column(nullable = false)
    private int durationSeconds;

    // Timestamp of when the game was played
    @Column(nullable = false)
    private LocalDateTime playedAt;

    // ─── Constructors ────────────────────────────────────────

    public GameRecord() {}

    public GameRecord(String playerName, int score, String difficulty, int durationSeconds) {
        this.playerName = playerName;
        this.score = score;
        this.difficulty = difficulty;
        this.durationSeconds = durationSeconds;
        this.playedAt = LocalDateTime.now();
    }

    // ─── Getters & Setters ───────────────────────────────────

    public Long getId() { return id; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public LocalDateTime getPlayedAt() { return playedAt; }
    public void setPlayedAt(LocalDateTime playedAt) { this.playedAt = playedAt; }
}
