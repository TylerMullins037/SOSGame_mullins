
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SOSGUI extends JFrame {

    protected JButton[][] boardButtons;  
    protected JButton blueS, blueO, redS, redO;
    protected JButton newGameButton;
    protected JTextField boardSizeField;
    protected SOSGame game; 
    protected JLabel turnLabel, scoreLabel; 
    protected int boardSize = 8;  
    protected JPanel boardPanel; 
    protected char blueChoice;
    protected char redChoice;
    protected JLabel gameModeLabel;
    protected JComboBox<String> gameModeComboBox;
    protected boolean gameInProgress = false; 
 
    public SOSGUI() {
        super("SOS"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);

        // Set up top control panel
        JPanel topPanel = new JPanel(new FlowLayout());

        // Game Mode Selection Components
        gameModeLabel = new JLabel("Select Mode:");
        gameModeComboBox = new JComboBox<>(new String[]{
            "Simple Game", 
            "General Game", 
        });
        gameModeComboBox.addActionListener(e -> {
            restartGame();
        });
        topPanel.add(gameModeLabel);
        topPanel.add(gameModeComboBox);

        // Board size configuration
        boardSizeField = new JTextField(8);
        boardSizeField.setText(String.valueOf(boardSize));
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            gameInProgress = true;
            rebuildBoardUI();
            updateTurnDisplay();
        });
        topPanel.add(new JLabel("Board Size:"));
        topPanel.add(boardSizeField);
        topPanel.add(newGameButton);

        
        boardPanel = new JPanel(new GridLayout(boardSize, boardSize));
        boardButtons = new JButton[boardSize][boardSize];
        initBoardButtons();

        JPanel bottomPanel = new JPanel();
        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        blueS = new JButton("S");
        blueO = new JButton("O");
        redS = new JButton("S");
        redO = new JButton("O");
        blueS.addActionListener(e -> {
            game.setBlue('S');
            blueS.setBackground(Color.BLUE);
            blueO.setBackground(null);
        });
        blueO.addActionListener(e -> {
            game.setBlue('O');
            blueO.setBackground(Color.BLUE);
            blueS.setBackground(null);
        });
        redS.addActionListener(e -> {
            game.setRed('S');
            redS.setBackground(Color.RED);
            redO.setBackground(null);
        });
        redO.addActionListener(e -> {
            game.setRed('O');
            redO.setBackground(Color.RED);
            redS.setBackground(null);
        });

        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        leftPanel.add(new JLabel("Blue Player:"));
        leftPanel.add(blueS);
        leftPanel.add(blueO);
        rightPanel.add(new JLabel("Red Player:"));
        rightPanel.add(redS);
        rightPanel.add(redO);
        turnLabel = new JLabel("Turn: Blue");
        turnLabel.setForeground(Color.BLUE);
        bottomPanel.add(turnLabel);
        scoreLabel = new JLabel("Blue: 0 | Red: 0");
        bottomPanel.add(scoreLabel);

        add(topPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.EAST);
        add(rightPanel, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
        updateGame(); // Start with the default Simple Mode and default board size (8)

        }

    // Method to clean the board
    private void startGame() {
        for (JButton[] buttonRow : boardButtons) {
            for (JButton button : buttonRow) {
                button.setText("");
                button.setEnabled(true);
            }
        }
    }

    // Method to enact rebuilding
    private void restartGame() {
        updateGame();
        rebuildBoardUI();
        startGame();
    }

    private void winner() {
        // placehold for later sprints
        JOptionPane.showMessageDialog(SOSGUI.this, "Draw Game ");
    }

 // Update turn display
    private void updateTurnDisplay() {
        int redScore = game.getScore('R');
        int blueScore = game.getScore('B');
        scoreLabel.setText("Blue: " + blueScore + " | Red: " + redScore);

        if (game.getTurn() == 'R') {
            turnLabel.setText("Turn: Red");
            turnLabel.setForeground(Color.RED);
        } else {
            turnLabel.setText("Turn: Blue");
            turnLabel.setForeground(Color.BLUE);
        }
    }

 // Update board UI when board size changes
    public void rebuildBoardUI() {
        int newSize;
        try {
            newSize = Integer.parseInt(boardSizeField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid board size.");
            return;
        }
        if (newSize <= 2) {
            JOptionPane.showMessageDialog(this, "Please enter a board size greater than 2.");
            return;
        }

        if (newSize != boardSize) {
            boardSize = newSize;
        }
        rebuildBoardPanel();
        updateGame();
    }

    protected void updateGame() { // Method to initialize SOSGame with a game mode
        String selectedMode = (String) gameModeComboBox.getSelectedItem();
        if (selectedMode.startsWith("Simple")) {
            boardSize = Integer.parseInt(boardSizeField.getText());
            game = new SOSSimpleGame(boardSize);
            startGame();
        } else {
            boardSize = Integer.parseInt(boardSizeField.getText());
            game = new SOSGeneralGame(boardSize);
            startGame();
        }
    }

    private void rebuildBoardPanel() { // Recreates the board
        remove(boardPanel);
        boardPanel = new JPanel(new GridLayout(boardSize, boardSize));
        boardButtons = new JButton[boardSize][boardSize];
        initBoardButtons();
        add(boardPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // Initialize board buttons
    private void initBoardButtons() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                JButton button = new JButton();
                button.addActionListener(new ButtonListener(i, j));  // Button listener for game moves
                boardPanel.add(button);
                boardButtons[i][j] = button;
            }
        }
    }

 // Event Listener for board buttons
    private class ButtonListener implements ActionListener {
        private int row, col;

        public ButtonListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!gameInProgress) {
                // If the game hasn't started, do not allow moves
                JOptionPane.showMessageDialog(SOSGUI.this, "Please start a new game before making moves.");
                return;
            }
            if (game.isValidMove(row, col)) {
                char moveChoice = game.getTurn() == 'B' ? game.blueChoice : game.redChoice; 

                boardButtons[row][col].setText(String.valueOf(moveChoice));
                game.makeMove(row, col, moveChoice);
                boardButtons[row][col].setEnabled(false);

                if (game.isGameEnding()) {
                    winner();
                } else {
                    updateTurnDisplay();
                }
            }
        }
    }

    public static void main(String[] args) {
        new SOSGUI();
    }
}
