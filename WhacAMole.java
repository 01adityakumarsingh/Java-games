import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.net.http.*;
import java.time.Instant;
import java.util.Random;
import javax.swing.*;

public class WhacAMole extends JPanel {

    // ─── Backend URL ──────────────────────────────────────────
    private static final String API_BASE = "http://localhost:8080/api";

    // ─── UI Components ────────────────────────────────────────
    JLabel textLabel   = new JLabel();
    JPanel textPanel   = new JPanel();
    JPanel boardPanel  = new JPanel();
    JButton[] board    = new JButton[9];

    ImageIcon moleIcon;
    ImageIcon plantIcon;

    JButton currMoleTile;
    JButton currPlantTile;

    Random random = new Random();
    Timer  setMoleTimer;
    Timer  setPlantTimer;

    int    score           = 0;
    long   gameStartMillis = 0;
    String playerName      = "Player";   // set via setPlayerName()
    String difficulty      = "Medium";   // set via setDifficulty()

    // ─── Constructor ─────────────────────────────────────────
    public WhacAMole() {
        setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Score: 0");
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(3, 3));
        boardPanel.setBackground(Color.DARK_GRAY);
        add(boardPanel, BorderLayout.CENTER);

        Image plantImg = new ImageIcon(getClass().getResource("./piranha.png")).getImage();
        plantIcon = new ImageIcon(plantImg.getScaledInstance(150, 150, Image.SCALE_SMOOTH));

        Image molImage = new ImageIcon(getClass().getResource("./monty.png")).getImage();
        moleIcon = new ImageIcon(molImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH));

        for (int i = 0; i < 9; i++) {
            JButton tile = new JButton();
            board[i] = tile;
            boardPanel.add(tile);
            tile.setFocusable(false);
            tile.addActionListener(e -> handleClick(tile));
        }

        setMoleTimer  = new Timer(1000, e -> spawnMole());
        setPlantTimer = new Timer(1500, e -> spawnPlant());

        gameStartMillis = System.currentTimeMillis();
        setMoleTimer.start();
        setPlantTimer.start();
    }

    // ─── Game Logic ───────────────────────────────────────────

    private void handleClick(JButton tile) {
        if (tile == currMoleTile) {
            score += 10;
            textLabel.setText("Score: " + score);
        } else if (tile == currPlantTile) {
            endGame();
        }
    }

    /**
     * Called when the player hits a plant (game over).
     * Stops timers, disables board, and saves result to backend.
     */
    private void endGame() {
        textLabel.setText("Game Over: " + score);
        setMoleTimer.stop();
        setPlantTimer.stop();
        for (JButton b : board) b.setEnabled(false);

        int durationSeconds = (int) ((System.currentTimeMillis() - gameStartMillis) / 1000);
        saveGameToBackend(score, durationSeconds);
    }

    private void spawnMole() {
        if (currMoleTile != null) {
            currMoleTile.setIcon(null);
            currMoleTile = null;
        }
        int num = random.nextInt(9);
        JButton tile = board[num];
        if (currPlantTile == tile) return;
        currMoleTile = tile;
        currMoleTile.setIcon(moleIcon);
    }

    private void spawnPlant() {
        if (currPlantTile != null) {
            currPlantTile.setIcon(null);
            currPlantTile = null;
        }
        int num = random.nextInt(9);
        JButton tile = board[num];
        if (currMoleTile == tile) return;
        currPlantTile = tile;
        currPlantTile.setIcon(plantIcon);
    }

    // ─── Difficulty ───────────────────────────────────────────

    public void setDifficulty(String level) {
        this.difficulty = level;
        int moleDelay, plantDelay;
        switch (level) {
            case "Easy"   -> { moleDelay = 1200; plantDelay = 1800; }
            case "Hard"   -> { moleDelay = 500;  plantDelay = 1000; }
            default       -> { moleDelay = 800;  plantDelay = 1400; } // Medium
        }
        setMoleTimer.setDelay(moleDelay);
        setPlantTimer.setDelay(plantDelay);
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    // ─── Reset ────────────────────────────────────────────────

    public void resetGame() {
        score = 0;
        textLabel.setText("Score: 0");
        for (JButton b : board) {
            b.setIcon(null);
            b.setEnabled(true);
        }
        gameStartMillis = System.currentTimeMillis();
        setMoleTimer.restart();
        setPlantTimer.restart();
    }

    // ─── Backend API Calls ────────────────────────────────────

    /**
     * POST /api/game/save
     * Sends the completed game result to the Spring Boot backend.
     * Runs on a background thread so it won't freeze the UI.
     */
    private void saveGameToBackend(int finalScore, int durationSeconds) {
        new Thread(() -> {
            try {
                String json = String.format(
                    "{\"playerName\":\"%s\",\"score\":%d,\"difficulty\":\"%s\",\"durationSeconds\":%d}",
                    playerName, finalScore, difficulty, durationSeconds
                );

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/game/save"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

                HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("Game saved. Response: " + response.statusCode());

            } catch (Exception ex) {
                System.err.println("Could not save game to backend: " + ex.getMessage());
            }
        }).start();
    }

    /**
     * GET /api/leaderboard
     * Fetches and prints the global top-10 leaderboard.
     * Call this from Main.java or a Leaderboard button.
     */
    public static String fetchLeaderboard() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + "/leaderboard"))
                .GET()
                .build();

            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception ex) {
            return "Error fetching leaderboard: " + ex.getMessage();
        }
    }

    /**
     * GET /api/stats/{playerName}
     * Fetches stats for the current player.
     */
    public String fetchMyStats() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + "/stats/" + URLEncoder.encode(playerName, "UTF-8")))
                .GET()
                .build();

            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception ex) {
            return "Error fetching stats: " + ex.getMessage();
        }
    }
}
