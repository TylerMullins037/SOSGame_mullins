import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SOSGeneralGameTest {

    private SOSGeneralGame game;

    @BeforeEach
    public void setUp() {
        game = new SOSGeneralGame(5); // Test with a 3x3 board
    }

    @Test
    public void testMakeNormalMoveS() {
        // Test making a valid 'S' move in General mode
        assertTrue(game.makeMove(0, 0, 'S'), "Valid normal 'S' move should return true");
    }

    @Test
    public void testMakeNormalMoveO() {
        // Test making a valid 'O' move in General mode
        assertTrue(game.makeMove(1, 1, 'O'), "Valid normal 'O' move should return true");
    }

    @Test
    public void testMakeMoveInvalidPosition() {
        // Test making a move outside the board in General mode
        assertFalse(game.makeMove(-1, -1, 'S'), "Invalid position should return false");
        assertFalse(game.makeMove(8, 8, 'O'), "Invalid position should return false");
    }

    @Test
    public void testMakeMoveOnOccupiedSquare() {
        // Test making a move on an already occupied square in General mode
        game.makeMove(0, 0, 'S');
        assertFalse(game.makeMove(0, 0, 'S'), "Move on an occupied square should return false");
    }

    @Test
    public void testTurnSwitchAfterInvalidMove() {
        // Test turn switch after an invalid move in General mode
        game.makeMove(0, 0, 'S');
        assertFalse(game.makeMove(0, 0, 'O'), "Move on an occupied square should return false");
        assertFalse(game.getTurn() == 'B', "Move on an occupied square should return false");
    }

    @Test
    public void testNoSOSFormedTurnChange() {
        // Test turn switch after a move resulting in no SOS formation
        game.makeMove(0, 0, 'S');
        assertEquals('R',game.getTurn());
        game.makeMove(1, 0, 'S');
        assertEquals('B',game.getTurn());

    }

    @Test
    public void testSOSFormedTurnContinues() {
        // Test turn switch after a move resulting in no SOS formation
        game.makeMove(0, 0, 'S');
        game.makeMove(2, 0, 'S');
        assertEquals('B',game.getTurn());
        game.makeMove(1, 0, 'O');
        assertEquals('B',game.getTurn());

    }

    @Test
    public void testIsGameEndingWhenBoardFull() {
        // Test if the game ends when the board is full in General mode
        game.makeMove(0, 0, 'S');
        game.makeMove(0, 1, 'S');
        game.makeMove(0, 2, 'S');
        game.makeMove(0, 3, 'S');
        game.makeMove(0, 4, 'S');
        game.makeMove(1, 0, 'S');
        game.makeMove(1, 1, 'S');
        game.makeMove(1, 2, 'S');
        game.makeMove(1, 3, 'S');
        game.makeMove(1, 4, 'S');
        game.makeMove(2, 0, 'S');
        game.makeMove(2, 1, 'S');
        game.makeMove(2, 2, 'S');
        game.makeMove(2, 3, 'S');
        game.makeMove(2, 4, 'S');
        game.makeMove(3, 0, 'S');
        game.makeMove(3, 1, 'S');
        game.makeMove(3, 2, 'S');
        game.makeMove(3, 3, 'S');
        game.makeMove(3, 4, 'S');
        game.makeMove(4, 0, 'S');
        game.makeMove(4, 1, 'S');
        game.makeMove(4, 2, 'S');
        game.makeMove(4, 3, 'S');
        game.makeMove(4, 4, 'S');
        assertTrue(game.isGameEnding(), "Game should end when the board is full");
    }

    @Test
    public void testGameDoesNotEndWithIncompleteBoard() {
        // Test if the game does not end if the board is not full in General mode
        game.makeMove(0, 0, 'S');
        game.makeMove(0, 1, 'O');
        assertFalse(game.isGameEnding(), "Game should not end when the board is not full");
    }

    @Test
    public void testValidMoveWithSOSFormation() {
        // Player 'S' places on (0, 0)
        assertTrue(game.makeMove(0, 0, 'S'));

        // Player 'O' places on (0, 1) forming part of an SOS sequence
        assertTrue(game.makeMove(0, 1, 'O'));

        // Player 'S' places on (0, 2) completing an SOS sequence
        assertTrue(game.makeMove(0, 2, 'S'));

        // Check if SOS sequence is formed
        assertTrue(game.getSOSFormed()); // Should be true after the sequence
    }

    @Test
    public void testSOSFormedMultipleTimes() {
        assertTrue(game.makeMove(0, 0, 'S')); // S at (0,0)
        assertTrue(game.makeMove(0, 2, 'S')); // S at (0,2)
        assertTrue(game.makeMove(0, 4, 'S')); // S at (0,4)
        assertTrue(game.makeMove(2, 0, 'S')); // S at (2,0)
        assertTrue(game.makeMove(2, 4, 'S')); // S at (2,4)
        assertTrue(game.makeMove(4, 0, 'S')); // S at (4,0)
        assertTrue(game.makeMove(4, 2, 'S')); // S at (4,2)
        assertTrue(game.makeMove(4, 4, 'S')); // S at (4,4)
    
        assertTrue(game.makeMove(1, 1, 'O')); // O at (1,1)
        assertTrue(game.makeMove(1, 3, 'O')); // O at (3,3)
        assertTrue(game.makeMove(3, 1, 'O')); // O at (1,3)
        assertTrue(game.makeMove(3, 3, 'O')); // O at (3,1)
        assertTrue(game.makeMove(3, 2, 'O')); // O at (3,2)
        assertTrue(game.makeMove(2, 3, 'O')); // O at (2,3)
        assertTrue(game.makeMove(2, 1, 'O')); // O at (2,1)
        assertTrue(game.makeMove(1, 2, 'O')); // O at (1,2)
    
        // Make the final 'S' move that completes multiple SOS formations
        assertTrue(game.makeMove(2, 2, 'S')); // S at (2,2) completing SOS sequences
        // Check if the blue player's score is 12 (since multiple SOS sequences are formed)
        assertEquals(12, game.getScore('B'));
    }

    @Test
    public void testScore() {
        game.makeMove(0, 0, 'S');
        game.makeMove(1, 1, 'O');
        game.makeMove(2, 2, 'S'); // Forms diagonal SOS

        assertEquals(1, game.getScore('B')); // Score should increment
    }

    @Test
    public void testDrawGame() {
        // Fill the board with valid moves (mix of 'S' and 'O')
        game.makeMove(0, 0, 'S');
        game.makeMove(0, 1, 'O');
        game.makeMove(0, 2, 'S');
        game.makeMove(0, 3, 'O');
        game.makeMove(0, 4, 'S');

        game.makeMove(1, 0, 'S');
        game.makeMove(1, 1, 'O');
        game.makeMove(1, 2, 'S'); 
        game.makeMove(1, 3, 'O');
        game.makeMove(1, 4, 'S');

        game.makeMove(2, 0, 'S');
        game.makeMove(2, 1, 'O');
        game.makeMove(2, 2, 'S'); 
        game.makeMove(2, 3, 'O');
        game.makeMove(2, 4, 'S');

        game.makeMove(3, 0, 'S');
        game.makeMove(3, 1, 'O');
        game.makeMove(3, 2, 'S'); 
        game.makeMove(3, 3, 'O');
        game.makeMove(3, 4, 'S');

        game.makeMove(4, 0, 'S');
        game.makeMove(4, 1, 'O');
        game.makeMove(4, 2, 'S'); 
        game.makeMove(4, 3, 'O');
        game.makeMove(4, 4, 'S');
        // Red should have more SOS formations than Blue
        assertTrue(game.getScore('B') == game.getScore('R'));
    }

    @Test
    public void testBluePlayerWins() {
        // Fill the board with valid moves (mix of 'S' and 'O')
        game.makeMove(0, 0, 'S');
        game.makeMove(0, 1, 'O');
        game.makeMove(0, 2, 'S');
        game.makeMove(0, 3, 'O');
        game.makeMove(0, 4, 'S');

        game.makeMove(1, 0, 'S');
        game.makeMove(1, 1, 'O');
        game.makeMove(1, 3, 'S'); 
        game.makeMove(1, 2, 'S');
        game.makeMove(1, 4, 'S');

        game.makeMove(2, 0, 'S');
        game.makeMove(2, 1, 'S');
        game.makeMove(2, 2, 'S'); 
        game.makeMove(2, 3, 'S');
        game.makeMove(2, 4, 'S');

        game.makeMove(3, 0, 'S');
        game.makeMove(3, 1, 'S');
        game.makeMove(3, 2, 'S'); 
        game.makeMove(3, 3, 'S');
        game.makeMove(3, 4, 'S');

        game.makeMove(4, 0, 'S');
        game.makeMove(4, 1, 'S');
        game.makeMove(4, 2, 'S'); 
        game.makeMove(4, 3, 'S');
        game.makeMove(4, 4, 'S');
        // Blue should have more SOS formations than Blue
        assertTrue(game.getScore('B') > game.getScore('R'));
    }

    @Test
    public void testRedPlayerWins() {
        // Fill the board with valid moves (mix of 'S' and 'O')
        game.makeMove(0, 0, 'S');
        game.makeMove(0, 1, 'S');
        game.makeMove(0, 2, 'S');
        game.makeMove(0, 3, 'S');
        game.makeMove(0, 4, 'S');

        game.makeMove(1, 0, 'S');
        game.makeMove(1, 1, 'S');
        game.makeMove(1, 2, 'S'); 
        game.makeMove(1, 3, 'O');
        game.makeMove(1, 4, 'S');

        game.makeMove(2, 0, 'S');
        game.makeMove(2, 1, 'O');
        game.makeMove(2, 2, 'S'); 
        game.makeMove(2, 3, 'S');
        game.makeMove(2, 4, 'S');

        game.makeMove(3, 0, 'S');
        game.makeMove(3, 1, 'S');
        game.makeMove(3, 2, 'S'); 
        game.makeMove(3, 3, 'S');
        game.makeMove(3, 4, 'S');

        game.makeMove(4, 0, 'S');
        game.makeMove(4, 1, 'S');
        game.makeMove(4, 2, 'S'); 
        game.makeMove(4, 3, 'S');
        game.makeMove(4, 4, 'S');
        // Red should have more SOS formations than Blue
        assertTrue(game.getScore('R') > game.getScore('B'));
    }
}