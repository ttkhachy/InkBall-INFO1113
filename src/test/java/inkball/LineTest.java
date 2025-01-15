package inkball;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class LineTest {
    private Line line;

    @BeforeEach
    public void setUp() {
        // Create a Line starting at position (50, 50)
        line = new Line(50, 50);
    }

    @Test
    public void testConstructor() {
        ArrayList<int[]> points = line.getPoints();
        assertEquals(1, points.size(), "Line should contain exactly one point after construction.");
        assertArrayEquals(new int[] { 50, 50 }, points.get(0), "The starting point should be (50, 50).");
    }

    @Test
    public void testAddPoints_Valid() {
        // Add a valid point
        line.addPoints(100, 100);
        ArrayList<int[]> points = line.getPoints();

        assertEquals(2, points.size(), "Line should contain two points after adding a valid point.");
        assertArrayEquals(new int[] { 100, 100 }, points.get(1), "The second point should be (100, 100).");
    }

    @Test
    public void testAddPoints_OutOfBounds() {
        // Add an out of bounds point
        line.addPoints(-10, -10);
        ArrayList<int[]> points = line.getPoints();

        assertEquals(1, points.size(), "Line should not add an out of bounds point.");
    }

    @Test
    public void testClearLine() {
        // Add some points and then clear the line
        line.addPoints(100, 100);
        line.clearLine();
        ArrayList<int[]> points = line.getPoints();

        assertEquals(0, points.size(), "Line should have no points after clearing.");
    }

    @Test
    public void testIsMouseNearLine_Near() {
        // Add a second point to form a line
        line.addPoints(100, 100);

        // Simulate mouse coordinates near the line
        assertTrue(line.isMouseNearLine(75, 75), "Mouse should be considered near the line.");
    }

    @Test
    public void testIsMouseNearLine_Far() {
        // Add a second point to form a line
        line.addPoints(100, 100);

        // Simulate mouse coordinates near the line
        assertFalse(line.isMouseNearLine(400, 400), "Mouse should not be considered near the line.");
    }

    @Test
    public void testDistancePointToSegment() {
        // Test with known points (50, 50) and (100, 100)
        float dist = line.distancePointToSegment(75, 75, 50, 50, 100, 100);
        assertEquals(0, dist, 0.1, "The distance from (75, 75) to the line segment should be 0.");
    }

    @Test
    public void testDist() {
        // Test distance between (0, 0) and (3, 4) (a 3-4-5 triangle)
        float dist = line.dist(0, 0, 3, 4);
        assertEquals(5.0, dist, 0.1, "The distance between (0, 0) and (3, 4) should be 5.0.");
    }

    @Test
    public void testSetRemoved() {
        assertFalse(line.shouldBeRemoved());
        line.setShouldBeRemoved(true);
        assertTrue(line.shouldBeRemoved());

    }
}
