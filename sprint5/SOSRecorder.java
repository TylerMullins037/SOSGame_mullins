import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SOSRecorder {
    private Connection connection;
    private int currentGameId = -1;
    
    private static final String DB_URL = "jdbc:sqlite:sosgames.db";
    
    // Table creation SQL
    private static final String CREATE_GAMES_TABLE = 
        "CREATE TABLE IF NOT EXISTS games (" +
        "game_id INTEGER PRIMARY KEY AUTOINCREMENT," +
        "board_size INTEGER NOT NULL," +
        "game_mode TEXT NOT NULL," +
        "blue_player_type TEXT NOT NULL," +
        "red_player_type TEXT NOT NULL," +
        "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
    
    private static final String CREATE_MOVES_TABLE = 
        "CREATE TABLE IF NOT EXISTS moves (" +
        "move_id INTEGER PRIMARY KEY AUTOINCREMENT," +
        "game_id INTEGER NOT NULL," +
        "player CHAR(1) NOT NULL," +
        "row INTEGER NOT NULL," +
        "col INTEGER NOT NULL," +
        "choice CHAR(1) NOT NULL," +
        "move_number INTEGER NOT NULL," +
        "sos_formed BOOLEAN NOT NULL," +
        "FOREIGN KEY (game_id) REFERENCES games(game_id))";
    
    // initializes the database connection
    public SOSRecorder() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            
            // Create tables if they don't exist
            Statement stmt = connection.createStatement();
            stmt.execute(CREATE_GAMES_TABLE);
            stmt.execute(CREATE_MOVES_TABLE);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Start recording a new game
    public int startNewGame(int boardSize, SOSGame.GameMode gameMode, String bluePlayerType, String redPlayerType) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO games (board_size, game_mode, blue_player_type, red_player_type) VALUES (?, ?,?,?)",
                Statement.RETURN_GENERATED_KEYS
            );
            pstmt.setInt(1, boardSize);
            pstmt.setString(2, gameMode.toString());
            pstmt.setString(3,bluePlayerType);
            pstmt.setString(4,redPlayerType);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                currentGameId = rs.getInt(1);
            }
            rs.close();
            pstmt.close();
            
            return currentGameId;
        } catch (SQLException e) {
            System.err.println("Error starting new game recording: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    // Record a move in the current game
    public void recordMove(char player, int row, int col, char choice, int moveNumber, boolean sosFormed) {
        if (currentGameId == -1) {
            System.err.println("Cannot record move: No active game");
            return;
        }
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO moves (game_id, player, row, col, choice, move_number, sos_formed) VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            pstmt.setInt(1, currentGameId);
            pstmt.setString(2, String.valueOf(player));
            pstmt.setInt(3, row);
            pstmt.setInt(4, col);
            pstmt.setString(5, String.valueOf(choice));
            pstmt.setInt(6, moveNumber);
            pstmt.setBoolean(7, sosFormed);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error recording move: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // End the current game recording
    public void endGameRecording() {
        currentGameId = -1;
    }
    
    // Get a list of all recorded games
    public List<GameRecord> getRecordedGames() {
        List<GameRecord> games = new ArrayList<>();
        
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT game_id, board_size, game_mode, blue_player_type, red_player_type, timestamp FROM games ORDER BY timestamp DESC");
            
            while (rs.next()) {
                int gameId = rs.getInt("game_id");
                int boardSize = rs.getInt("board_size");
                SOSGame.GameMode gameMode = SOSGame.GameMode.valueOf(rs.getString("game_mode"));
                String bluePlayerType = rs.getString("blue_player_type");
                String redPlayerType = rs.getString("red_player_type");
                String timestamp = rs.getString("timestamp");
                
                games.add(new GameRecord(gameId, boardSize, gameMode, bluePlayerType, redPlayerType, timestamp));
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting recorded games: " + e.getMessage());
            e.printStackTrace();
        }
        
        return games;
    }
    
    // Get all moves for a specific game
    public List<MoveRecord> getGameMoves(int gameId) {
        List<MoveRecord> moves = new ArrayList<>();
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                "SELECT player, row, col, choice, move_number, sos_formed FROM moves " +
                "WHERE game_id = ? ORDER BY move_number"
            );
            pstmt.setInt(1, gameId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                char player = rs.getString("player").charAt(0);
                int row = rs.getInt("row");
                int col = rs.getInt("col");
                char choice = rs.getString("choice").charAt(0);
                int moveNumber = rs.getInt("move_number");
                boolean sosFormed = rs.getBoolean("sos_formed");
                
                moves.add(new MoveRecord(player, row, col, choice, moveNumber, sosFormed));
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting game moves: " + e.getMessage());
            e.printStackTrace();
        }
        
        return moves;
    }
    
    // Get game info for a specific game ID
    public GameRecord getGameInfo(int gameId) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                "SELECT board_size, game_mode, blue_player_type, red_player_type, timestamp FROM games WHERE game_id = ?"
            );
            pstmt.setInt(1, gameId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int boardSize = rs.getInt("board_size");
                SOSGame.GameMode gameMode = SOSGame.GameMode.valueOf(rs.getString("game_mode"));
                String bluePlayerType = rs.getString("blue_player_type");
                String redPlayerType = rs.getString("red_player_type");
                String timestamp = rs.getString("timestamp");
                
                return new GameRecord(gameId, boardSize, gameMode, bluePlayerType, redPlayerType, timestamp);
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting game info: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Close the database connection when done
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static class GameRecord {
        private int gameId;
        private int boardSize;
        private SOSGame.GameMode gameMode;
        private String bluePlayerType;
        private String redPlayerType;
        private String timestamp;
        
        public GameRecord(int gameId, int boardSize, SOSGame.GameMode gameMode, String bluePlayerType, String redPlayerType, String timestamp) {
            this.gameId = gameId;
            this.boardSize = boardSize;
            this.gameMode = gameMode;
            this.bluePlayerType = bluePlayerType;
            this.redPlayerType = redPlayerType;
            this.timestamp = timestamp;
        }
        
        
        public int getGameId() { return gameId; }
        public int getBoardSize() { return boardSize; }
        public SOSGame.GameMode getGameMode() { return gameMode; }
        public String getBluePlayerType() { return bluePlayerType; }
        public String getRedPlayerType() { return redPlayerType; }
        public String getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return "Game #" + gameId + " - " + boardSize + "x" + boardSize + " " + gameMode + 
                   " - Blue: " + bluePlayerType + ", Red: " + redPlayerType + " - " + timestamp;
        }
    }
    
    // Record class for move info
    public static class MoveRecord {
        private char player;
        private int row;
        private int col;
        private char choice;
        private int moveNumber;
        private boolean sosFormed;
        
        public MoveRecord(char player, int row, int col, char choice, int moveNumber, boolean sosFormed) {
            this.player = player;
            this.row = row;
            this.col = col;
            this.choice = choice;
            this.moveNumber = moveNumber;
            this.sosFormed = sosFormed;
        }
        
        public char getPlayer() { return player; }
        public int getRow() { return row; }
        public int getCol() { return col; }
        public char getChoice() { return choice; }
        public int getMoveNumber() { return moveNumber; }
        public boolean isSosFormed() { return sosFormed; }
    }
}