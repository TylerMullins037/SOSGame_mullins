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
        // Wait for initialization to complete
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        assertEquals(5, gui.boardButtons.length, "The board should have the correct number of rows.");
        assertEquals(5, gui.boardButtons[0].length, "The board should have the correct number of columns.");
    }
    
    // Test AC 1.2 Default Board Size
    @Test
    public void testDefaultBoardSize() {
        // Check initial board size is the default (8)
        assertEquals(8, gui.boardSize, "The initial board size should be 8.");
        
        // Simulate launching the program with no board size set
        gui.boardSizeField.setText("");  // Empty field
        // This would normally show an error dialog, but in testing we just check if it keeps default value
        gui.rebuildBoardUI();
        
        // The board size should remain at the default (8) when an invalid input is provided
        assertEquals(8, gui.boardSize, "The board size should default to 8.");
    }
    
    // Test AC 1.3 Invalid Board Size (n <= 2)
    @Test
    public void testInvalidBoardSize() {
        // Simulate user input for an invalid size (e.g., 2)
        gui.boardSizeField.setText("2");
        // Store the current board size before attempting to rebuild
        int initialSize = gui.boardSize;
        gui.rebuildBoardUI();
        
        // Check if the board size remains unchanged when an invalid size is entered
        assertEquals(initialSize, gui.boardSize, "The board size should remain unchanged when an invalid size is entered.");
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
        // When GUI is initialized, it should have a default game mode
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
        
        // Click "New Game" and check if the game is initialized
        gui.newGameButton.doClick();
        
        // Check if the game is in progress
        assertTrue(gui.gameInProgress, "The game should be in progress after clicking New Game button.");
        
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
        
        // Check if players are properly initialized
        assertNotNull(gui.getBluePlayer(), "Blue player should be initialized");
        assertTrue(gui.getBluePlayer() instanceof SOSHumanPlayer, "Blue player should be human");
        assertNotNull(gui.getRedPlayer(), "Red player should be initialized");
        assertTrue(gui.getRedPlayer() instanceof SOSHumanPlayer, "Blue player should be human");
    }
    
    // Test player type selection
    @Test
    public void testPlayerTypeSelection() {
        // Set Human for Blue and Computer for Red
        gui.bluePlayerComboBox.setSelectedItem("Human");
        gui.redPlayerComboBox.setSelectedItem("Computer");
        
        // Start a new game
        gui.newGameButton.doClick();
        
        // Check player types
        assertTrue(gui.getBluePlayer() instanceof SOSHumanPlayer, "Blue player should be human");
        assertTrue(gui.getRedPlayer() instanceof SOSComputerPlayer, "Red player should be computer");
        
        // Now try the opposite
        gui.bluePlayerComboBox.setSelectedItem("Computer");
        gui.redPlayerComboBox.setSelectedItem("Human");
        
        // Start a new game
        gui.newGameButton.doClick();
        
        // Check player types again
        assertTrue(gui.getBluePlayer() instanceof SOSComputerPlayer, "Blue player should be computer");
        assertTrue(gui.getRedPlayer() instanceof SOSHumanPlayer, "Red player should be human");
    }
    
    // Test initial player letter selection
    @Test
    public void testInitialPlayerLetterSelection() {
        // Start a new game
        gui.newGameButton.doClick();
        
        // Check initial letter selections
        assertEquals('S', gui.game.getBlueChoice(), "Blue player should initially select 'S'");
        assertEquals('S', gui.game.getRedChoice(), "Red player should initially select 'S'");
        
        // Check that the S buttons are highlighted
        Color blueColor = gui.blueS.getBackground();
        Color redColor = gui.redS.getBackground();
        
        assertEquals(Color.BLUE, blueColor, "Blue S button should be highlighted");
        assertEquals(Color.RED, redColor, "Red S button should be highlighted");
    }
}