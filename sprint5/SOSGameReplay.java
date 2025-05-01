import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SOSGameReplay {
    private SOSGame game;
    private List<SOSGameRecorder.MoveRecord> moves;
    private int currentMoveIndex = 0;
    private ReplayListener listener;
    private Timer timer;
    private int replaySpeed = 1000; // milliseconds between moves
    private boolean isPlaying = false;
    private String bluePlayerType;
    private String redPlayerType;
    
    // Interface for UI to listen for replay events
    public interface ReplayListener {
        void onMovePlayed(SOSGameRecorder.MoveRecord move, int currentIndex, int totalMoves);
        void onReplayComplete();
        void onReplayError(String message);
    }
    
    // Constructor
    public SOSGameReplay(SOSGameRecorder.GameRecord gameRecord, List<SOSGameRecorder.MoveRecord> moves, ReplayListener listener) {
        // Create the appropriate game type based on the game mode
        if (gameRecord.getGameMode() == SOSGame.GameMode.SIMPLE) {
            this.game = new SOSSimpleGame(gameRecord.getBoardSize());
        } else {
            this.game = new SOSGeneralGame(gameRecord.getBoardSize());
        }
        
        this.moves = moves;
        this.listener = listener;
        this.bluePlayerType = gameRecord.getBluePlayerType();
        this.redPlayerType = gameRecord.getRedPlayerType();
    }
    
    // Get blue player type
    public String getBluePlayerType() {
        return bluePlayerType;
    }
    
    // Get red player type
    public String getRedPlayerType() {
        return redPlayerType;
    }
    
    // Start the replay from the beginning
    public void start() {
        // Reset the game
        if (game.getGameMode() == SOSGame.GameMode.SIMPLE) {
            game = new SOSSimpleGame(game.getBoardSize());
        } else {
            game = new SOSGeneralGame(game.getBoardSize());
        }
        
        currentMoveIndex = 0;
        isPlaying = true;
        
        scheduleNextMove();
    }
    
    // Pause the replay
    public void pause() {
        isPlaying = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    // Resume the replay from the current position
    public void resume() {
        if (currentMoveIndex < moves.size()) {
            isPlaying = true;
            scheduleNextMove();
        }
    }
    
    // Step forward one move
    public boolean stepForward() {
        if (currentMoveIndex < moves.size()) {
            playMove(moves.get(currentMoveIndex));
            return true;
        }
        return false;
    }
    
    // Step backward one move - requires resetting the game and replaying up to the previous move
    public boolean stepBackward() {
        if (currentMoveIndex > 0) {
            // Reset the game
            if (game.getGameMode() == SOSGame.GameMode.SIMPLE) {
                game = new SOSSimpleGame(game.getBoardSize());
            } else {
                game = new SOSGeneralGame(game.getBoardSize());
            }
            
            // Replay up to the previous move
            for (int i = 0; i < currentMoveIndex - 1; i++) {
                SOSGameRecorder.MoveRecord move = moves.get(i);
                makeMoveSilently(move);
            }
            
            currentMoveIndex--;
            
            // Notify the listener about the current state
            if (listener != null && currentMoveIndex > 0) {
                listener.onMovePlayed(moves.get(currentMoveIndex - 1), currentMoveIndex, moves.size());
            }
            
            return true;
        }
        return false;
    }
    
    // Set the replay speed
    public void setReplaySpeed(int milliseconds) {
        this.replaySpeed = milliseconds;
    }
    
    // Get the current game state
    public SOSGame getGame() {
        return game;
    }
    
    // Schedule the next move
    private void scheduleNextMove() {
        if (!isPlaying || currentMoveIndex >= moves.size()) {
            return;
        }
        
        if (timer != null) {
            timer.cancel();
        }
        
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (currentMoveIndex < moves.size()) {
                    playMove(moves.get(currentMoveIndex));
                    if (currentMoveIndex < moves.size()) {
                        scheduleNextMove();
                    }
                } else {
                    isPlaying = false;
                    if (listener != null) {
                        listener.onReplayComplete();
                    }
                }
            }
        }, replaySpeed);
    }
    
    // Play a single move
    private void playMove(SOSGameRecorder.MoveRecord move) {
        if (move.getPlayer() == 'B') {
            game.setBlue(move.getChoice());
        } else {
            game.setRed(move.getChoice());
        }
        
        // Make sure it's the correct player's turn
        if (game.getTurn() != move.getPlayer()) {
            if (listener != null) {
                listener.onReplayError("Turn mismatch: Expected " + game.getTurn() + ", got " + move.getPlayer());
            }
            return;
        }
        
        boolean result = game.makeMove(move.getRow(), move.getCol(), move.getChoice());
        if (!result && !game.isGameEnding()) {
            if (listener != null) {
                listener.onReplayError("Invalid move at position: " + move.getRow() + "," + move.getCol());
            }
            return;
        }
        
        if (listener != null) {
            listener.onMovePlayed(move, currentMoveIndex + 1, moves.size());
        }
        
        currentMoveIndex++;
        
        // Check if replay is complete
        if (currentMoveIndex >= moves.size()) {
            isPlaying = false;
            if (listener != null) {
                listener.onReplayComplete();
            }
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    }
    
    // Make a move without notifying the listener (used for stepping back)
    private void makeMoveSilently(SOSGameRecorder.MoveRecord move) {
        if (move.getPlayer() == 'B') {
            game.setBlue(move.getChoice());
        } else {
            game.setRed(move.getChoice());
        }
        
        game.makeMove(move.getRow(), move.getCol(), move.getChoice());
    }
    
    // Reset the replay to the beginning
    public void reset() {
        if (game.getGameMode() == SOSGame.GameMode.SIMPLE) {
            game = new SOSSimpleGame(game.getBoardSize());
        } else {
            game = new SOSGeneralGame(game.getBoardSize());
        }
        
        currentMoveIndex = 0;
        isPlaying = false;
        
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    // Check if the replay is currently playing
    public boolean isPlaying() {
        return isPlaying;
    }
    
    // Get the current move index
    public int getCurrentMoveIndex() {
        return currentMoveIndex;
    }
    
    // Get the total number of moves
    public int getTotalMoves() {
        return moves.size();
    }
}