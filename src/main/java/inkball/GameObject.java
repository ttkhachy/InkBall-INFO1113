package inkball;

public class GameObject {
    private String type; // e.g., "Hole1", "Spawner"
    protected int x;
    protected int y;
    protected float width;
    protected float height;

    public GameObject(String type, int x, int y, float width, float height) {
        this.type = type;
        this.x = x; // x-coordinate of top-left corner
        this.y = y; // y-coordinate of top-left corner
        this.width = width;
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }
}
