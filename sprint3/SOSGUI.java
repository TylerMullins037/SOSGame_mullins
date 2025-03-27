import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
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
    private DrawPanel drawPanel;
    private List<int[]> SOSLines = new ArrayList<>();
 
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

        drawPanel = new DrawPanel();

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

        add(drawPanel);
        drawPanel.setOpaque(false);
        drawPanel.setBounds(0, 0, getWidth(), getHeight());

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
        drawPanel.clearLines();
    }

    private void winner() {
        char winner = game.getWinner();
        if (winner == 'B') {
            JOptionPane.showMessageDialog(SOSGUI.this, "Blue Wins");
        }
        else if (winner == 'R') {
            JOptionPane.showMessageDialog(SOSGUI.this, "Red Wins");
        }
        else {
            JOptionPane.showMessageDialog(SOSGUI.this, "Draw Game");
        }
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
        redS.setBackground(Color.RED);
        blueS.setBackground(Color.BLUE);
        redO.setBackground(null);
        blueO.setBackground(null);
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

    private void drawSOS(int row, int col) {
        List<int[][]> newLines = game.getSOSLines(row, col);
        
        if (!newLines.isEmpty()) {
            Color lineColor = (game.getTurn() == 'B') ? Color.BLUE : Color.RED;
            for (int[][] line : newLines) {
                int[] lineCoords = new int[]{line[0][0], line[0][1], line[2][0], line[2][1]};
                drawPanel.addLine(lineCoords, lineColor);
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
                boardButtons[row][col].setFont(new Font("Arial", Font.BOLD, 24)); 
        
                game.makeMove(row, col, moveChoice);
                boardButtons[row][col].setEnabled(false);
                drawSOS(row, col);
                updateTurnDisplay();
                if (game.isGameEnding()) {
                    winner();
                } 
            }
        }
    }

    private class DrawPanel extends JPanel {
        private List<LineInfo> sosLines = new ArrayList<>();
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3)); // Thicker line
            
            for (LineInfo lineInfo : sosLines) {
                // Use the stored color for each line
                g2.setColor(lineInfo.color);
                
                // Get the center of the first and last buttons in the SOS sequence
                JButton startButton = boardButtons[lineInfo.line[0]][lineInfo.line[1]];
                JButton endButton = boardButtons[lineInfo.line[2]][lineInfo.line[3]];
                
                // Get the absolute coordinates of the board panel
                Point boardPanelLocation = boardPanel.getLocationOnScreen();
                Point thisLocation = getLocationOnScreen();
                
                // Calculate absolute screen coordinates relative to the DrawPanel
                int x1 = startButton.getX() + startButton.getWidth() / 2 - (boardPanelLocation.x - thisLocation.x);
                int y1 = startButton.getY() + startButton.getHeight() / 2 - (boardPanelLocation.y - thisLocation.y);
                int x2 = endButton.getX() + endButton.getWidth() / 2 - (boardPanelLocation.x - thisLocation.x);
                int y2 = endButton.getY() + endButton.getHeight() / 2 - (boardPanelLocation.y - thisLocation.y);
                
                // Draw the line
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    
        // Clear all lines
        public void clearLines() {
            sosLines.clear();
            repaint();
        }
    
        // Add a line with its specific color
        public void addLine(int[] line, Color color) {
            sosLines.add(new LineInfo(line, color));
            repaint();
        }
    
        // Inner class to store line information
        private class LineInfo {
            int[] line;
            Color color;
    
            LineInfo(int[] line, Color color) {
                this.line = line;
                this.color = color;
            }
        }
    }

    public static void main(String[] args) {
        new SOSGUI();
    }
}
