package inkball;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import processing.core.PImage;

public class BallTests {

    private Ball ball;
    private Ball ball2;
    private App mockApp;
    private PImage mockPImage;
    private Line mockLine;
    private Hole mockHole;

    @BeforeEach
    public void setUp() {
        // mock dependencies
        mockApp = mock(App.class);
        mockPImage = mock(PImage.class);
        mockHole = mock(Hole.class);

        // Set up the ball
        ball = new Ball("B1", 2, 3, "orange", 24, 24);
        ball2 = new Ball("B2", 5, 5, "blue", 24, 24);

        mockLine = mock(Line.class);
        mockLine.addPoints(50, 50);
        mockLine.addPoints(75, 75);
        mockLine.addPoints(100, 100);

    }

    @Test
    public void testBallInitialization() {
        assertEquals(2 * App.CELLSIZE, ball.xPosition);
        assertEquals(3 * App.CELLSIZE + App.TOPBAR, ball.yPosition);
        assertEquals("orange", ball.getBallColour());
        assertFalse(ball.getSpawned());
    }

    @Test
    public void testSetSpawned() {
        ball.setSpawned(true);
        assertTrue(ball.getSpawned());

        ball.setSpawned(false);
        assertFalse(ball.getSpawned());
    }

    @Test
    public void testShouldBeRemoved() {
        assertFalse(ball.shouldBeRemoved());
        ball.shouldBeRemoved = true;
        assertTrue(ball.shouldBeRemoved());
    }

    @Test
    public void testMoveWhenSpawned() {
        ball.setSpawned(true);
        ball.move(mockApp);
        assertNotEquals(2 * App.CELLSIZE, ball.xPosition);
        assertNotEquals(3 * App.CELLSIZE + App.TOPBAR, ball.yPosition);
    }

    @Test
    public void testMoveWhenNotSpawned() {
        ball.setSpawned(false);
        int originalX = ball.xPosition;
        int originalY = ball.yPosition;

        ball.move(mockApp);
        assertEquals(originalX, ball.xPosition);
        assertEquals(originalY, ball.yPosition);
    }

    @Test
    public void testApplyNewVelocities() {
        float[] normal = { 1.0f, 0.0f };
        ball.applyNewVelocities(normal);
        assertNotNull(ball.getVelocityVector());
    }

    @Test
    public void testCheckCollisionWithWall() {
        Wall mockWall = mock(Wall.class);
        when(mockWall.getCollisionPerimeters()).thenReturn(new int[][] { { 1, 1 }, { 2, 2 } });

        boolean collision = ball.checkCollisionWithWall(mockWall);
        assertFalse(collision);
    }

    @Test
    public void testNormalizeVelocityToConstantSpeed() {
        ball.normalizeVelocityToConstantSpeed();
        float[] velocity = ball.getVelocityVector();
        float magnitude = (float) Math.sqrt(velocity[0] * velocity[1]);
        assertEquals(2.828f, magnitude, 0.02f); // Checking approximate magnitude
    }

    @Test
    public void testCheckCollisionWithLine_CollisionCooldownActive() {
        ball.collisionCooldown = 2;

        // Run the checkCollisionWithLine method
        boolean result = ball.checkCollisionWithLine(mockLine);

        // Verify that no collision is detected due to cooldown
        assertFalse(result, "Collision should not be detected when cooldown is active.");
    }

    @Test
    public void testCollisionChangeBallColour() {
        Wall mockWall = mock(Wall.class);
        mockWall.setWallColour("3");
        ball.collisionChangeBallColour(mockWall.getWallColour());

        assertEquals("green", ball.getBallColour().toLowerCase()); // has error
        assertEquals("B3", ball.getType());
    }

    @Test
    public void testHoleAttractionCheck() {
        Hole mockHole = mock(Hole.class);
        when(mockHole.XCentre()).thenReturn(100);
        when(mockHole.YCentre()).thenReturn(100);

        ball.holeAttractionCheck(mockApp, mockHole);
        assertNotNull(ball.getVelocityVector());
    }

    @Test
    public void testBallWithinAttractionRange() {
        // Set up the hole to be close to the ball (e.g., at (6, 6))
        when(mockHole.XCentre()).thenReturn(2 * App.CELLSIZE + 32);
        when(mockHole.YCentre()).thenReturn(2 * App.CELLSIZE + App.TOPBAR + 32);

        // Run the holeAttractionCheck method
        ball.holeAttractionCheck(mockApp, mockHole);

        // Check that the velocity has been adjusted (assuming initial velocity is
        // non-zero)
        float[] velocity = ball.getVelocityVector();
        assertNotEquals(0, velocity[0], "X velocity should be affected by hole attraction.");
        assertNotEquals(0, velocity[1], "Y velocity should be affected by hole attraction.");
    }

    @Test
    public void testHoleAttraction_BallFarFromHole() {
        // Set up the hole position far from the ball
        when(mockHole.XCentre()).thenReturn(500);
        when(mockHole.YCentre()).thenReturn(500);

        // Call holeAttractionCheck with a far away hole
        ball.holeAttractionCheck(mockApp, mockHole);

        // Verify that the ball's size factor remains at 1.0
        assertEquals(1.0f, ball.currentSizeFactor, 0.1);
    }

    @Test
    public void testBallCapturedByHole() {
        // Set up the hole to be very close to the ball (e.g., at (5, 5))
        when(mockHole.XCentre()).thenReturn(2 * App.CELLSIZE + 32);
        when(mockHole.YCentre()).thenReturn(3 * App.CELLSIZE + App.TOPBAR + 32);
        when(mockHole.getHoleColour()).thenReturn("blue"); // Hole has matching color

        // Run the holeAttractionCheck method
        ball.holeAttractionCheck(mockApp, mockHole);

        assertTrue(ball.shouldBeRemoved(), "Ball should be marked for removal after being captured.");
    }

    @Test
    public void testCollisionWithLineFalse() {
        // checks that a collision is not detected when the ball is not close to the
        // line
        boolean result = ball.checkCollisionWithLine(mockLine);
        assertFalse(result);
    }

    public void testCollisionWithLineTrue() {
        // checks that a collision is detected when the ball is close to / colliding
        // with the line
        ball.setPosition(75, 75);
        boolean result = ball.checkCollisionWithLine(mockLine);
        assertTrue(result);
    }

    @Test
    public void testCheckCollisionWithLine_CollisionDetected() {
        // Set up line points that are close enough for a collision
        ArrayList<int[]> points = new ArrayList<>();
        points.add(new int[] { 5 * App.CELLSIZE, 5 * App.CELLSIZE + App.TOPBAR });
        points.add(new int[] { 6 * App.CELLSIZE, 6 * App.CELLSIZE + App.TOPBAR });

        Mockito.when(mockLine.getPoints()).thenReturn(points);

        // Before running the collision, ensure that the line should not be removed
        assertFalse(mockLine.shouldBeRemoved(), "Line should not be removed initially.");

        // Run the checkCollisionWithLine method
        boolean result = ball2.checkCollisionWithLine(mockLine);

        // Verify that a collision is detected
        assertTrue(result, "Collision should be detected when the ball intersects with the line.");

        // Verify that the ball's velocity is updated after the collision
        float[] velocity = ball2.getVelocityVector();
        assertNotEquals(2f, velocity[0], "X velocity should be updated after the collision.");
        assertNotEquals(-2f, velocity[1], "Y velocity should be updated after the collision.");

        // Check that the collisionCooldown is updated
        assertEquals(3, ball2.collisionCooldown, "Collision cooldown should be set after collision.");
    }
}
