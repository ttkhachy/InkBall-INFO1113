package inkball;

import processing.core.PApplet;
import processing.core.PConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import processing.event.MouseEvent;
import processing.event.KeyEvent;

public class AppTests {

    private App app;
    private KeyEvent mockEvent;

    @BeforeEach
    public void setupTests() {
        app = new App();
        mockEvent = mock(KeyEvent.class);
        PApplet.runSketch(new String[] { "App" }, app);
        app.settings();
        app.setup(); // Initialize resources
        app.board = new Tile[18][18]; // Initialize a simple 18x18 board for testing
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 18; j++) {
                app.board[i][j] = new Tile(i, j); // Fill the board with default tiles
            }
        }
        app.drawnLines = new ArrayList<>();
        app.mouseButton = PConstants.LEFT;
        app.delay(1000);

    }

    @Test
    public void testSetBallColoursMap() {
        app.setBallColoursMap();

        assertEquals("grey", App.ballColours.get("B0"));
        assertEquals("orange", App.ballColours.get("B1"));
        assertEquals("blue", App.ballColours.get("B2"));
        assertEquals("green", App.ballColours.get("B3"));
        assertEquals("yellow", App.ballColours.get("B4"));
    }

    @Test
    public void testMouseReleased_LineAddedToDrawnLines() {
        // Mock MouseEvent
        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getButton()).thenReturn(PConstants.LEFT);
        when(mockEvent.getX()).thenReturn(150);
        when(mockEvent.getY()).thenReturn(250);

        app.gameOver = false;
        app.currentLine = new Line(100, 200); // Initialize currentLine

        // Call the mouseReleased method with the mocked event
        app.mouseReleased(mockEvent);

        // Veri fy that currentLine is added to drawnLines
        assertEquals(1, app.drawnLines.size(), "currentLine should be added to drawnLines");
        assertNull(app.currentLine, "currentLine should be reset to null after mouse release");
    }

    @Test
    public void testMouseDragged_RightButton_RemovesLine() {
        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getButton()).thenReturn(PConstants.RIGHT);
        when(mockEvent.getX()).thenReturn(51);
        when(mockEvent.getY()).thenReturn(51);

        app.gameOver = false;
        Line existingLine = new Line(50, 50);
        existingLine.addPoints(60, 60);
        app.drawnLines.add(existingLine);

        app.mouseDragged(mockEvent);
        app.draw();

        assertTrue(app.drawnLines.isEmpty(), "The line should be removed when right-click dragged near it"); // FAILED
    }

    @Test
    public void restartLevelScoreTest() {
        app.scoreAtStartOfLevel = 100;
        app.score = 300;
        app.CURRENT_LEVEL = 1;

        app.restartLevel();
        assertEquals(app.score, app.scoreAtStartOfLevel);

    }

    @Test
    public void testKeyPressed_RKey_GameNotOver() {
        app.gameOver = false;
        app.CURRENT_LEVEL = 2;

        when(mockEvent.getKey()).thenReturn('r');
        app.keyPressed(mockEvent);

    }

    @Test
    public void testKeyPressed_SpaceKey_TogglePauseOn() {
        app.gamePaused = false;
        // simulate pressing space to pause the game
        when(mockEvent.getKey()).thenReturn(' ');

        app.keyPressed(mockEvent);

        // the game should now be paused,
        assertTrue(app.gamePaused, "The game should be paused.");
    }

    @Test
    public void testStartYellowTileAnimation_GamePaused() {
        // Arrange: Set the game to a paused state
        app.gamePaused = true;

        // Act: Attempt to start the yellow tile animation
        app.startYellowTileAnimation();

        // Assert: No tiles should be animated, and the yellow tile count should remain
        // 0
        assertEquals(0, app.yellowTileCount, "Yellow tile count should not change when the game is paused.");
        assertFalse(app.board[0][0].showYellowTile, "Top row tile should not be animated.");
        assertFalse(app.board[17][17].showYellowTile, "Bottom row tile should not be animated.");
        assertFalse(app.yellowTileAnimationComplete, "Animation should not be complete when game is paused.");
    }

    @Test
    public void testStartYellowTileAnimation_AnimationComplete() {
        // set yellowTileCount to 16 to simulate nearing the end of the animation
        app.yellowTileCount = 16;
        app.gamePaused = false;

        // start the yellow tile animation
        app.startYellowTileAnimation();

        // the yellow tile count should reach 17, and the animation should be marked as
        // complete
        assertEquals(17, app.yellowTileCount, "Yellow tile count should reach 17 at the end of the animation.");
        assertTrue(app.board[0][16].showYellowTile, "Top row tile should be animated.");
        assertTrue(app.board[16][0].showYellowTile, "Bottom row tile should be animated.");
        assertTrue(app.yellowTileAnimationComplete, "Animation should be complete when yellowTileCount reaches 17.");
    }

    @Test
    public void testDraw_NonNullBoardTile() {
        Tile tileMock = new Tile(0, 0); // Mocking not required, directly use an instance
        app.board[0][0] = tileMock;
        assertNotNull(app.board[0][0], "Board tile should not be null");
    }

    @Test
    public void testDraw_GameObjects() {
        GameObject gameObject = new GameObject("testObject", 1, 1, 1, 1);
        app.gameObjects.add(gameObject);

        app.draw();

        assertFalse(app.gameObjects.isEmpty(), "Game objects should not be empty after drawing");
    }

    @Test
    public void testDraw_BallMovementsAndInteractions() {
        Ball ball = new Ball("B1", 0, 0, "orange", 24, 24);
        Wall wall = new Wall(0, 0, "X");
        Line line = new Line(10, 10);
        Hole hole = new Hole("Hole1", 1, 1, 2, 2);

        app.activeBalls.add(ball);
        app.allWalls.add(wall);
        app.drawnLines.add(line);
        app.allHoles.add(hole);

        app.draw();

        assertFalse(app.activeBalls.isEmpty(), "Active balls should not be empty");
        assertFalse(app.allWalls.isEmpty(), "Walls should not be empty");
        assertFalse(app.allHoles.isEmpty(), "Holes should not be empty");
    }

    @Test
    public void testDraw_GameOver() {
        app.gameOver = true;
        Ball ball = new Ball("B1", 0, 0, "orange", 24, 24);
        app.activeBalls.add(ball);

        assertFalse(ball.getSpawned(), "Ball should be unspawned when game is over");
    }

    @Test
    public void testDraw_YellowTileAnimationComplete() {
        app.yellowTileAnimationComplete = true;
        app.scoreAdditionFinished = true;

        app.draw();

        assertFalse(app.yellowTileAnimationComplete, "Yellow tile animation should be complete");
    }

    @Test
    public void testDraw_HandleBallSpawning() {
        // Arrange
        Ball ball = new Ball("B1", 0, 0, "orange", 24, 24);
        app.ballSpawnQueue.add(ball);

        // Act
        app.draw();

        // Assert
        assertFalse(app.ballSpawnQueue.isEmpty(), "Ball spawn queue should be handled during draw");
    }

    @Test
    public void testMouseDragged_LeftButton_NewLineCreated() {
        // Arrange
        MouseEvent mockEvent = mock(MouseEvent.class);
        app.currentLine = null; // currentLine is initially null
        app.mouseX = 100;
        app.mouseY = 150;

        // Act
        app.mouseDragged(mockEvent);

        // Assert
        assertNotNull(app.currentLine, "A new line should be created when currentLine is null.");
        assertEquals(100, app.currentLine.getPoints().get(0)[0], "X coordinate of the new line should match mouseX.");
        assertEquals(150, app.currentLine.getPoints().get(0)[1], "Y coordinate of the new line should match mouseY.");
    }

    @Test
    public void levelCompleteTest() {
        app.CURRENT_LEVEL = 1;
        app.scoreAtStartOfLevel = 20;
        app.score = 60;

        assertEquals(app.CURRENT_LEVEL, 2);
        assertEquals(app.scoreAtStartOfLevel, 60);
    }

}
