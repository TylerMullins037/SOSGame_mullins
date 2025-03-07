import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    public boolean SOSCheck(int row, int col, char turn){
        //placeholder for later sprints to provide 
        //functionality to other classes
        return false;
    }

    public boolean getSOSFormed(){ // Function to get if the last move was an sos formation
        return SOSFormed;
    }

    public int getScore(char player) { // Function to get the score of a player
        return scores.get(player);
    }
    
    public abstract boolean makeMove(int row, int col, char choice); 

    public abstract boolean isGameEnding();
}
