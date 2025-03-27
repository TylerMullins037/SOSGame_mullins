import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class SOSGame {
    // Enums
    public enum Square { E, S, O }
    public enum GameMode { SIMPLE, GENERAL }

    // Private fields
    private int n;
    private Square[][] board;
    private char blueChoice;
    private char redChoice;
    private char turn;
    private boolean SOSFormed = false;
    private GameMode gameMode;
    private Map<Character, Integer> scores; 
    private List<int[][]> SOSCoordinates = new ArrayList<>();

    // Constructor
    public SOSGame(int n, GameMode gameMode) {
        if (n < 3) {
            this.n = 8;
        } else {
            this.n = n;
            this.gameMode = gameMode;
            blueChoice = 'S';
            redChoice = 'S';
            board = new Square[n][n];
            turn = 'B';
            scores = new HashMap<>();
            scores.put('B', 0);
            scores.put('R', 0);
            initSOSGame();
        }
    }

    // Board and Game State Initialization Methods
    protected void initSOSGame() {
        for (Square[] row : board) {
            Arrays.fill(row, Square.E);
        }
    }

    public int getBoardSize() {
        return n;
    }

    // method to check if the board is full
    public boolean isBoardFull() {
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (board[row][col] == Square.E) {
                    return false;
                }
            }
        }
        return true;
    }

    // method to get the value of a square
    public Square getSquare(int row, int col) {
        if (row >= 0 && row < n && col >= 0 && col < n) {
            return board[row][col];
        } else {
            return Square.E;
        }
    }

    // method to insert a value into a square
    public void setSquare(int row, int col, Square value) {
        if (row >= 0 && row < n && col >= 0 && col < n) {
            board[row][col] = value;
        } else {
            throw new IllegalArgumentException("Invalid board coordinates");
        }
    }

    // Player Choice and Turn Methods
    public void setBlue(char choice) {
        if (choice == 'S' || choice == 'O') {
            blueChoice = choice;
        }
    }
    
    public void setRed(char choice) {
        if (choice == 'S' || choice == 'O') {
            redChoice = choice;
        }
    }

    public char getTurn() {
        return turn;
    }

    // method to swap turns
    public void setTurn() {
        this.turn = (this.turn == 'R') ? 'B' : 'R';
    }

    public char getBlueChoice() {
        return blueChoice;
    }

    public char getRedChoice() {
        return redChoice;
    }

    // method to get the game mode
    public GameMode getGameMode() {
        return gameMode;
    }

    // Method to get a players score
    public int getScore(char player) {
        return scores.get(player);
    }

    // Method to increase a players score
    private void updateScore(char player, int count) {
        scores.put(turn, scores.get(player) + count);
    }
    
    // Method to determine the winner
    public char getWinner() {
        if (getScore('B') > getScore('R')) {
            return 'B';
        } else if (getScore('R') > getScore('B')) {
            return 'R';
        } else {
            return 'D';
        }
    }

    // method to check all directions for all sos sequences
    protected boolean SOSCheck(int row, int col, char turn) {
        int SOSCount = 0;
        int[][] adjacencies = new int[][] {
            {0, 1}, {1, 0}, {1, 1}, {1, -1},  
            {0, -1}, {-1, 0}, {-1, -1}, {-1, 1} 
        };
        Set<String> processedSequences = new HashSet<>();

        for (int[] adjacent : adjacencies) {
            int[][] coordinates = checkAdjacent(row, col, adjacent[0], adjacent[1]);
            if (coordinates != null) {
                String sequenceKey = createSequenceKey(coordinates);
                if (!processedSequences.contains(sequenceKey)) {
                    SOSCoordinates.add(coordinates);
                    processedSequences.add(sequenceKey);
                    SOSCount++;
                }
            }
        }

        if (SOSCount > 0) {
            updateScore(turn, SOSCount);
            return true;
        }
        return false;
    }

    // method to check if the square is in a sequence.
    private int[][] checkAdjacent(int row, int col, int ar, int ac) {
        Square active = getSquare(row, col);

        if (active == Square.S 
            && getSquare(row+ar,col+ac) == Square.O 
            && getSquare(row +2*ar, col +2*ac) == Square.S){
            return new int[][]{{row, col}, {row+ar, col+ac}, {row+2*ar, col+2*ac}};
        }

        if (getSquare(row-ar,col-ac) == Square.S 
            && active == Square.O 
            && getSquare(row +ar, col +ac) == Square.S){
            return new int[][]{{row-ar, col-ac}, {row, col}, {row+ar, col+ac}};
        }

        if (active == Square.S 
            && getSquare(row-ar,col-ac) == Square.O 
            && getSquare(row -2*ar, col -2*ac) == Square.S){
            return new int[][]{{row-2*ar, col-2*ac}, {row-ar, col-ac}, {row, col}};
        }

        return null;
    }

    // This helps keep track of unique SOS sequences
    private String createSequenceKey(int[][] coordinates) {
        Arrays.sort(coordinates, (a, b) -> {
            if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
            return Integer.compare(a[1], b[1]);
        });
        return Arrays.toString(coordinates[0]) + Arrays.toString(coordinates[1]) + Arrays.toString(coordinates[2]);
    }

    // This was implempented to help the gui be able draw lines when an sos is formed.
    public List<int[][]> getSOSLines(int row, int col) {
        List<int[][]> result = new ArrayList<>();
        for (int[][] coordinates : SOSCoordinates) {
            for (int[] point : coordinates) {
                if (point[0] == row && point[1] == col) {
                    result.add(coordinates);
                    break;
                }
            }
        }
        return result;
    }

    // method that checks validity of a move: is the game ongoing, is it in bounds, is it an empty square.
    public boolean isValidMove(int row, int col) {
        return !isGameEnding() && row >= 0 && row < n && col >= 0 && col < n && board[row][col] == Square.E;
    }

    public boolean getSOSFormed() {
        return SOSFormed;
    }

    public void setSOSFormed(boolean SOS) {
        SOSFormed = SOS;
    }
    
    // Abstract Method Implemented by its subclasses
    public abstract boolean makeMove(int row, int col, char choice);
    public abstract boolean isGameEnding();
}
