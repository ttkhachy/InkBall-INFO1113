package inkball;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import processing.core.PImage;

public class TileTests {
    private Tile mockTile;
    private App mockApp;
    private PImage mockPImage;

    @BeforeEach
    public void setUp() {
        // mock dependencies
        mockApp = mock(App.class);
        mockPImage = mock(PImage.class);

        mockTile = new Tile(10, 10);

    }

    @Test
    public void TileXCellSizeTest() {
        System.out.println(mockTile.getXCellSize());
        assertEquals(mockTile.getXCellSize(), 320);
    }

    @Test
    public void TileYCellSizeTest() {
        assertEquals(mockTile.getYCellSize(), 384);
    }
}
