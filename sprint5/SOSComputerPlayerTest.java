import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

public class SOSComputerPlayerTest {

    private SOSComputerPlayer computerPlayer;
    private SOSGame game;

    @BeforeEach
    public void setUp() {
        computerPlayer = new SOSComputerPlayer('R'); // Red player
    }

    @Test
    public void testMakeRandomMove() {
        game = new SOSSimpleGame(3);
        try {
            Method makeRandomMoveMethod = SOSComputerPlayer.class.getDeclaredMethod("makeRandomMove", SOSGame.class);
            makeRandomMoveMethod.setAccessible(true);

            int[] move = (int[]) makeRandomMoveMethod.invoke(computerPlayer, game);
            assertNotNull(move);
            assertEquals(3, move.length);
            assertTrue(move[0] >= 0 && move[0] < 3);
            assertTrue(move[1] >= 0 && move[1] < 3);
            assertTrue(move[2] == 'S' || move[2] == 'O');

            // Fill the board except for one cell
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (!(i == 2 && j == 2)) {
                        game.setSquare(i, j, SOSGame.Square.S);
                    }
                }
            }

            move = (int[]) makeRandomMoveMethod.invoke(computerPlayer, game);
            assertNotNull(move);
            assertEquals(2, move[0]);
            assertEquals(2, move[1]);

            game.setSquare(2, 2, SOSGame.Square.S);
            move = (int[]) makeRandomMoveMethod.invoke(computerPlayer, game);
            assertNull(move);

        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }
    }

    @Test
    public void testFindTacticalMove() {
        game = new SOSSimpleGame(3);
        game.setSquare(0, 0, SOSGame.Square.S);
        game.setSquare(0, 2, SOSGame.Square.S);

        try {
            Method findTacticalMoveMethod = SOSComputerPlayer.class.getDeclaredMethod("findTacticalMove", SOSGame.class);
            findTacticalMoveMethod.setAccessible(true);

            int[] move = (int[]) findTacticalMoveMethod.invoke(computerPlayer, game);
            assertNotNull(move);
            assertEquals(0, move[0]);
            assertEquals(1, move[1]);
            assertEquals('O', move[2]);

        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }
    }

    @Test
    public void testFindSmartMove() {
        game = new SOSSimpleGame(3);
        game.setSquare(0, 0, SOSGame.Square.S);
        game.setSquare(1, 1, SOSGame.Square.O);
        game.setSquare(2, 2, SOSGame.Square.S);

        try {
            Method findSmartMoveMethod = SOSComputerPlayer.class.getDeclaredMethod("findSmartMove", SOSGame.class);
            findSmartMoveMethod.setAccessible(true);

            int[] move = (int[]) findSmartMoveMethod.invoke(computerPlayer, game);
            assertNotNull(move);
            assertEquals(3, move.length);
            assertTrue(move[0] >= 0 && move[0] < 3);
            assertTrue(move[1] >= 0 && move[1] < 3);
            assertTrue(move[2] == 'S' || move[2] == 'O');

        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }
    }

    @Test
    public void testMakeMove() {
        game = new SOSSimpleGame(3);
        int[] move = computerPlayer.makeMove(game);

        assertNotNull(move);
        assertEquals(3, move.length);
        assertTrue(move[0] >= 0 && move[0] < 3);
        assertTrue(move[1] >= 0 && move[1] < 3);
        assertTrue(move[2] == 'S' || move[2] == 'O');

        game = new SOSSimpleGame(3);
        game.setSquare(0, 0, SOSGame.Square.S);
        game.setSquare(0, 2, SOSGame.Square.S);

        move = computerPlayer.makeMove(game);
        System.out.println("Move with tactical option: " + Arrays.toString(move));
    }

    @Test
    public void testEvaluationFunctions() {
        game = new SOSSimpleGame(5);

        try {
            Method evalMoveMethod = SOSComputerPlayer.class.getDeclaredMethod(
                "evalMove", SOSGame.class, int.class, int.class, char.class);
            evalMoveMethod.setAccessible(true);

            double scoreCenter = (double) evalMoveMethod.invoke(computerPlayer, game, 2, 2, 'S');
            double scoreCorner = (double) evalMoveMethod.invoke(computerPlayer, game, 0, 0, 'S');
            assertTrue(scoreCenter > scoreCorner);

            game.setSquare(0, 2, SOSGame.Square.S);
            double scoreNextToS = (double) evalMoveMethod.invoke(computerPlayer, game, 0, 1, 'O');
            double scoreRandom = (double) evalMoveMethod.invoke(computerPlayer, game, 3, 3, 'O');
            System.out.println("Score next to S: " + scoreNextToS);
            System.out.println("Score random: " + scoreRandom);

            game = new SOSSimpleGame(3);
            game.setSquare(0, 0, SOSGame.Square.S);
            game.setSquare(0, 1, SOSGame.Square.O);
            double scoreCompletesSOS = (double) evalMoveMethod.invoke(computerPlayer, game, 0, 2, 'S');
            double scoreNeutral = (double) evalMoveMethod.invoke(computerPlayer, game, 1, 1, 'S');
            assertTrue(scoreCompletesSOS > scoreNeutral);

        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }
    }

    @Test
    public void testPlayFullGame() {
        game = new SOSSimpleGame(4);
        int moveCount = 0;
        while (!game.isGameEnding() && moveCount < 16) {
            int[] move = computerPlayer.makeMove(game);
            assertNotNull(move);
            game.setSquare(move[0], move[1], move[2] == 'S' ? SOSGame.Square.S : SOSGame.Square.O);
            moveCount++;
        }
        assertTrue(game.isGameEnding() || moveCount >= 16);
    }
}