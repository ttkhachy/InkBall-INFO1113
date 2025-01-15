package inkball;

public class Spawner extends GameObject {
    public Spawner(String type, int x, int y, int width, int height) {
        super(type, x, y, width, height);
    }

    public int getXCentre() {
        return this.x * App.CELLSIZE + App.CELLSIZE / 2;
    }

    public int getYCentre() {
        return this.y * App.CELLSIZE + App.CELLSIZE / 2 + App.TOPBAR;
    }

}
