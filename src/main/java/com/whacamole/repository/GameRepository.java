package com.whacamole.repository;

import com.whacamole.model.GameRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<GameRecord, Long> {

    // ─── Leaderboard ─────────────────────────────────────────

    // Top 10 scores globally (all difficulties)
    List<GameRecord> findTop10ByOrderByScoreDesc();

    // Top 10 scores for a specific difficulty
    List<GameRecord> findTop10ByDifficultyOrderByScoreDesc(String difficulty);

    // ─── Player Stats ─────────────────────────────────────────

    // All games played by a specific player (newest first)
    List<GameRecord> findByPlayerNameOrderByPlayedAtDesc(String playerName);

    // Best score ever for a player
    @Query("SELECT MAX(g.score) FROM GameRecord g WHERE g.playerName = :name")
    Integer findBestScoreByPlayer(@Param("name") String playerName);

    // Average score for a player
    @Query("SELECT AVG(g.score) FROM GameRecord g WHERE g.playerName = :name")
    Double findAvgScoreByPlayer(@Param("name") String playerName);

    // Total games played by a player
    long countByPlayerName(String playerName);

    // ─── Global Stats ─────────────────────────────────────────

    // Total number of games ever played
    @Query("SELECT COUNT(g) FROM GameRecord g")
    long countAllGames();

    // Highest score ever recorded
    @Query("SELECT MAX(g.score) FROM GameRecord g")
    Integer findHighestScoreEver();
}
