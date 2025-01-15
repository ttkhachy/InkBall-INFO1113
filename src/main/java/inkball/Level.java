package inkball;

import java.util.Map;

/**
 * The Level class represents a level in the Inkball game.
 */
public class Level {
    /** The layout file of the level */
    private String layout;
    /** The time limit for the level in seconds. */
    private int time;
    /** The interval in seconds at which balls are spawned. */
    private int spawnInterval;
    /** A modifier for increasing the score after hole capture. */
    private float scoreIncreaseModifier;
    /** A modifier for decreasing the score after incorrect hole capture */
    private float scoreDecreaseModifier;
    /** An array representing the balls which will be spawned in the level. */
    private String[] balls;
    /** A map storing the score increase for each ball color. */
    private Map<String, Integer> scoreIncrease;
    /** A map storing the score decrease for each ball color. */
    private Map<String, Integer> scoreDecrease;

    /**
     * Constructs a new Level object with the specified parameters.
     *
     * @param layout                the layout of the level
     * @param time                  the time limit for the level in seconds
     * @param spawnInterval         the interval (in frames) between ball spawns
     * @param scoreIncreaseModifier a modifier for score increases
     * @param scoreDecreaseModifier a modifier for score decreases
     * @param balls                 an array representing the types of balls in the
     *                              level
     * @param scoreIncrease         a map containing score increases for each ball
     *                              color
     * @param scoreDecrease         a map containing score decreases for each ball
     *                              color
     */
    public Level(String layout, int time, int spawnInterval, float scoreIncreaseModifier, float scoreDecreaseModifier,
            String[] balls, Map<String, Integer> scoreIncrease, Map<String, Integer> scoreDecrease) {
        this.layout = layout;
        this.time = time;
        this.spawnInterval = spawnInterval;
        this.scoreIncreaseModifier = scoreIncreaseModifier;
        this.scoreDecreaseModifier = scoreDecreaseModifier;
        this.balls = balls;
        this.scoreIncrease = scoreIncrease;
        this.scoreDecrease = scoreDecrease;
    }

    /**
     * Returns the layout of the level.
     *
     * @return the layout of the level
     */
    public String getLayout() {
        return layout;
    }

    /**
     * Returns the time limit for the level.
     *
     * @return the time limit for the level in seconds
     */
    public int getTime() {
        return time;
    }

    /**
     * Returns the interval between ball spawns.
     *
     * @return the spawn interval in frames
     */
    public int getSpawnInterval() {
        return spawnInterval;
    }

    /**
     * Returns the modifier for score increases.
     *
     * @return the score increase modifier
     */
    public float scoreIncreaseModifier() {
        return scoreIncreaseModifier;
    }

    /**
     * Returns the modifier for score decreases.
     *
     * @return the score decrease modifier
     */
    public float scoreDecreaseModifier() {
        return scoreDecreaseModifier;
    }

    /**
     * Returns the balls which are to be spawned in the level.
     *
     * @return an array of ball types available in the level
     */
    public String[] getBalls() {
        return balls;
    }

    /**
     * Returns the score increase for a given ball color.
     *
     * @param BallColour the color of the ball
     * @return the score increase for the specified ball color
     */
    public int getScoreIncrease(String BallColour) {
        return this.scoreIncrease.get(BallColour);
    }

    /**
     * Returns the score decrease for a given ball color.
     *
     * @param BallColour the color of the ball
     * @return the score decrease for the specified ball color
     */
    public int getScoreDecrease(String BallColour) {
        return this.scoreDecrease.get(BallColour);
    }

}
