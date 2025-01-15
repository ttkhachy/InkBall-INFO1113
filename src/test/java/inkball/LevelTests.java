package inkball;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import processing.core.PImage;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

public class LevelTests {
    private Level mockLevel;
    private App mockApp;
    private PImage mockPImage;
    Map<String, Integer> scoreIncrease;
    Map<String, Integer> scoreDecrease;

    @BeforeAll
    public void createHashMaps() {
        scoreIncrease.put("grey", 50);
        scoreIncrease.put("orange", 60);
        scoreIncrease.put("blue", 70);
        scoreIncrease.put("green", 80);
        scoreIncrease.put("yellow", 90);
        scoreDecrease.put("grey", 0);
        scoreDecrease.put("orange", 10);
        scoreDecrease.put("blue", 20);
        scoreDecrease.put("green", 25);
        scoreDecrease.put("yellow", 100);

    }

    @BeforeEach
    public void setUp() {
        mockLevel = new Level(null, 100, 10, (float) 1.2, (float) 1.4,
                new String[] { "blue", "green", "grey", "orange", "grey" }, scoreIncrease, scoreDecrease);
    }

    public void testTime() {
        assertEquals(mockLevel.getTime(), 100);
    }

}
