public abstract class SOSPlayer {
    private char player;
    private char choice;

    public SOSPlayer(char player) {
        this.player = player;
        this.choice = 'S';
    }

    public char getPlayer() {
        return player;
    }

    public char getChoice() {
        return choice;
    }

    public void setChoice(char newChoice) {
        if (choice == 'S' || choice == 'O'){
            this.choice = newChoice;
        }
    }

    public abstract int[] makeMove(SOSGame game);
}