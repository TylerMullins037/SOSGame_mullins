import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class SOSGame {
    public enum Square { E, S, O }
    public enum GameMode { SIMPLE, GENERAL }

    protected int n;
    protected Square[][] board;
	protected char blueChoice; // Players move choice S or O
	protected char redChoice;
    protected char turn; // Current player's turn (Red or Blue)
    protected boolean SOSFormed = false;
    protected GameMode gameMode; // Game mode
    protected Map<Character, Integer> scores; 
    protected List<int[][]> SOSCoordinates = new ArrayList<>();


    public SOSGame(int n, GameMode gameMode) {
        if (n<3) {
            this.n = 8;
        }
        else {
            this.n = n;
            this.gameMode = gameMode;
            blueChoice = 'S'; // Blue player's initial choice
            redChoice = 'S';  // Red player's initial choice
            board = new Square[n][n];
            turn = 'B';
            scores = new HashMap<>(); //  Initial implementation of scores, thought not complete
            scores.put('B', 0);
            scores.put('R', 0);
            initSOSGame();
        }
    }

    protected void initSOSGame() { // Fills the board with and makes them empty
         for (Square[] row : board) {
            Arrays.fill(row, Square.E);
        }
    }

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
            scores.put(turn, scores.get(turn)+(SOSCount));
            return true;
        }
        return false;
    }

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

    private String createSequenceKey(int[][] coordinates) {
        Arrays.sort(coordinates, (a, b) -> {
            if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
            return Integer.compare(a[1], b[1]);
        });
        return Arrays.toString(coordinates[0]) + Arrays.toString(coordinates[1]) + Arrays.toString(coordinates[2]);
    }

    protected boolean isBoardFull() { // Checks for a filled board
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (board[row][col] == Square.E) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public int getBoardSize() { // Getter method to get board size
        return n;
    }

    public Square getSquare(int row, int col) { // function to get the item in a square
        if (row >= 0 && row < n && col >= 0 && col < n) {
            return board[row][col];
        } else {
            return Square.E; // return EMPTY instead of null for out-of-bounds
        }
    }

    public void setBlue(char choice) { //  Method to set Blue Player's move choice
        if (choice == 'S' || choice == 'O') {
            blueChoice = choice;
        }
    }

    public void setRed(char choice) { //  Method to set Red Player's move choice
        if (choice == 'S' || choice == 'O') {
            redChoice = choice;
        }
    }

    public char getTurn() { // getter method to return who's turn it is
        return turn;
    }

    public GameMode getGameMode() {// getter method to get the current game mode
        return gameMode;
    }
    
    public void setTurn() { // function to set turn
        this.turn = (this.turn == 'R') ? 'B' : 'R';
    }

    public boolean isValidMove(int row, int col) { // method that checks validity of move: is the game ongoing, is it in bournds, is it an empty square.
        return !isGameEnding() && row >= 0 && row < n && col >= 0 && col < n && board[row][col] == Square.E;
    }

    public boolean getSOSFormed(){ // Function to get if the last move was an sos formation
        return SOSFormed;
    }

    public int getScore(char player) { // Function to get the score of a player
        return scores.get(player);
    }

    public char getWinner() {
        if(getScore('B')>getScore('R')){
            return 'B';
        }
        else if(getScore('R')>getScore('B')){
                return 'R';
        }
        else {
            return 'D';
        }
    }
    
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
    
    public abstract boolean makeMove(int row, int col, char choice); 

    public abstract boolean isGameEnding();
}
