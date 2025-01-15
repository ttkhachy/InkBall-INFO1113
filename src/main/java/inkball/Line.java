//tbh idk if i need this or if im doing too much -  i guess this could implement collidable as well
package inkball;

import java.util.ArrayList;

/**
 * The Line class represents a drawable line in the Inkball game.
 */

public class Line {
    /** A list of points that make up the line */
    private ArrayList<int[]> points;
    /** A flag indicating whether the line should be removed from the game. */
    private boolean shouldBeRemoved = false;

    /**
     * Constructs a new Line object starting at the given coordinates.
     *
     * @param startX the x-coordinate of the starting point of the line
     * @param startY the y-coordinate of the starting point of the line
     */
    public Line(int startX, int startY) {
        points = new ArrayList<>();
        points.add(new int[] { startX, startY });
    }

    /**
     * Draws the line on the game screen using the provided App object.
     *
     * @param app the main App object responsible for rendering the line
     */
    public void draw(App app) {
        app.strokeWeight(10);

        for (int i = 0; i < points.size() - 1; i++) {
            app.line(points.get(i)[0], points.get(i)[1], points.get(i + 1)[0], points.get(i + 1)[1]);
        }
    }

    /**
     * Adds a new point to the line if the point is within the game boundaries.
     *
     * @param x the x-coordinate of the new point
     * @param y the y-coordinate of the new point
     */
    public void addPoints(int x, int y) {
        if (x < 0 || x > App.WIDTH || y < App.TOPBAR || y > App.HEIGHT) {
            return;
        }

        points.add(new int[] { x, y });
    }

    /**
     * Sets the flag indicating whether the line should be removed.
     *
     * @param shouldBeRemoved true if the line should be removed, false otherwise
     */

    public void setShouldBeRemoved(boolean shouldBeRemoved) {
        this.shouldBeRemoved = shouldBeRemoved;
    }

    /**
     * Returns whether the line should be removed from the game.
     *
     * @return true if the line should be removed, false otherwise
     */
    public boolean shouldBeRemoved() {
        return shouldBeRemoved;
    }

    /**
     * Returns the list of points that make up the line.
     *
     * @return the list of points in the line
     */
    public ArrayList<int[]> getPoints() {
        return points;
    }

    /**
     * Clears all points from the line, effectively removing it from the game.
     */
    public void clearLine() {
        points.clear();
    }

    /**
     * Checks if the mouse cursor is near the line, within a certain threshold.
     *
     * @param mouseX the x-coordinate of the mouse cursor
     * @param mouseY the y-coordinate of the mouse cursor
     * @return true if the mouse is near the line, false otherwise
     */
    public boolean isMouseNearLine(int mouseX, int mouseY) {
        float threshold = 10.0f;
        for (int i = 0; i < points.size() - 1; i++) {
            int[] p1 = points.get(i);
            int[] p2 = points.get(i + 1);

            if (distancePointToSegment(mouseX, mouseY, p1[0], p1[1], p2[0], p2[1]) <= threshold) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the shortest distance between a point and a line segment.
     *
     * @param px the x-coordinate of the point
     * @param py the y-coordinate of the point
     * @param x1 the x-coordinate of the first point of the segment
     * @param y1 the y-coordinate of the first point of the segment
     * @param x2 the x-coordinate of the second point of the segment
     * @param y2 the y-coordinate of the second point of the segment
     * @return the shortest distance from the point to the segment
     */
    public float distancePointToSegment(int px, int py, int x1, int y1, int x2, int y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;

        if (dx == 0 && dy == 0) {
            // The segment is a point
            return dist(px, py, x1, y1);
        }

        // Calculate the projection of the point onto the line segment
        float t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);

        // Clamp t to the range [0, 1]
        t = Math.max(0, Math.min(1, t));

        // the closest point on the segment
        float closestX = x1 + t * dx;
        float closestY = y1 + t * dy;

        // Return the distance from the point to the closest point on the segment
        return dist(px, py, closestX, closestY);
    }

    /**
     * Calculates the distance between two points.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @return the distance between the two points
     */
    public float dist(float x1, float y1, float x2, float y2) {
        return (float) Math.hypot(x2 - x1, y2 - y1);
    }

}
