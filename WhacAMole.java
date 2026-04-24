import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class WhacAMole extends JPanel {
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    JButton[] board = new JButton[9];
    ImageIcon moleIcon;
    ImageIcon plantIcon;

    JButton currMoleTile;
    JButton currPlantTile;

    Random random = new Random();
    Timer setMoleTimer;
    Timer setPlantTimer;
    int score = 0;

    public WhacAMole(){
        setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Score: 0");
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(3,3));
        boardPanel.setBackground(Color.DARK_GRAY);
        add(boardPanel, BorderLayout.CENTER);

        Image plantImg = new ImageIcon(getClass().getResource("./piranha.png")).getImage();
        plantIcon = new ImageIcon(plantImg.getScaledInstance(150, 150, Image.SCALE_SMOOTH));

        Image molImage = new ImageIcon(getClass().getResource("./monty.png")).getImage();
        moleIcon = new ImageIcon(molImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH));

        for(int i=0; i<9; i++){
            JButton tile = new JButton();
            board[i] = tile;
            boardPanel.add(tile);
            tile.setFocusable(false);
            tile.addActionListener(e -> handleClick(tile));
        }

        setMoleTimer = new Timer(1000, e -> spawnMole());
        setPlantTimer = new Timer(1500, e -> spawnPlant());

        setMoleTimer.start();
        setPlantTimer.start();
    }

    private void handleClick(JButton tile){
        if(tile == currMoleTile){
            score += 10;
            textLabel.setText("Score: " + score);
        } else if(tile == currPlantTile){
            textLabel.setText("Game Over: " + score);
            setMoleTimer.stop();
            setPlantTimer.stop();
            for(JButton b : board) b.setEnabled(false);
        }
    }

    private void spawnMole(){
        if(currMoleTile != null){
            currMoleTile.setIcon(null);
            currMoleTile = null;
        }
        int num = random.nextInt(9);
        JButton tile = board[num];
        if(currPlantTile == tile) return;
        currMoleTile = tile;
        currMoleTile.setIcon(moleIcon);
    }

    private void spawnPlant(){
        if(currPlantTile != null){
            currPlantTile.setIcon(null);
            currPlantTile = null;
        }
        int num = random.nextInt(9);
        JButton tile = board[num];
        if(currMoleTile == tile) return;
        currPlantTile = tile;
        currPlantTile.setIcon(plantIcon);
    }
    public void setDifficulty(String level){
    int moleDelay, plantDelay;

    switch(level){
        case "Easy":
            moleDelay = 1200;
            plantDelay = 1800;
            break;
        case "Medium":
            moleDelay = 800;
            plantDelay = 1400;
            break;
        case "Hard":
            moleDelay = 500;
            plantDelay = 1000;
            break;
        default:
            moleDelay = 1000;
            plantDelay = 1500;
    }

    // Update timers
    setMoleTimer.setDelay(moleDelay);
    setPlantTimer.setDelay(plantDelay);
}


    public void resetGame(){
        score = 0;
        textLabel.setText("Score: 0");
        for(JButton b : board){
            b.setIcon(null);
            b.setEnabled(true);
        }
        setMoleTimer.restart();
        setPlantTimer.restart();
    }
}

