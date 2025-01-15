package inkball;

public class Wall extends Tile {
    private String wallColour;

    public Wall(int x, int y, String colour) {
        super(x, y);
        this.wallColour = setWallColour(colour);
    }

    public String getWallColour() {
        return this.wallColour;
    }

    public String setWallColour(String fileSymbol) {
        if (fileSymbol.equalsIgnoreCase("1")) {
            return "orange";
        } else if (fileSymbol.equalsIgnoreCase("2")) {
            return "blue";
        } else if (fileSymbol.equalsIgnoreCase("3")) {
            return "green";
        } else if (fileSymbol.equalsIgnoreCase("4")) {
            return "yellow";
        } else {
            return null;
        }

    }

    public int[][] getCollisionPerimeters() {
        int[][] wallSegments = { { this.getXCellSize(), this.getYCellSize() },
                { this.getXCellSize() + App.CELLSIZE, this.getYCellSize() },
                { this.getXCellSize() + App.CELLSIZE, this.getYCellSize() + App.CELLSIZE },
                { this.getXCellSize(), this.getYCellSize() + App.CELLSIZE } };
        return wallSegments;
    }

}
