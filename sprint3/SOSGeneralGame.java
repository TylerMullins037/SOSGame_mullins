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
}