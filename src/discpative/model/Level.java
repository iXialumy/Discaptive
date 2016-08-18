package discpative.model;

import discpative.controller.Direction;
import discpative.controller.Rotation;
import discpative.io.In;
import discpative.io.Out;
import discpative.view.ViewInterface;

import java.util.ArrayList;

public class Level implements LevelInterface{
    private int rowCount = 0;
    private int colCount = 0;
    private int movesCount = 0;
    private int playerRow;
    private int playerCol;
    private Tile[][] grid;
    private ArrayList<Guard> guards;
    private ArrayList<Crate> crates;
    private ArrayList<ViewInterface> views;

    public Level(int levelNumber) {
        views = new ArrayList<>();
        crates = new ArrayList<>();
        array2Level(loadLevel(levelNumber));
    }

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

    public void moveGuards() {
        guards.forEach(Guard::move);
        guards.forEach(Guard::resetMoved);
    }

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
                    default:
                        grid[row][col] = new Wall();
                        break;
                }
            }
        }
    }

    public boolean canPlayerMoveTo(Direction direction){
        Player player = (Player) getTileAt(playerRow, playerCol).contains();
        return !player.checkCollision(direction);
    }

    public void movePlayerTo(Direction direction) {
        moveMovable(playerRow, playerCol, direction);
        moveGuards();
    }

    public void moveMovable(int row, int col, Direction direction) {
        Movable movable = grid[row][col].contains();
        movable.move(direction);
        if(movable.isPlayer())
            movesCount++;
    }

    void setPlayerPos(int row, int col) {
        this.playerRow = row;
        this.playerCol = col;
    }

    public void setTileAt(int col, int row, Tile tile) {
        this.grid[row][col] = tile;
    }

    public void lose() {
        Out.println("You lost.");
    }

    private void updateTile(int row, int col) {
        for (ViewInterface view : views)
            view.updateTile(row, col);
    }
    public void updateMoveablePresence(int row, int col, Movable movable) {
        if (movable.isPlayer()) {
            updatePlayerPresence(row, col);
        } else if(movable.isGuard()) {
            updateGuardsPresence(row, col);
        } else if(movable.isCrate()) {
            updateCratePresence(row, col);
        }
    }
    private void updatePlayerPresence(int row, int col) {
        for (ViewInterface view : views)
            view.updatePlayerPresence(row, col);
    }
    private void updateGuardsPresence(int row, int col) {
        for (ViewInterface view : views)
            view.updateGuardPresence(row, col);
    }
    private void updateCratePresence(int row, int col) {
        for (ViewInterface view : views)
            view.updateCratePresence(row, col);
    }
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

    public void winGame() {
    }
}
