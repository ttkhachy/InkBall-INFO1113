package inkball;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Hole} class.
 */
public class HoleTest {

    private Hole hole1;
    private Hole hole2;
    private Hole hole3;
    private Hole hole4;
    private Hole defaultHole;

    /**
     * Setup method to initialize the test objects before each test.
     */
    @BeforeEach
    public void setUp() {
        // Initialize holes with different types
        hole1 = new Hole("Hole1", 1, 1, 2, 2);
        hole2 = new Hole("Hole2", 2, 2, 2, 2);
        hole3 = new Hole("Hole3", 3, 3, 2, 2);
        hole4 = new Hole("Hole4", 4, 4, 2, 2);
        defaultHole = new Hole("Hole0", 0, 0, 2, 2); // For default case
    }

    /**
     * Test that the constructor correctly initializes a {@link Hole} object.
     */
    @Test
    public void testHoleConstructor() {
        assertEquals("Hole1", hole1.getType());
        assertEquals(1, hole1.getX());
        assertEquals(1, hole1.getY());
        assertEquals(2, hole1.getWidth());
        assertEquals(2, hole1.getHeight());
    }

    /**
     * Test the XCentre() method to ensure the correct x-center position is
     * returned.
     */
    @Test
    public void testXCentre() {
        int expectedXCentre = 1 * App.CELLSIZE + 32;
        assertEquals(expectedXCentre, hole1.XCentre());
    }

    /**
     * Test the YCentre() method to ensure the correct y-center position is
     * returned.
     */
    @Test
    public void testYCentre() {
        int expectedYCentre = 1 * App.CELLSIZE + App.TOPBAR + 32;
        assertEquals(expectedYCentre, hole1.YCentre());
    }

    /**
     * Test the getHoleColour() method for different hole types.
     */
    @Test
    public void testGetHoleColour() {
        assertEquals("orange", hole1.getHoleColour());
        assertEquals("blue", hole2.getHoleColour());
        assertEquals("green", hole3.getHoleColour());
        assertEquals("yellow", hole4.getHoleColour());
        assertEquals("grey", defaultHole.getHoleColour());
    }
}
