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
            if (choice == 'S') {
                setSquare(row,col,Square.S);
            } else{
                setSquare(row,col,Square.O);
            }
            if (SOSCheck(row,col,getTurn())) { // SOS check to end the game (not implemented yet)
                setSOSFormed(true);
                gameEnding = true;
            }
            else {
                setSOSFormed(false);
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
}
