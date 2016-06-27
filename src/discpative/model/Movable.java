package discpative.model;


import discpative.controller.Direction;
import discpative.controller.Rotation;
import discpative.tools.Tools;

abstract class Movable {
    private int row, col;
    private Level level;

    public Movable(int row, int col, Level level) {
        this.row = row;
        this.col = col;
        this.level = level;
    }

    /**
     * Moves the player 1 field in the given Direction.
     * Does check for collision first.
     * Does not move at all if Collision is detected.
     * @param direction The direction the Player should Move
     */
    void move(Direction direction){
        moveTo(row + Tools.dir2row(direction), col + Tools.dir2col(direction));
    }

    /**
     * Checks for collision in the given direction.
     * @param direction The direction that should be checked for collision.
     * @return False if no collision detected - True if Collision detected
     */
    boolean checkCollision(Direction direction) {
        Tile destinationTile = getLevel().getTileAt(
                getRow() + Tools.dir2row(direction),
                getCol() + Tools.dir2col(direction));
        Movable destinationTileContent = destinationTile.contains();

        if (destinationTile.getClass() == Wall.class)
            return true;
        if (destinationTile.getClass() == Pitfall.class
                && !((Pitfall) destinationTile).isFilled())
            return true;
        if (destinationTileContent != null)
            return true;
        return false;
    }

    /**
     * Moves player to given coordinates
     * @param destinationRow The destinationRow of the players destination
     * @param destinationCol The destinationColumn of the players destination
     */
    private void moveTo(int destinationRow, int destinationCol) {
        Tile originTile = level.getTileAt(this.row, this.col);
        Tile destinationTile = level.getTileAt(destinationRow, destinationCol);

        this.row = destinationRow;
        this.col = destinationCol;

        originTile.steppedOnBy(null);
        destinationTile.steppedOnBy(this);
    }

    int getRow() {
        return row;
    }

    int getCol() {
        return col;
    }

    protected Level getLevel(){
        return this.level;
    }

    protected Tile tileAt(Direction direction) {
        return level.getTileAt(Tools.dir2row(direction) + row,
                Tools.dir2col(direction) + col);
    }
}

abstract class Character extends Movable {
    private Direction direction;

    public Character(int row, int col, Direction direction, Level level) {
        super(row, col, level);
        this.direction = direction;
    }

    @Override
    boolean checkCollision(Direction direction) {
        Tile destinationTile = getLevel().getTileAt(
                getRow() + Tools.dir2row(direction),
                getCol() + Tools.dir2col(direction));
        Movable destinationTileContent = destinationTile.contains();

        if (destinationTileContent != null
                && destinationTileContent.getClass() == Crate.class)
            return destinationTileContent.checkCollision(direction);
        return super.checkCollision(direction);
    }

    @Override
    void move(Direction direction) {
        Tile destinationTile = tileAt(direction);
        if (destinationTile.contains() != null
                && destinationTile.contains().getClass() == Crate.class){
            destinationTile.contains().move(direction);
        }
        super.move(direction);
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    protected void rotate(Direction direction) {
        this.direction = direction;
    }
}

class Player extends Character {

    public Player(int row, int col, Level level) {
        super(row, col, Direction.UP, level);
    }

    @Override
    void move(Direction direction) {
        super.move(direction);
        getLevel().setPlayerPos(getRow(), getCol());
    }
}

class Guard extends Character {

    public Guard(int row, int col, Direction direction, Level level) {
        super(row, col, direction, level);
    }

    void rotateTo(Rotation rotation) {
        switch (super.getDirection()) {
            case DOWN:
                if (rotation == Rotation.RIGHT) {
                    super.rotate(Direction.LEFT);
                    break;
                }
                super.rotate(Direction.RIGHT);
                break;
            case LEFT:
                if (rotation == Rotation.RIGHT) {
                    super.rotate(Direction.UP);
                    break;
                }
                super.rotate(Direction.DOWN);
                break;
            case RIGHT:
                if (rotation == Rotation.RIGHT) {
                    super.rotate(Direction.DOWN);
                    break;
                }
                super.rotate(Direction.UP);
                break;
            case UP:
                if (rotation == Rotation.RIGHT) {
                    super.rotate(Direction.RIGHT);
                    break;
                }
                super.rotate(Direction.LEFT);
                break;
        }
    }

}

class Crate extends Movable {
    public Crate(int row, int col, Level level) {
        super(row, col, level);
    }

    @Override
    boolean checkCollision(Direction direction) {
        int targetRow = Tools.dir2row(direction) + getRow();
        int targetCol = Tools.dir2col(direction) + getCol();

        //TODO specific collision

        Tile targetTile = super.getLevel().getTileAt(targetRow, targetCol);
        return targetTile.contains() != null;
    }
}
