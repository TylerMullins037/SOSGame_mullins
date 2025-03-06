import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import javax.swing.*;
import java.awt.*;

public class SOSGUITest {

    private SOSGUI gui;

    @BeforeEach
    public void setUp() {
        // Initialize the GUI before each test
        gui = new SOSGUI();
    }

    @AfterEach
    public void tearDown() {
        // Dispose of the GUI after each test to prevent memory leaks
        if (gui != null) {
            gui.dispose();
        }
    }

    // Test AC 1.1 Valid Board Size (n > 2)
    @Test
    public void testValidBoardSize() {
        // Simulate user input: setting the board size greater than 2 (e.g., 5)
        gui.boardSizeField.setText("5");
        gui.rebuildBoardUI();

        // Check if the board size is updated correctly
        assertEquals(5, gui.boardSize, "The board size should be updated to the selected value.");
    }

    // Test AC 1.2 Default Board Size
    @Test
    public void testDefaultBoardSize() {
        // Simulate launching the program with no board size set
        gui.boardSizeField.setText("");  // Empty field
        gui.rebuildBoardUI();

        // Check if the default size (8) is used when no size is selected
        assertEquals(8, gui.boardSize, "The board size should default to 8.");
    }

    // Test AC 1.3 Invalid Board Size (n <= 2)
    @Test
    public void testInvalidBoardSize() {
        // Simulate user input for an invalid size (e.g., 2)
        gui.boardSizeField.setText("2");
        gui.rebuildBoardUI();

        // Check if an error message appears when selecting an invalid size
        assertEquals(8, gui.boardSize, "The board size should remain the default size (8) when an invalid size is entered.");
    }

    // Test AC 2.1 Simple Game Mode
    @Test
    public void testSimpleGameMode() {
        // Select "Simple Game" mode from the combo box
        gui.gameModeComboBox.setSelectedItem("Simple Game");
        gui.updateGame();

        // Check if the game mode is set to Simple
        assertTrue(gui.game instanceof SOSSimpleGame, "The game mode should be set to Simple.");
    }

    // Test AC 2.2 Default Game Mode
    @Test
    public void testDefaultGameMode() {
        // Simulate launching the program without selecting a game mode
        gui.gameModeComboBox.getSelectedItem();
        gui.updateGame();

        // Check if the default game mode is Simple
        assertTrue(gui.game instanceof SOSSimpleGame, "The default game mode should be Simple.");
    }

    // Test AC 2.3 General Game Mode
    @Test
    public void testGeneralGameMode() {
        // Select "General Game" mode from the combo box
        gui.gameModeComboBox.setSelectedItem("General Game");
        gui.updateGame();

        // Check if the game mode is set to General
        assertTrue(gui.game instanceof SOSGeneralGame, "The game mode should be set to General.");
    }

    // Test AC 3.1 Starting a New Game
    @Test
    public void testStartNewGame() {
        // Set up a new game with a board size and mode
        gui.boardSizeField.setText("5");
        gui.rebuildBoardUI();
        gui.gameModeComboBox.setSelectedItem("Simple Game");
        gui.updateGame();

        // Click "New Game" and check if the board is reset
        gui.newGameButton.doClick();

        // Check if the board is reset (no moves should be made yet)
        boolean boardIsEmpty = true;
        for (JButton[] row : gui.boardButtons) {
            for (JButton button : row) {
                if (!button.getText().equals("")) {
                    boardIsEmpty = false;
                    break;
                }
            }
        }
        assertTrue(boardIsEmpty, "The board should be empty when a new game is started.");
    }
}