package inkball;

import java.util.Random;
import processing.core.PImage;

/**
 * The Ball class represents a ball object in the Inkball game. It handles
 * movement, collision detection, and interactions with walls and holes.
 */
public class Ball extends GameObject {
    private static final float ATTRACTION_COEFFICIENT = 0.005f;
    /** The current x-position of the ball in pixels. */
    public int xPosition;
    /** The current y-position of the ball in pixels. */
    public int yPosition;
    /** The x-velocity of the ball. */
    private float xVelocity;
    /** The y-velocity of the ball. */
    private float yVelocity;
    /** The type of the ball (e.g., "B1" for orange, "B2" for blue). */
    private String ballType;
    /** The current color of the ball. */
    private String ballColour;
    /** The radius of the ball in pixels. */
    private int radius;
    /** Whether the ball is currently spawned in the game. */
    protected boolean spawned;
    /** The cooldown time for collisions to prevent repeated collisions. */
    public int collisionCooldown = 0;
    /** The original width of the ball in pixels. */
    private float originalWidth;
    /** The original height of the ball in pixels. */
    private float originalHeight;
    /** The current size factor for the ball. */
    public float currentSizeFactor = 1.0f;
    /** Whether the ball should be removed from the game */
    public boolean shouldBeRemoved;
    /** The width of the ball for drawing purposes. */
    private float ball_draw_width;
    /** The height of the ball for drawing purposes. */
    private float ball_draw_height;

    /** The image representing the ball. */
    private PImage ball;

    /** A random object used for velocity. */
    Random rand = new Random();

    /**
     * Constructs a new Ball object with the given parameters.
     *
     * @param ballType   the type of the ball (e.g., "B1" for orange)
     * @param xPosition  the initial x-position of the ball (in grid units)
     * @param yPosition  the initial y-position of the ball (in grid units)
     * @param ballColour the color of the ball
     * @param width      the width of the ball in pixels
     * @param height     the height of the ball in pixels
     */
    public Ball(String ballType, int xPosition, int yPosition, String ballColour, float width, float height) {
        super(ballType, xPosition, yPosition, width, height);
        this.ballType = ballType;
        this.ballColour = setBallColour(ballType);
        this.xPosition = xPosition * App.CELLSIZE;
        this.yPosition = yPosition * App.CELLSIZE + App.TOPBAR;
        this.xVelocity = rand.nextBoolean() ? 2f : -2f;
        this.yVelocity = rand.nextBoolean() ? 2f : -2f;
        this.radius = (int) width / 2;
        this.originalWidth = width;
        this.originalHeight = height;
        this.ball = App.levelFileSymbolSprites.get(ballType);
        // this.app = app;
        this.spawned = false;
        this.shouldBeRemoved = false;
        this.ball_draw_width = originalWidth;
        this.ball_draw_height = originalHeight;

    }

    /**
     * Sets the spawned status of the ball.
     *
     * @param status true if the ball is spawned, false otherwise
     */
    public void setSpawned(boolean status) {
        this.spawned = status;
    }

    /**
     * Returns the current color of the ball.
     *
     * @return the current color of the ball
     */

    public String getBallColour() {
        return this.ballColour;
    }

    /**
     * Returns whether the ball should be removed from the game.
     *
     * @return true if the ball should be removed, false otherwise
     */
    public boolean shouldBeRemoved() {
        return this.shouldBeRemoved;
    }

    /**
     * Returns whether the ball is currently spawned.
     *
     * @return true if the ball is spawned, false otherwise
     */
    public boolean getSpawned() {
        return spawned;
    }

    /**
     * Draws the ball on the screen.
     *
     * @param app the main app object for rendering
     */

    public void draw(App app) {

        PImage ball = App.levelFileSymbolSprites.get(ballType);
        float ball_draw_width = originalWidth * currentSizeFactor;
        float ball_draw_height = originalHeight * currentSizeFactor;
        float xCentre = xPosition + radius;
        float yCentre = yPosition + radius;
        float xDrawPosition = xCentre - (ball_draw_width / 2);
        float yDrawPosition = yCentre - (ball_draw_height / 2);

        app.image(ball, xDrawPosition, yDrawPosition, ball_draw_width, ball_draw_height);
    }

    /**
     * Moves the ball according to its velocity
     *
     * @param app the main app object
     */

    public void move(App app) {
        if (this.spawned && !app.gamePaused) {
            xPosition += xVelocity;
            yPosition += yVelocity;

            if (collisionCooldown > 0) {
                collisionCooldown--;
            }
        }
    }

    /**
     * Returns the current velocity vector of the ball.
     *
     * @return a float array containing the x and y velocities of the ball
     */
    public float[] getVelocityVector() {
        return new float[] { xVelocity, yVelocity };
    }

    /**
     * Checks if the ball is colliding with a {@link Line} .
     *
     * @param line the line to check collision with
     * @return true if the ball collides with the line, false otherwise
     */
    public boolean checkCollisionWithLine(Line line) {
        if (collisionCooldown > 0) {
            return false;
        }
        float nextX = xPosition + radius;
        float nextY = yPosition + radius;

        double[] distances = new double[line.getPoints().size()];

        for (int i = 0; i < line.getPoints().size(); i++) {
            distances[i] = getDistance(line.getPoints().get(i)[0], line.getPoints().get(i)[1], nextX, nextY);
        }

        for (int i = 0; i < distances.length - 1; i++) {
            if (isCollidingWithSegment(distances[i + 1], distances[i], getDistance(line.getPoints().get(i)[0],
                    line.getPoints().get(i)[1], line.getPoints().get(i + 1)[0], line.getPoints().get(i + 1)[1]))) {
                int[] P1 = line.getPoints().get(i);
                int[] P2 = line.getPoints().get(i + 1);

                line.setShouldBeRemoved(true);

                float dx = P2[0] - P1[0];
                float dy = P2[1] - P1[1];

                float[] normal1 = { -dy, dx };
                float[] normal2 = { dy, -dx };

                // step 3
                normalise(normal1);
                normalise(normal2);

                int wallMidpointX = (int) (dx / 2);
                int wallMidpointY = (int) (dy / 2);

                // step 4
                float[] closestNormal = getCloserNormalVector(normal1, normal2, wallMidpointX, wallMidpointY);

                applyNewVelocities(closestNormal);

                collisionCooldown = 3;
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the ball is colliding with a {@link Wall}.
     *
     * @param wall the {@link Wall} to check collision with
     * @return true if the ball collides with the wall, false otherwise
     */
    public boolean checkCollisionWithWall(Wall wall) {
        if (collisionCooldown > 0) {
            return false;
        }

        float nextX = xPosition + radius + xVelocity;
        float nextY = yPosition + radius + yVelocity;
        int[][] wallSegments = wall.getCollisionPerimeters();

        double[] distances = new double[wallSegments.length];

        for (int i = 0; i < wallSegments.length; i++) {
            distances[i] = getDistance(wallSegments[i][0], wallSegments[i][1], nextX, nextY);
        }

        for (int i = 0; i < distances.length; i++) {
            int nextIndex = (i + 1) % distances.length;
            if (isCollidingWithWallSegment(distances[i], distances[nextIndex]) || isCollidingWithGameEdge()) {
                int[] P1 = wallSegments[i];
                int[] P2 = wallSegments[nextIndex];
                float dx = P2[0] - P1[0];
                float dy = P2[1] - P1[1];

                float[] normal1 = { -dy, dx };
                float[] normal2 = { dy, -dx };

                // step 3
                normalise(normal1);
                normalise(normal2);

                int wallMidpointX = (int) dx / 2;
                int wallMidpointY = (int) dy / 2;

                // step 4
                float[] closestNormal = getCloserNormalVector(normal1, normal2, wallMidpointX, wallMidpointY);
                collisionChangeBallColour(wall.getWallColour());

                applyNewVelocities(closestNormal);
                collisionCooldown = 5;
                return true;

            }
        }
        return false;
    }

    /**
     * Checks if the ball is colliding with a segment of a line.
     *
     * @param d1 the distance from the first point of the segment
     * @param d2 the distance from the second point of the segment
     * @param d3 the distance between the two points
     * @return true if the ball is colliding with the segment, false otherwise
     */

    public boolean isCollidingWithSegment(double d1, double d2, double d3) {
        return d1 + d2 < d3 + radius;
    }

    /**
     * Checks if the ball is colliding with the game edge.
     *
     * @return true if the ball is colliding with the game edge, false otherwise
     */
    public boolean isCollidingWithGameEdge() {
        if (xPosition < 0 || xPosition > App.WIDTH || yPosition < App.TOPBAR || yPosition > App.HEIGHT) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the ball is colliding with a segment of a wall.
     *
     * @param d1 the distance from the first point of the wall segment
     * @param d2 the distance from the second point of the wall segment
     * @return true if the ball is colliding with the wall segment, false otherwise
     */
    public boolean isCollidingWithWallSegment(double d1, double d2) {
        return d1 + d2 < App.CELLSIZE + radius - 4;
    }

    /**
     * Calculates the distance between two points.
     *
     * @param aX the x-coordinate of the first point
     * @param aY the y-coordinate of the first point
     * @param bX the x-coordinate of the second point
     * @param bY the y-coordinate of the second point
     * @return the distance between the two points
     */
    public double getDistance(int aX, int aY, double bX, double bY) {
        // bruh PApplet literally had a dist function saddd
        double d = Math.sqrt(Math.pow(bX - aX, 2) + Math.pow(bY - aY, 2));
        return d;
    }

    /**
     * Normalizes the given vector.
     *
     * @param vec the vector to normalize
     */
    public void normalise(float[] vec) {
        float magnitude = (float) Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1]);
        if (magnitude != 0) {
            vec[0] /= magnitude;
            vec[1] /= magnitude;
        }
    }

    /**
     * Gets the closer of two normal vectors to a midpoint.
     *
     * @param n1            the first normal vector
     * @param n2            the second normal vector
     * @param wallMidPointX the x-coordinate of the midpoint
     * @param wallMidPointY the y-coordinate of the midpoint
     * @return the closer normal vector to the midpoint
     */

    public float[] getCloserNormalVector(float[] n1, float[] n2, int wallMidPointX, int wallMidPointY) {
        double n1Distance = getDistance(wallMidPointX, wallMidPointY, n1[0], n1[1]);
        double n2Distance = getDistance(wallMidPointX, wallMidPointY, n2[0], n2[1]);
        if (n1Distance < n2Distance) {
            return n1;
        } else {
            return n2;
        }
    }

    /**
     * Applies new velocities to the ball based on a normal vector.
     *
     * @param norm the normal vector used to reflect the ball's velocity
     */

    public void applyNewVelocities(float[] norm) {
        // compute dot product v · n
        float dotProduct = (xVelocity * norm[0] + yVelocity * norm[1]);

        // Reflect velocity using the formula u = v - 2(v · n)n
        xVelocity = xVelocity - (2 * dotProduct * norm[0]);
        yVelocity = yVelocity - (2 * dotProduct * norm[1]);
        normalizeVelocityToConstantSpeed();
    }

    /**
     * Changes the ball's color when it collides with a {@link Wall} of a specific
     * color.
     *
     * @param wallColour the color of the wall that the ball collided with
     */
    public void collisionChangeBallColour(String wallColour) {
        if (wallColour == null) {
            return;
        }
        switch (wallColour.toLowerCase()) {
            case "orange":
                ballType = "B1";
                ballColour = "orange";
                break;
            case "blue":
                ballType = "B2";
                ballColour = "blue";
                break;
            case "green":
                ballType = "B3";
                ballColour = "green";
                break;
            case "yellow":
                ballType = "B4";
                ballColour = "yellow";
                break;
            default:
                return;
        }

        PImage newBallImage = App.levelFileSymbolSprites.get(ballType);
        ball = newBallImage;

    }

    /**
     * Handles the attraction of the ball toward a hole.
     *
     * @param app  the main app object
     * @param hole the hole the ball is attracted to
     */
    public void holeAttractionCheck(App app, Hole hole) {
        int xCentre = xPosition + radius;
        int yCentre = yPosition + radius;

        double dist = getDistance(xCentre, yCentre, hole.XCentre(), hole.YCentre());
        if (dist < 32) {
            float[] ballHoleVector = { hole.XCentre() - xCentre, hole.YCentre() - yCentre };
            normalise(ballHoleVector);
            float[] direction = ballHoleVector;

            // Avoid division by zero
            if (direction[0] == 0 && direction[1] == 0) {
                direction = new float[] { 0, 0 };
            }

            float forceMagnitude = 1 - (ATTRACTION_COEFFICIENT * (float) dist);
            forceMagnitude = Math.max(0, forceMagnitude); // Ensure

            xVelocity += direction[0] * forceMagnitude;
            yVelocity += direction[1] * forceMagnitude;

            float speed = (float) Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
            float maxSpeed = 5.0f; // Adjust as needed
            if (speed > maxSpeed) {
                xVelocity = (xVelocity / speed) * maxSpeed;
                yVelocity = (yVelocity / speed) * maxSpeed;
            }

            currentSizeFactor = (float) dist / 32;

            if (dist < 10) {
                captureBall(app, hole);
            }

        } else {
            currentSizeFactor = 1.0f;
        }

    }

    /**
     * Normalizes the velocity of the ball to a constant speed.
     */
    public void normalizeVelocityToConstantSpeed() {
        float magnitude = (float) Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);

        float constantSpeed = (float) (2 * Math.sqrt(2));

        // normalize the velocity to the constant speed
        if (magnitude != 0) {
            xVelocity = (xVelocity / magnitude) * constantSpeed;
            yVelocity = (yVelocity / magnitude) * constantSpeed;
        }
    }

    /**
     * Handles the capture of the ball by a hole, including updating the score and
     * marking the ball for removal.
     *
     * @param app  the main app object
     * @param hole the hole capturing the ball
     */
    public void captureBall(App app, Hole hole) {
        // Check if the ball's color matches the hole's color (including grey rules)
        boolean success = ballColour.equalsIgnoreCase(hole.getHoleColour()) || ballColour.equalsIgnoreCase("grey")
                || hole.getHoleColour().equalsIgnoreCase("grey");

        if (success) {
            // Increase score
            app.score += app.currentLevel.getScoreIncrease(ballColour) * app.currentLevel.scoreIncreaseModifier();
        } else {
            // Decrease score
            app.score -= app.currentLevel.getScoreDecrease(ballColour) * app.currentLevel.scoreDecreaseModifier();
            // Requeue the ball to be spawned again
            app.addBallToSpawnQueue(this);
        }
        this.shouldBeRemoved = true;
        this.setSpawned(false);

    }

    /**
     * Sets the position of the ball.
     *
     * @param x the x-coordinate to set the ball's position to
     * @param y the y-coordinate to set the ball's position to
     */
    public void setPosition(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
    }

    /**
     * Shifts the ball to the left by a given number of units.
     *
     * @param num the number of units to shift the ball left by
     */
    public void shiftLeftBy(int num) {
        this.yPosition -= num;
    }

    /**
     * Resets the ball velocity to either 2 or -2
     */
    public void resetVelocity() {
        this.xVelocity = rand.nextBoolean() ? 2f : -2f;
        this.yVelocity = rand.nextBoolean() ? 2f : -2f;
    }

    /**
     * Sets the ball colour based on the symbol of the ball
     * 
     * @param ballSymbol the given symbol of the ball
     */
    public String setBallColour(String ballSymbol) {
        if (ballSymbol.equals("B0")) {
            return "grey";
        }
        if (ballSymbol.equals("B1")) {
            return "orange";
        }
        if (ballSymbol.equals("B2")) {
            return "blue";
        }
        if (ballSymbol.equals("B3")) {
            return "green";
        }
        if (ballSymbol.equals("B4")) {
            return "yellow";
        } else {
            return "grey";
        }

    }

}