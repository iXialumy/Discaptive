package discpative.model;

import discpative.controller.Direction;
import discpative.controller.Rotation;
import discpative.io.In;
import discpative.io.Out;
import discpative.tools.Tools;
import discpative.view.ViewInterface;

import java.util.ArrayList;

/**
 * The model of the level.
 *
 * @author jpaus
 * @version final
 */
public class Level implements LevelInterface{
    private int rowCount = 0; //total number of rows in the level
    private int colCount = 0; //total number of columns in the level
    private int movesCount = 0; //number of moves done by the player
    private int playerRow; //row coordinate of the player
    private int playerCol; //column coordinate of the player
    private Tile[][] grid; //2-dimensional array of all the tile objects
    private ArrayList<Guard> guards; //array of all guards
    private ArrayList<Crate> crates; //array of all crates
    private ArrayList<ViewInterface> views; //array of views
    private boolean lost; //is the level lost
    private boolean won; //is the level lost

    /**
     * Creates a new object of the type Level
     * Loads the level
     * @param levelNumber the number of the level that should be loaded
     */
    public Level(int levelNumber) {
        views = new ArrayList<>();
        crates = new ArrayList<>();
        array2Level(loadLevel(levelNumber));
        lost = false;
        won = false;
    }

    /**
     * Reads the level file into an array of chars
     * @param levelNumber the number of the level that should be loaded
     * @return the level file as array of characters
     */
    public char[][] loadLevel(int levelNumber) {
        ArrayList<String> lineArray = new ArrayList<>();
        In.open("resources/levels/Level" + levelNumber + ".txt");

        String line = In.readLine();
        for (int i = 0; In.done(); i++) {
            lineArray.add(i, line);
            int lineLen = line.length();
            if (lineLen > colCount)
                colCount = lineLen;
            line = In.readLine();
        }
        rowCount = lineArray.size();

        char[][] level = new char[rowCount][colCount];
        for (int i = 0; i < lineArray.size(); i++) {
            for (int j = 0; j < colCount; j++) {
                try {
                    level[i][j] = lineArray.get(i).charAt(j);
                } catch (IndexOutOfBoundsException e) {
                    level[i][j] = '#';
                }
            }
        }
        return level;
    }

    /**
     * Converts an array of chars to an actual Level and writes it into the object
     * @param level the number of the level that should be loaded
     */
    private void array2Level(char[][] level) {
        grid = new Tile[rowCount][colCount];
        guards = new ArrayList<>();
        for (int row = 0; row < level.length; row++) {
            for (int col = 0; col < level[0].length; col++) {
                switch (level[row][col]) {
                    case ' ':
                        grid[row][col] = new EmptyPassage();
                        break;
                    case '#':
                        grid[row][col] = new Wall();
                        break;
                    case '$': {
                        Crate crate = new Crate(row, col, this);
                        grid[row][col] = new EmptyPassage(crate);
                        crates.add(crate);
                        break;
                    }
                    case '@':
                        grid[row][col] = new EmptyPassage(new
                                Player(row, col, this));
                        playerRow = row;
                        playerCol = col;
                        break;
                    case '.':
                        grid[row][col] = new Objective();
                        break;
                    case '*': {
                        Crate crate = new Crate(row, col, this);
                        grid[row][col] = new Objective(crate);
                        crates.add(crate);
                        break;
                    }
                    case '!':
                        grid[row][col] = new Pitfall();
                        break;
                    case 'N': {
                        Guard guard = new Guard(row, col, Direction.UP, this);
                        grid[row][col] = new EmptyPassage(guard);
                        guards.add(guard);
                        break;
                    }
                    case 'W': {
                        Guard guard = new Guard(row, col, Direction.LEFT, this);
                        grid[row][col] = new EmptyPassage(guard);
                        guards.add(guard);
                        break;
                    }
                    case 'S': {
                        Guard guard = new Guard(row, col, Direction.DOWN, this);
                        grid[row][col] = new EmptyPassage(guard);
                        guards.add(guard);
                        break;
                    }
                    case 'O': {
                        Guard guard = new Guard(row, col, Direction.RIGHT, this);
                        grid[row][col] = new EmptyPassage(guard);
                        guards.add(guard);
                        break;
                    }
                    case 'L':
                        grid[row][col] = new RotationPassage(Rotation.LEFT);
                        break;
                    case 'R':
                        grid[row][col] = new RotationPassage(Rotation.LEFT);
                        break;
                    case '=':
                        grid[row][col] = new IcyTile();
                        break;
                    case 'n':
                        grid[row][col] = new CurvedIcyTile(Direction.UP, Direction.RIGHT, Direction.UP);
                        break;
                    case 'o':
                        grid[row][col] = new CurvedIcyTile(Direction.DOWN, Direction.RIGHT, Direction.RIGHT);
                        break;
                    case 's':
                        grid[row][col] = new CurvedIcyTile(Direction.DOWN, Direction.LEFT, Direction.DOWN);
                        break;
                    case 'w':
                        grid[row][col] = new CurvedIcyTile(Direction.UP, Direction.LEFT, Direction.LEFT);
                        break;
                    default:
                        grid[row][col] = new Wall();
                        break;
                }
            }
        }
    }

    /**
     * Moves all guards that can move at not given order
     */
    public void moveGuards() {
        guards.forEach(Guard::move);
        guards.forEach(Guard::resetMoved);
    }

    /**
     * Checks if the player is Visible to at least one of the guards.
     * Ends the game if so
     */
    public void isPlayerVisible() {
        guards.forEach(Guard::canSeePlayer);
    }

    /**
     * Checks if the player can move into given direction
     * @param direction direction the player tries to move to
     * @return true if player can move to 1 field in given direction directly, otherwise false
     */
    public boolean canPlayerMoveTo(Direction direction){
        Player player = (Player) getTileAt(playerRow, playerCol).contains();
        return !player.checkCollision(direction);
    }

    /**
     * Checks if the player can move to field.
     * Only works if player takes exactly 1 step.
     * @param row the destination row
     * @param col the destiantion column
     * @return true if player can move to coordinates directly, otherwise false
     */
    public boolean canplayerMoveTo(int row, int col) {
        Direction dir = Tools.delta2dir(row - playerRow,col - playerCol);
        if(dir != null)
            return canPlayerMoveTo(dir);
        return false;
    }

    /**
     * Moves player 1 field in given direction
     * Does not check for collision
     * Moves guards afterwards and checks if player is visible
     * @param direction direction the player tries to move to
     */
    public void movePlayerTo(Direction direction) {
        if(lost || won)
            return;
        Movable player = grid[playerRow][playerCol].contains();
        player.move(direction);
        movesCount++;
        moveGuards();
        isPlayerVisible();
    }

    /**
     * Moves player to given coordinates if 1 field away at max
     * Uses movePlayerTo(Direction)
     * @param row destination row
     * @param col destination column
     */
    public void movePlayerTo(int row, int col) {
        Direction dir = Tools.delta2dir(row - playerRow, col - playerCol);
        if(dir != null)
            movePlayerTo(dir);
    }

    /**
     * Sets the coordinates of the player
     * @param row the new row
     * @param col the new column
     */
    void setPlayerPos(int row, int col) {
        this.playerRow = row;
        this.playerCol = col;
    }

    /**
     * Prints out that the game is lost and interrupts user from further moves
     */
    public void lose() {
        lost = true;
        Out.println("You lost.");
    }

    /**
     * Updates the graphic of a tile
     * @param row the row that tile is in
     * @param col the column that tile is in
     */
    public void updateTile(int row, int col) {
        for (ViewInterface view : views)
            view.updateTile(row, col);
    }

    /**
     * Updates the graphic of a moveable
     * Removes old graphic if movable is not at coordinates
     * Adds new graphic if movable is at coordinates
     * @param row the row that moveable is in
     * @param col the column that moveable is in
     * @param movable the movable itself
     */
    public void updateMoveablePresence(int row, int col, Movable movable) {
        if (movable.isPlayer()) {
            updatePlayerPresence(row, col);
        } else if(movable.isGuard()) {
            updateGuardsPresence(row, col);
        } else if(movable.isCrate()) {
            updateCratePresence(row, col);
        }
    }

    /**
     * Updates the graphic of the player
     * Removes old graphic if player is not at coordinates
     * Adds new graphic if player is at coordinates
     * @param row the row of the player
     * @param col the column of the player
     */
    private void updatePlayerPresence(int row, int col) {
        for (ViewInterface view : views)
            view.updatePlayerPresence(row, col);
    }

    /**
     * Updates the graphic of a guard
     * Removes old graphic if guard is not at coordinates
     * Adds new graphic if guard is at coordinates
     * @param row
     * @param col
     */
    private void updateGuardsPresence(int row, int col) {
        for (ViewInterface view : views)
            view.updateGuardPresence(row, col);
    }

    /**
     * Updates the graphic of a crate
     * Removes old graphic if crate is not at coordinates
     * Adds new graphic if crate is at coordinates
     * @param row
     * @param col
     */
    private void updateCratePresence(int row, int col) {
        for (ViewInterface view : views)
            view.updateCratePresence(row, col);
    }

    /**
     * updates the statusline on all views
     */
    private void updateStatusLine() {
        views.forEach(ViewInterface::updateStatusLine);
    }

    @Override
    public void registerView(ViewInterface view) {
        views.add(view);
    }

    @Override
    public void unregisterView(ViewInterface view) {
        views.remove(view);
    }

    @Override
    public int getRowCount() {
        return this.rowCount;
    }

    @Override
    public int getColCount() {
        return this.colCount;
    }

    @Override
    public Tile getTileAt(int row, int col) {
        return grid[row][col];
    }

    @Override
    public boolean isPlayerAt(int row, int col) {
        return getTileAt(row, col).contains() != null
                && getTileAt(row, col).contains().isPlayer();
    }

    @Override
    public int getMoveCount() {
        return movesCount;
    }

    /**
     * Interrupts further user input upon win.
     */
    public void winGame() {
        won = true;
        Out.println("You won!");
    }

    /**
     * Interrupts further user input upon lose.
     * @param crate the crate to be removed from the array
     */
    public void popcrate(Crate crate) {
        crates.remove(crate);
    }
}
