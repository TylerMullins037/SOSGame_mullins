// Class for the General Game mode extending from SOSGame
public class SOSGeneralGame extends SOSGame {
    
    public SOSGeneralGame(int size) {
        super(size, GameMode.GENERAL);
        initSOSGame();
    }

    @Override
    public boolean makeMove(int row, int col, char choice) { // General Game move function, which checks for SOS formations and continues turn if no SOS is formed then change turns.
        if (isValidMove(row, col)) {
            if (choice == 'S') {
                setSquare(row,col,Square.S);
            } else{
                setSquare(row,col,Square.O);
            }
            if (SOSCheck(row,col,getTurn())){ 
                setSOSFormed(true);
            } else {
                setSOSFormed(false);
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