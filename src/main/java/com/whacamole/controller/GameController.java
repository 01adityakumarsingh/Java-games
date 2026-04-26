package com.whacamole.controller;

import com.whacamole.model.GameRecord;
import com.whacamole.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allows requests from your Swing game
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    // ─────────────────────────────────────────────────────────
    // POST /api/game/save
    // Called when a game ends — saves the result to DB
    //
    // Request body (JSON):
    // {
    //   "playerName": "Alice",
    //   "score": 120,
    //   "difficulty": "Hard",
    //   "durationSeconds": 45
    // }
    // ─────────────────────────────────────────────────────────
    @PostMapping("/game/save")
    public ResponseEntity<GameRecord> saveGame(@RequestBody GameRecord record) {
        if (record.getPlayedAt() == null) {
            record.setPlayedAt(java.time.LocalDateTime.now());
        }
        GameRecord saved = gameRepository.save(record);
        return ResponseEntity.ok(saved);
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/leaderboard
    // Returns top 10 scores across all difficulties
    // ─────────────────────────────────────────────────────────
    @GetMapping("/leaderboard")
    public ResponseEntity<List<GameRecord>> getLeaderboard() {
        return ResponseEntity.ok(gameRepository.findTop10ByOrderByScoreDesc());
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/leaderboard/{difficulty}
    // Returns top 10 scores for a given difficulty (Easy/Medium/Hard)
    // ─────────────────────────────────────────────────────────
    @GetMapping("/leaderboard/{difficulty}")
    public ResponseEntity<List<GameRecord>> getLeaderboardByDifficulty(
            @PathVariable String difficulty) {
        return ResponseEntity.ok(
                gameRepository.findTop10ByDifficultyOrderByScoreDesc(difficulty)
        );
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/stats/{playerName}
    // Returns stats summary for a specific player
    // ─────────────────────────────────────────────────────────
    @GetMapping("/stats/{playerName}")
    public ResponseEntity<Map<String, Object>> getPlayerStats(
            @PathVariable String playerName) {

        Map<String, Object> stats = new HashMap<>();
        stats.put("playerName", playerName);
        stats.put("totalGames", gameRepository.countByPlayerName(playerName));
        stats.put("bestScore",  gameRepository.findBestScoreByPlayer(playerName));
        stats.put("avgScore",   gameRepository.findAvgScoreByPlayer(playerName));
        stats.put("recentGames", gameRepository.findByPlayerNameOrderByPlayedAtDesc(playerName));

        return ResponseEntity.ok(stats);
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/stats/global
    // Returns overall game statistics
    // ─────────────────────────────────────────────────────────
    @GetMapping("/stats/global")
    public ResponseEntity<Map<String, Object>> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalGamesPlayed", gameRepository.countAllGames());
        stats.put("highestScoreEver", gameRepository.findHighestScoreEver());
        return ResponseEntity.ok(stats);
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/history/{playerName}
    // Returns full game history for a player (newest first)
    // ─────────────────────────────────────────────────────────
    @GetMapping("/history/{playerName}")
    public ResponseEntity<List<GameRecord>> getPlayerHistory(
            @PathVariable String playerName) {
        return ResponseEntity.ok(
                gameRepository.findByPlayerNameOrderByPlayedAtDesc(playerName)
        );
    }
}
