import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Gameframe extends JFrame implements ActionListener {
    WhacAMole game;
    JButton resetButton;
    JComboBox<String> difficultyBox;

    public Gameframe(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,650);
        setLayout(new BorderLayout());

        // Restart button
        resetButton = new JButton("RESTART");
        resetButton.addActionListener(this);

        // Difficulty selector
        String[] levels = {"Easy", "Medium", "Hard"};
        difficultyBox = new JComboBox<>(levels);
        difficultyBox.addActionListener(this);

        // Panel for controls
        JPanel controlPanel = new JPanel();
        controlPanel.add(resetButton);
        controlPanel.add(difficultyBox);

        // Game panel
        game = new WhacAMole();

        add(game, BorderLayout.CENTER);       // game board in the middle
        add(controlPanel, BorderLayout.SOUTH); // controls at the bottom

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == resetButton){
            game.resetGame();
        } else if(e.getSource() == difficultyBox){
            String level = (String) difficultyBox.getSelectedItem();
            game.setDifficulty(level);   // 👉 call difficulty change
        }
    }
}

