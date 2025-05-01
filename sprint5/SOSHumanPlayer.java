// Class will be more useful when move history/game statistics is implemented later on.
public class SOSHumanPlayer extends SOSPlayer {
    private int[] selectedMove = null;

    public SOSHumanPlayer(char player) {
        super(player);
    }

    public void setSelectedMove(int row, int col, char choice){
        selectedMove = new int[] {row,col,choice};
    }

    public int[] getSelectedMove(){
        return selectedMove;
    }

    @Override
    public int[] makeMove(SOSGame game) {
        return selectedMove;
    }
}
