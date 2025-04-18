// Class for the General Game mode extending from SOSGame
public class SOSGeneralGame extends SOSGame {
    
    public SOSGeneralGame(int size) {
        super(size, GameMode.GENERAL);
        initSOSGame();
    }

    @Override
    public boolean makeMove(int row, int col, char choice) { // General Game move function, which checks for SOS formations and continues turn if no SOS is formed then change turns.
        if (isValidMove(row, col)) {
            board[row][col] = (choice == 'S') ? Square.S : Square.O;
            if (super.SOSCheck(row,col,turn)){ 
                SOSFormed = true;
                // score updates
            } else {
                SOSFormed = false;
                if (!getSOSFormed()){
                    setTurn();
                }
            } 
            return !isGameEnding();
        } 
        return false;
    }

    @Override
    public boolean isGameEnding() {
        return isBoardFull(); // Only ends when board is full
    }

    private boolean isBoardFull() { // Checks for a filled board
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