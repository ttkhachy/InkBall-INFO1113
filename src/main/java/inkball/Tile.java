package inkball;

import processing.core.PImage;

public class Tile {

    private int x;
    private int y;

    public String levelFileSymbol;
    public boolean showYellowTile;
    public int yellowFrameCount;

    public Tile(int x, int y) {
        this.x = x; // should i just times this by the app cell size and make my life easier??
        this.y = y;
    }

    public void draw(App app) {
        if (this.levelFileSymbol == null) {
            return;
        }
        PImage tile = app.getSprite("tile");
        if (!this.getLevelFileSymbol().equals(" ")) {
            tile = App.levelFileSymbolSprites.get(this.levelFileSymbol);
        }
        if (this.showYellowTile) {
            tile = app.getSprite("wall4");
            yellowFrameCount++;
            if (yellowFrameCount == 2) {
                this.showYellowTile = false;
            }

        }

        app.image(tile, x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
    }

    public String getLevelFileSymbol() {
        return this.levelFileSymbol;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getXCellSize() {
        return this.x * App.CELLSIZE;
    }

    public int getYCellSize() {
        return this.y * App.CELLSIZE + App.TOPBAR;
    }

    public int getXOtherEnd() {
        return this.x * App.CELLSIZE + App.CELLSIZE;
    }

    public int getYOtherEnd() {
        return this.y * App.CELLSIZE + App.TOPBAR + App.CELLSIZE;
    }
}
