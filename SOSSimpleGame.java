// Class for Simple Game mode extending from SOSGame
public class SOSSimpleGame extends SOSGame {
    private boolean gameEnding;

    public SOSSimpleGame(int size) {
        super(size, GameMode.SIMPLE);
        initSOSGame();
    }

    @Override
    public boolean makeMove(int row, int col, char choice) {
        if (isValidMove(row,col)) { // checking within the bounds of the board and if space is empty
            board[row][col] = (choice == 'S') ? Square.S : Square.O; // if 'S' fill in else fill 'O'
            if (super.SOSCheck(row,col,turn)) { // SOS check to end the game (not implemented yet)
                SOSFormed = true;
                gameEnding = true;
            }
            else {
                SOSFormed = false;
                if(!getSOSFormed()) { // Switches turns
                    setTurn();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isGameEnding() { // Ends when an SOS is formed (future addtion) or when the board is full
        return gameEnding || isBoardFull();
    }

    private boolean isBoardFull() { // Checks for an empty board to end the game
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (board[row][col] == Square.E) {
                    return false;
                }
            }
        }
        return true;
    }
}