package inkball;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

import com.google.common.escape.ArrayBasedCharEscaper;

public class App extends PApplet {

    public static final int CELLSIZE = 32; // 8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64; // this was initially 0? changed it to 6 after doing maths
    public static int WIDTH = 576; // CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; // BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH / CELLSIZE;
    public static final int BOARD_HEIGHT = 20;
    /**
     * The index of the current level.
     */
    public int CURRENT_LEVEL;
    /**
     * The score at the start of the current level.
     */
    public int scoreAtStartOfLevel;

    /**
     * The frames per second (FPS) for the game.
     */
    public static final int FPS = 30;

    public String configPath;

    /**
     * Tracks the current game time in seconds.
     */
    public int time;
    /**
     * keeps track of the score time of the game
     */
    public int score;

    /**
     * Random instance for generating random numbers.
     */
    public static Random random = new Random();

    /**
     * a 2d array of the game board
     */
    public Tile[][] board;
    /**
     * keeps track of all the sprites in the game, where string is the name of the
     * image and the PImage object
     */
    private static HashMap<String, PImage> allSprites = new HashMap<>();
    /**
     * stores the sprites that are asosciated with the level.txt file
     */
    public static HashMap<String, PImage> levelFileSymbolSprites = new HashMap<>();
    /**
     * stores track of the ball symbol and the subsequent colour of the ball
     */
    public static HashMap<String, String> ballColours = new HashMap<>();

    /**
     * an array of each character in the level.txt file
     */
    private String[][] levelFileArray;
    /**
     * A list storing all {@link GameObject} instances.
     */
    public List<GameObject> gameObjects = new ArrayList<>();
    /**
     * A list storing all currently active {@link Ball} instances.
     */
    public ArrayList<Ball> activeBalls = new ArrayList<>();
    /**
     * A list storing all {@link Wall} instances in the game.
     */
    public ArrayList<Wall> allWalls = new ArrayList<>();
    /**
     * A list storing all {@link Hole} instances in the game.
     */
    public ArrayList<Hole> allHoles = new ArrayList<>();
    /**
     * A list storing all {@link Spawner} instances in the game.
     */
    private ArrayList<Spawner> allSpawners = new ArrayList<>();

    /**
     * A list storing all {@link Line} instances in the game.
     */
    protected List<Line> drawnLines = new ArrayList<>();; // List to store all drawn lines

    /**
     * stores the insatcne of the line that is currently being drawn - null if none
     */
    protected Line currentLine;
    /**
     * marks if the game has been completed
     */
    protected boolean gameOver;
    /**
     * marks if the game is currently paused
     */
    protected boolean gamePaused;
    /**
     * keeps track of the frame which the pause was initiated
     */
    private int pauseStartFrame;

    /**
     * A list storing all {@link Level} instances loaded from the configuration
     * file.
     */

    protected List<Level> levels;
    /**
     * stores the instance of the current level
     */
    public Level currentLevel;
    /**
     * stores the frame which the current level was started
     */
    private int levelStartFrame;

    /**
     * A list of {@link Line} instances to be removed from the game, after being
     * captured by the holes
     */

    protected List<Ball> ballsToRemove = new ArrayList<>();
    /**
     * A list of {@link Line} instances to be removed from the game., after being
     * touched by a ball.
     */

    protected List<Line> linesToRemove = new ArrayList<>();
    /**
     * A queue storing balls yet to be spawned.
     */
    public Queue<Ball> ballSpawnQueue = new LinkedList<>(); // Queue to store balls yet to be spawned
    /**
     * The interval between ball spawns, in frames.
     */
    private int spawnInterval; // Interval between spawns, in frames
    /**
     * Countdown timer for spawning the next ball
     */
    private float spawnTimer;
    /**
     * A list of balls currently displayed in the ball queue.
     */
    private List<Ball> displayedBalls = new ArrayList<>(); // Balls currently displayed in the queue
    /**
     * The count of yellow number of yellow tiles which have been displayed during
     * the animation.
     */
    public int yellowTileCount;
    /**
     * Boolean flag indicating if the yellow tile animation is complete.
     */
    public boolean yellowTileAnimationComplete;
    /**
     * Boolean flag indicating if the score addition is finished.
     */
    public boolean scoreAdditionFinished;
    // Feel free to add any additional methods or attributes you want. Please put
    // classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    public PImage getSprite(String s) {
        PImage result = allSprites.get(s);
        if (result == null) {
            result = loadImage(
                    this.getClass().getResource(s + ".png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));

        }
        allSprites.put(s, result);
        return result;
        /*
         * try {
         * result = loadImage(URLDecoder.decode(this.getClass().getResource(s
         * +".png").getPath(), StandardCharsets.UTF_8.name()));
         * catch (UnsupportedEncodingException e) {
         * throw new RuntimeException(e);
         * }
         */
    }

    /**
     * Returns the game board.
     * 
     * @return A 2D array of {@link Tile} representing the game board.
     */
    public Tile[][] getBoard() {
        return this.board;
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player
     * and map elements.
     */
    @Override
    public void setup() {

        frameRate(FPS);
        // See PApplet javadoc:
        levels = new ArrayList<>();
        Config.loadLevelsFromConfig(this, configPath);

        currentLevel = levels.get(0); // need to remove this hard code - mayeb can set the attribute CURRENT LEVEL

        CURRENT_LEVEL = 0;
        startLevel(CURRENT_LEVEL);

        String[] sprites = { "entrypoint", "tile" }; // idk why spritesheet is for ??
        for (int i = 0; i < sprites.length; i++) {
            getSprite(sprites[i]);
        }

        String[] SpriteFirstWords = { "ball", "hole", "wall" };
        for (int i = 0; i < SpriteFirstWords.length; i++) {
            for (int k = 0; k < 5; k++) {
                getSprite(SpriteFirstWords[i] + String.valueOf(k));
            }
        }
        setBallColoursMap();
        declarelevelFileSymbolSprites();

    }

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKey() == 'r') {
            if (!gameOver) {
                restartLevel();
            } else {
                restartGame();
            }
        } else if (event.getKey() == ' ') {
            gamePaused = !gamePaused; // Toggle the pause state

            if (gamePaused) {
                // Record the frame when the game was paused
                pauseStartFrame = frameCount;
            } else {
                // Adjust levelStartFrame by the duration of the pause
                int pauseDuration = frameCount - pauseStartFrame;
                levelStartFrame += pauseDuration;
            }
        }

    }

    /**
     * Receive key released signal from the keyboard.
     */
    @Override
    public void keyReleased() {

    }

    @Override
    public void mousePressed(MouseEvent e) {

        // create a new player-drawn line object

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // add line segments to player-drawn line object if left mouse button is held
        if (gameOver) {
            return;
        }
        if (mouseButton == LEFT) {
            if (currentLine == null) {
                currentLine = new Line(mouseX, mouseY);
            } else if (frameCount % 3 == 0) {
                currentLine.addPoints(mouseX, mouseY);
            }

        }

        else if (mouseButton == RIGHT) {
            Iterator<Line> iterator = drawnLines.iterator();
            while (iterator.hasNext()) {
                Line l = iterator.next();
                if (l.isMouseNearLine(mouseX, mouseY)) {
                    iterator.remove(); // Safely remove the line from the list
                    break; // Assuming you only want to remove one line at a time

                }
            }

            // remove player-drawn line object if right mouse button is held
        }

        // remove player-drawn line object if right mouse button is held
        // and mouse position collides with the line
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (gameOver) {
            return;
        }
        if (currentLine != null) {
            drawnLines.add(currentLine);
            currentLine = null;
        }
    }

    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {
        background(210);

        fill(0, 0, 0);
        strokeWeight(0);
        this.rect(9, 13, 154, 39);

        if (gamePaused) {
            textSize(23);
            fill(0, 0, 0);
            text("*** PAUSED ***", 202, 40);
        }

        for (int i = 0; i < board.length; i++) {
            for (int k = 0; k < board[i].length; k++) {
                if (board[i][k] != null) {
                    board[i][k].draw(this);
                }
            }
        }
        for (GameObject obj : gameObjects) {
            drawGameObject(obj);
        }

        for (Line line : drawnLines) {
            line.draw(this);
        }

        for (Ball ball : activeBalls) {
            ball.draw(this);
            ball.move(this);

            for (Wall wall : allWalls) {
                ball.checkCollisionWithWall(wall);
            }

            for (Line line : drawnLines) {
                ball.checkCollisionWithLine(line);
                if (line.shouldBeRemoved()) {
                    linesToRemove.add(line);
                }
            }
            for (Hole hole : allHoles) {
                ball.holeAttractionCheck(this, hole);
            }

            if (ball.shouldBeRemoved()) {
                ballsToRemove.add(ball);
                ball.shouldBeRemoved = false;
            }

        }
        activeBalls.removeAll(ballsToRemove);
        ballsToRemove.clear();
        drawnLines.removeAll(linesToRemove);
        linesToRemove.clear();

        for (Ball ball : displayedBalls) {
            ball.draw(this);
        }
        // fill(0, 0, 0);
        // strokeWeight(0);
        // this.rect(10, 10, 145, 45);

        handleBallSpawning();

        drawBallQueue();
        this.time = displayCountdown();
        int countdown = displayCountdown();

        textSize(23);
        fill(0, 0, 0);
        text("Time: " + countdown, 440, 55);
        text("Score: " + score, 445, 30);
        if (!ballSpawnQueue.isEmpty()) {
            float spawnTimerInSeconds = spawnTimer / (float) FPS;
            text(String.format("%.1f", spawnTimerInSeconds), 170, 40);
        }

        if (gameOver) {
            text("=== TIME'S UP ===", 200, 20);
            for (Ball ball : activeBalls) {
                ball.setSpawned(false);

            }

        }

        if (countdown <= 0 || (activeBalls.isEmpty() && ballSpawnQueue.isEmpty())) {
            // do yellow tiles
            if (frameCount % 2 == 0) {
                if (!scoreAdditionFinished) {
                    addRemainingTimeToScore();
                }
                startYellowTileAnimation();
            }
            if (yellowTileAnimationComplete && scoreAdditionFinished) {
                levelComplete();
                yellowTileAnimationComplete = false;
            }
        }
    }

    /**
     * Retrieves the level file as a 2D array of symbols representing
     * different game elements (balls, walls, holes).
     * 
     * @return A 2D array of level symbols.
     */
    public String[][] GetLevelFileSymbol() {
        String[][] levelFileArray = new String[18][18];
        String currentLevelFile = currentLevel.getLayout();
        int row = 0;

        try {
            File file = new File(currentLevelFile);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine() && row < 18) {
                String line = scanner.nextLine();
                for (int i = 0; i < line.length() && i < 18; i++) {
                    char currentChar = line.charAt(i);
                    if (currentChar == 'H' || currentChar == 'B') {
                        levelFileArray[row][i] = "" + line.charAt(i) + line.charAt(i + 1);
                        levelFileArray[row][i + 1] = " ";
                        i++;
                    } else {
                        levelFileArray[row][i] = String.valueOf(currentChar);
                    }

                }
                row++;
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return levelFileArray;
    }

    /**
     * Parses the level file and initializes the game board with its elements.
     */
    public void parseLevelFile() {
        int numRows = levelFileArray.length;
        int numCols = levelFileArray[0].length;
        board = new Tile[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                String symbol = levelFileArray[row][col];
                if (symbol == null) { // been marked as occupied in markOccupiedTiles
                    continue;
                }
                if (symbol.startsWith("H")) {
                    // could add a function that does this since it both if statements are doing the
                    // same thing?
                    int holeType = Integer.parseInt(symbol.substring(1));
                    Hole hole = new Hole("Hole" + holeType, col, row, 2, 2);
                    gameObjects.add(hole);
                    allHoles.add(hole);

                    markOccupiedTiles(levelFileArray, row, col, 2, 2);
                } else if (symbol.startsWith("S")) {
                    Spawner spawner = new Spawner("Spawner", col, row, 1, 1);
                    gameObjects.add(spawner);
                    allSpawners.add(spawner);
                } else if (symbol.startsWith("B")) {
                    Ball ball = new Ball(symbol, col, row, ballColours.get(symbol.trim()), 24, 24);
                    ball.setSpawned(true);
                    this.activeBalls.add(ball);
                    board[row][col] = new Tile(col, row); // even though a ball is supposed to start there, we still
                                                          // want a tile to be initialised because the ball is going to
                                                          // move away
                    board[row][col].levelFileSymbol = " "; // setting the row as a space so a
                    // tile is drawn
                } else if (symbol.matches("[X1234]")) {
                    board[row][col] = new Wall(col, row, symbol);
                    this.allWalls.add(new Wall(col, row, symbol));
                    board[row][col].levelFileSymbol = symbol;
                } else {
                    board[row][col] = new Tile(col, row);
                    board[row][col].levelFileSymbol = symbol;
                }
            }
        }
    }

    /**
     * Marks tiles as occupied in the level file.
     * 
     * @param levelFileArray The 2D array representing the level layout.
     * @param startRow       The starting row of the area to mark.
     * @param startCol       The starting column of the area to mark.
     * @param width          The width of the area.
     * @param height         The height of the area.
     */
    public void markOccupiedTiles(String[][] levelFileArray, int startRow, int startCol, int width, int height) {
        for (int y = startRow; y < startRow + height; y++) { // not fully convinced about the values here??
            for (int x = startCol; x < startCol + width; x++) {
                if (y < levelFileArray.length && x < levelFileArray[y].length) {
                    levelFileArray[y][x] = null; // Mark as occupied
                }
            }
        }
    }

    /**
     * Declares the sprites corresponding to different symbols in the level file.
     */
    public static void declarelevelFileSymbolSprites() {
        String[] wallSymbols = { "X", "1", "2", "3", "4" };
        // walls
        for (int i = 0; i < wallSymbols.length; i++) {
            levelFileSymbolSprites.put(wallSymbols[i], allSprites.get("wall" + String.valueOf(i)));
        }
        // holes
        for (int i = 0; i <= 4; i++) {
            levelFileSymbolSprites.put("H" + i, allSprites.get("hole" + String.valueOf(i)));
        }
        // balls
        for (int i = 0; i <= 4; i++) {
            levelFileSymbolSprites.put("B" + i, allSprites.get("ball" + String.valueOf(i)));
        }
        // spawner
        levelFileSymbolSprites.put("S", allSprites.get("entrypoint"));

    }

    /**
     * Draws a {@link GameObject} on the game board.
     * 
     * @param obj The {@link GameObject} to draw.
     */
    private void drawGameObject(GameObject obj) {
        PImage sprite = null;

        if (obj.getType().startsWith("Hole")) {
            String holeType = obj.getType().substring(4);
            sprite = levelFileSymbolSprites.get("H" + holeType);
        } else if (obj.getType().equals("Spawner")) {
            sprite = levelFileSymbolSprites.get("S");
        }
        if (sprite != null) {
            int x = obj.getX();
            int y = obj.getY();
            // int widthPixels = obj.getWidth() * CELLSIZE;
            // int heightPixels = obj.getHeight() * CELLHEIGHT;
            image(sprite, x * CELLSIZE, y * CELLSIZE + TOPBAR);
        }
    }

    /**
     * Initializes the mapping of ball colors to their corresponding symbols.
     */
    public void setBallColoursMap() {
        ballColours.put("B0", "grey");
        ballColours.put("B1", "orange");
        ballColours.put("B2", "blue");
        ballColours.put("B3", "green");
        ballColours.put("B4", "yellow");
    }

    /**
     * Displays the countdown timer for the current level.
     * 
     * @return The remaining time in seconds for the current level.
     */
    public int displayCountdown() {
        if (gamePaused) {
            int totalLevelTime = currentLevel.getTime();
            int elapsedFrames = pauseStartFrame - levelStartFrame;
            int remainingFrames = totalLevelTime * FPS - elapsedFrames;
            int countdown = remainingFrames / FPS;
            return countdown;
        }

        int totalLevelTime = currentLevel.getTime(); // Total time for the level in seconds
        int elapsedFrames = frameCount - levelStartFrame; // Frames since level started
        int remainingFrames = totalLevelTime * FPS - elapsedFrames;
        int countdown = remainingFrames / FPS; // Convert frames back to seconds

        if (countdown < 0) {
            countdown = 0;
        }

        return countdown;
    }

    /**
     * Starts the specified level by index.
     * 
     * @param levelIndex The index of the level to start.
     */
    public void startLevel(int levelIndex) {
        currentLevel = levels.get(levelIndex);
        CURRENT_LEVEL = levelIndex;

        gameObjects.clear();
        activeBalls.clear();
        allWalls.clear();
        allHoles.clear();
        allSpawners.clear();
        drawnLines.clear();

        levelFileArray = GetLevelFileSymbol();
        parseLevelFile();

        ballSpawnQueue.clear();
        spawnTimer = currentLevel.getSpawnInterval() * FPS; // Initialize the timer based on the FPS

        // Load balls to the queue
        ballSpawnQueue.addAll(getJsonBalls(configPath)); // Assuming getJsonBalls gets the balls for the level
        updateDisplayedBalls(); // To initially display the first 5 balls in the queue

        // set the level start frame
        levelStartFrame = frameCount;

        loop();

    }

    /**
     * Marks the current level as complete and advances to the next one.
     */
    public void levelComplete() {
        CURRENT_LEVEL++;
        scoreAtStartOfLevel = score;
        if (CURRENT_LEVEL < levels.size()) {
            startLevel(CURRENT_LEVEL);
        } else {
            // No more levels; the game is over
            gameOver = true;
            // noLoop(); // Stop the draw loop

        }
    }

    /**
     * Restarts the current level, resetting its elements and state.
     */
    public void restartLevel() {
        score = scoreAtStartOfLevel;
        startLevel(CURRENT_LEVEL);

    }

    /**
     * Restarts the game, resetting all levels and game state.
     */
    public void restartGame() {
        gameOver = false;
        CURRENT_LEVEL = 0;
        startLevel(CURRENT_LEVEL);
    }

    /**
     * Adds a {@link Ball} to the spawn queue.
     * 
     * @param ball The {@link Ball} to add to the spawn queue.
     */
    public void addBallToSpawnQueue(Ball ball) {
        ballSpawnQueue.add(ball);

    }

    /**
     * Retrieves the balls defined in the level's configuration file.
     * 
     * @param configPath The path to the configuration file.
     * @return A list of {@link Ball} instances.
     */
    public ArrayList<Ball> getJsonBalls(String configPath) {
        ArrayList<Ball> bArray = new ArrayList<>();
        for (String ball : this.levels.get(CURRENT_LEVEL).getBalls()) {
            switch (ball) {
                case "green":
                    bArray.add(new Ball("B3", 0, 0, "green", 24, 24));
                    break;
                case "blue":
                    bArray.add(new Ball("B2", 0, 0, "blue", 24, 24));
                    break;
                case "yellow":
                    bArray.add(new Ball("B4", 0, 0, "yellow", 24, 24));
                    break;
                case "orange":
                    bArray.add(new Ball("B1", 0, 0, "orange", 24, 24));
                    break;
                case "grey":
                    bArray.add(new Ball("B0", 0, 0, "grey", 24, 24));
                    break;
                default:
                    break;
            }

        }
        return bArray;

    }

    /**
     * Handles ball spawning based on the spawn interval timer specified in the
     * config file and adds them to the game when the timer expires.
     */
    public void handleBallSpawning() {
        if (gamePaused) {
            return; // Do not update spawnTimer or spawn balls when game is paused
        }
        if (spawnTimer > 0) {
            spawnTimer--; // Decrement the spawn timer
        } else {
            if (!ballSpawnQueue.isEmpty()) {
                Ball nextBall = ballSpawnQueue.poll();
                respawnBallAtRandomSpawner(nextBall);
            }

            shiftDisplayedBalls();

            // Reset the spawn timer
            spawnTimer = currentLevel.getSpawnInterval() * FPS;
        }
    }

    /**
     * Draws the queue of balls waiting to be spawned.
     */
    public void drawBallQueue() {
        int xOffset = 10; // Starting x position for the ball queue display
        int yOffset = 20; // y position for the ball queue display

        // Loop through the displayed balls and draw them
        for (int i = 0; i < displayedBalls.size(); i++) {
            Ball ball = displayedBalls.get(i);
            ball.setPosition(xOffset + i * CELLSIZE, yOffset); // draw at an offset position
            ball.setSpawned(false);
        }
    }

    /**
     * Shifts the displayed balls to the left to simulate ball movement in the
     * queue.
     */
    public void shiftDisplayedBalls() {

        for (Ball ball : displayedBalls) {
            ball.shiftLeftBy(1);
        }
        updateDisplayedBalls();
    }

    /**
     * Gets a random {@link Spawner} from the available spawners.
     * 
     * @return A random {@link Spawner} instance.
     */
    public Spawner getRandomSpawner() {
        int index = random.nextInt(allSpawners.size());
        return allSpawners.get(index);

    }

    /**
     * Updates the list of displayed balls in the spawn queue.
     */
    public void updateDisplayedBalls() {
        displayedBalls.clear();
        int count = 0;
        for (Ball ball : ballSpawnQueue) {
            displayedBalls.add(ball);
            count++;
            if (count >= 5) {
                break; // Only show the next 5 balls
            }
        }
    }

    /**
     * Starts the yellow tile animation at the end of the level.
     */
    public void startYellowTileAnimation() {
        if (gamePaused) {
            return;
        }

        Tile[] topRow = board[0];
        Tile[] bottomRow = board[17];
        Tile t = topRow[yellowTileCount];
        t.showYellowTile = true;
        Tile b = bottomRow[17 - yellowTileCount];
        b.showYellowTile = true;
        if (yellowTileCount == 17) {
            yellowTileAnimationComplete = true;
            return;
        }
        yellowTileCount++;

    }

    /**
     * Adds the remaining time to the player's score after the level is complete.
     */
    public void addRemainingTimeToScore() {
        score += time;
        scoreAdditionFinished = true;

    }

    /**
     * Respawns a ball at a random spawner on the board.
     * 
     * @param ball The {@link Ball} to respawn.
     */
    public void respawnBallAtRandomSpawner(Ball ball) {
        Spawner spawner = getRandomSpawner();
        ball.setPosition(spawner.getX() * App.CELLSIZE + App.CELLSIZE / 4,
                spawner.getY() * App.CELLSIZE + App.TOPBAR + App.CELLSIZE / 4);
        ball.resetVelocity(); // Reset the velocity
        ball.setSpawned(true); // Mark the ball as spawned again
        activeBalls.add(ball); // Add the ball to the active balls list
        System.out.print("active balls : ");

    }

    /**
     * The main entry point for starting the game.
     * 
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}
