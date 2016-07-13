package discpative.model;


import discpative.controller.Axis;
import discpative.controller.Direction;
import discpative.controller.Rotation;
import discpative.tools.Tools;

abstract class Movable {
    private int row, col;
    private Level level;
    private Direction icyDirection;

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
        int targetRow = row + Tools.dir2row(direction);
        int targetCol = col + Tools.dir2col(direction);

        Tile targetTile = level.getTileAt(targetRow, targetCol);

        if (targetTile.isCurvedIcyTile()) {
            moveTo(targetRow, targetCol, direction);
        }
        moveTo(targetRow, targetCol);
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

        if (destinationTile.isWall())
            return true;
        if (destinationTile.isPitfall() && !((Pitfall) destinationTile).isFilled())
            return true;
        if (destinationTile.isCurvedIcyTile() && destinationTileContent != null) {
            CurvedIcyTile curvedIcyTile = ((CurvedIcyTile) destinationTile);
            Direction icyDirectionOne = curvedIcyTile.getDirectionOne();
            Direction icyDirectionTwo = curvedIcyTile.getDirectionTwo();
            if(direction == icyDirectionOne || direction == icyDirectionTwo)
                return true;
            return false;
        }
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

    /**
     * Moves player to given coordinates
     * @param destinationRow The destinationRow of the players destination
     * @param destinationCol The destinationColumn of the players destination
     */
    private void moveTo(int destinationRow, int destinationCol, Direction direction) {
        Tile originTile = level.getTileAt(this.row, this.col);
        CurvedIcyTile destinationTile = (CurvedIcyTile) level.getTileAt(destinationRow, destinationCol);

        this.row = destinationRow;
        this.col = destinationCol;

        originTile.steppedOnBy(null);
        destinationTile.steppedOnBy(this, direction);
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

    public boolean isCrate() {
        return false;
    }

    public boolean isGuard() {
        return false;
    }

    public boolean isPlayer() {
        return false;
    }
}

abstract class Character extends Movable {
    private Direction direction;
    private Axis axis;

    public Character(int row, int col, Direction direction, Level level) {
        super(row, col, level);
        this.direction = direction;
        setAxis(direction);
    }


    /**
     * Checks if the character has collision in given direction.
     * Gets the blocking moveable out of the way if possible.
     * @param direction the direction that should be checked for collision.
     * @return
     */
    @Override
    boolean checkCollision(Direction direction) {
        Tile destinationTile = getLevel().getTileAt(getRow() + Tools.dir2row(direction)
                , getCol() + Tools.dir2col(direction));
        Movable destinationTileContent = destinationTile.contains();

        //In case of a crate check if you can move the crate
        if (destinationTileContent != null && destinationTileContent.isCrate())
            return destinationTileContent.checkCollision(direction);

        /*
         * In case of a guard, check if the guard is moving on the same Axis.
         * If he is, only check the direction you are trying to move for collision
         * Else check collision in both directions for the guard
         */
        if (destinationTileContent != null && destinationTileContent.isGuard()) {
            Guard destinationGuard = (Guard) destinationTileContent;
            Axis destinationGuardAxis = destinationGuard.getAxis();
            if (getAxis() == destinationGuardAxis) {
                boolean destinationHasCollision = destinationGuard.checkCollision(direction);
                if (destinationHasCollision)
                    return true;
                destinationGuard.move(direction);
                return false;
            } else {
                Direction destinationGuardDirection = destinationGuard.getDirection();
                Direction destinationGuardOppositeDirection
                        = Tools.getOppositeDirection(destinationGuard.getDirection());
                boolean destinationHasCollision;
                destinationHasCollision = destinationGuard.checkCollision(destinationGuardDirection)
                        || destinationGuard.checkCollision(destinationGuardOppositeDirection);
                if (destinationHasCollision)
                    return true;
                destinationGuard.move(destinationGuardDirection);
                return false;
            }
        }
        return super.checkCollision(direction);
    }

    @Override
    void move(Direction direction) {
        Tile destinationTile = tileAt(direction);
        if (destinationTile.contains() != null
                && destinationTile.contains().isCrate()){
            destinationTile.contains().move(direction);
        }
        super.move(direction);
        this.direction = direction;
        setAxis(direction);
    }

    public Direction getDirection() {
        return direction;
    }

    protected void rotate(Direction direction) {
        this.direction = direction;
        setAxis(direction);
    }

    public void setAxis(Direction direction) {
        switch (direction) {
            case RIGHT: case LEFT:
                axis = Axis.HORIZONTAL;
                break;
            default:
                axis = Axis.VERTICAL;
                break;
        }
    }

    Axis getAxis() {
        return axis;
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

    public Player clone() {
        return new Player(getRow(), getCol(), getLevel());
    }

    @Override
    public boolean isPlayer() {
        return true;
    }
}

class Guard extends Character {
    private boolean moved;

    public Guard(int row, int col, Direction direction, Level level) {
        super(row, col, direction, level);
    }

    @Override
    boolean checkCollision(Direction direction) {
        Tile destinationTile = getLevel().getTileAt(
                getRow() + Tools.dir2row(direction),
                getCol() + Tools.dir2col(direction));
        Movable destinationTileContent = destinationTile.contains();

        if (destinationTileContent != null && destinationTileContent.isPlayer())
            getLevel().lose();
        return super.checkCollision(direction);
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

    private boolean canSeePlayer() {
        int vertical   = Tools.dir2row(getDirection());
        int horizontal = Tools.dir2col(getDirection());

        int checkRow;
        int checkCol;
        while (true) {
            checkRow = getRow() + vertical;
            checkCol = getCol() + horizontal;
            Tile checkTile = getLevel().getTileAt(checkRow, checkCol);
            Movable checkTileContent = checkTile.contains();
            if (checkTile.isWall())
                return false;
            if (checkTileContent != null) {
                if (checkTileContent.isPlayer())
                    return true;
                return false;
            }
            if (checkTile.isCurvedIcyTile())
                return false;
        }
    }

    public void turn() {
        Direction oppositeDirection = Tools.getOppositeDirection(getDirection());
        if (moved)
            return;
        if (!checkCollision(oppositeDirection))
            move(oppositeDirection);
    }

    public void move() {
        if (moved)
            return;
        if (!checkCollision(getDirection()))
            move(getDirection());
        else
            turn();
    }

    @Override
    void move(Direction direction) {
        super.move(direction);
        setAxis(direction);
        moved = true;
    }

    public void resetMoved() {
        moved = false;
    }

    public Guard clone() {
        return new Guard(getRow(), getCol(), getDirection(), getLevel());
    }

    @Override
    public boolean isGuard() {
        return true;
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

        Tile targetTile = super.getLevel().getTileAt(targetRow, targetCol);
        Movable targetTileContent = targetTile.contains();

        if(targetTile.isPitfall() && targetTileContent == null)
            return false;
        return super.checkCollision(direction);
    }

    @Override
    public boolean isCrate() {
        return true;
    }

    public Crate clone() {
        return new Crate(getRow(), getCol(), getLevel());
    }
}
