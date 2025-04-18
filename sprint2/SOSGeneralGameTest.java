import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SOSGeneralGameTest {

    private SOSGeneralGame game;

    @BeforeEach
    public void setUp() {
        game = new SOSGeneralGame(3); // Test with a 3x3 board
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
        assertFalse(game.makeMove(3, 3, 'O'), "Invalid position should return false");
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
    public void testIsGameEndingWhenBoardFull() {
        // Test if the game ends when the board is full in General mode
        game.makeMove(0, 0, 'S');
        game.makeMove(0, 1, 'O');
        game.makeMove(0, 2, 'S');
        game.makeMove(1, 0, 'O');
        game.makeMove(1, 1, 'S');
        game.makeMove(1, 2, 'O');
        game.makeMove(2, 0, 'S');
        game.makeMove(2, 1, 'O');
        game.makeMove(2, 2, 'S');
        
        assertTrue(game.isGameEnding(), "Game should end when the board is full");
    }

    @Test
    public void testGameDoesNotEndWithIncompleteBoard() {
        // Test if the game does not end if the board is not full in General mode
        game.makeMove(0, 0, 'S');
        game.makeMove(0, 1, 'O');
        assertFalse(game.isGameEnding(), "Game should not end when the board is not full");
    }
}

