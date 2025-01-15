//tbh idek if these classes are necessary - maybe i could make these extend from Tile same for spawner and wall??

package inkball;

/**
 * The Hole class represents a hole object in the Inkball game.
 */

public class Hole extends GameObject {
    /**
     * Constructs a new Hole object.
     *
     * @param type   the type of the hole (e.g., "Hole1", "Hole2")
     * @param x      the x-position of the hole on the grid
     * @param y      the y-position of the hole on the grid
     * @param width  the width of the hole in grid units
     * @param height the height of the hole in grid units
     */
    public Hole(String type, int x, int y, int width, int height) {
        super(type, x, y, width, height);
        System.out.println(type);
    }

    /**
     * Returns the x-coordinate of the center of the hole in pixels.
     *
     * @return the x-coordinate of the center of the hole in pixels
     */
    public int XCentre() {
        return x * App.CELLSIZE + 32; // is that correct?
    }

    /**
     * Returns the y-coordinate of the center of the hole in pixels.
     *
     * @return the y-coordinate of the center of the hole in pixels
     */
    public int YCentre() {
        return y * App.CELLSIZE + App.TOPBAR + 32;
    }

    /**
     * Returns the color associated with the hole based on its type.
     *
     * @return the color of the hole
     */
    public String getHoleColour() {

        switch (this.getType().toLowerCase()) {
            case "hole1":
                return "orange";
            case "hole2":
                return "blue";
            case "hole3":
                return "green";
            case "hole4":
                return "yellow";
            default:
                return "grey";
        }
    }

}
