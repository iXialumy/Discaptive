package discpative.model;


import discpative.controller.Axis;
import discpative.controller.Direction;
import discpative.controller.Rotation;
import discpative.tools.Tools;

/**
 * Basic movable class
 * Conntains what all movables share
 */
public abstract class Movable {
    private int row, col; //coordinates
    private Level level; //model of the level

    /**
     * Constructor
     * @param row row coordinate
     * @param col column coordinate
     * @param level model of the level
     */
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
        Direction targetDirection = direction;
        Tile currentTile = level.getTileAt(getRow(), getCol());
        if (currentTile.isIcyTile() || currentTile.isCurvedIcyTile())
            targetDirection = ((IcyTile) currentTile).getIcyDirection();
        int targetRow = row + Tools.dir2row(targetDirection);
        int targetCol = col + Tools.dir2col(targetDirection);

        Tile targetTile = level.getTileAt(targetRow, targetCol);

        if (targetTile.isIcyTile() || targetTile.isCurvedIcyTile()) {
            moveTo(targetRow, targetCol, targetDirection);
        } else
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
            Direction curveDirectionVertical = curvedIcyTile.getVertical();
            Direction curveDirectionHorizontal = curvedIcyTile.getHorizontal();
            if(direction == curveDirectionVertical || direction == curveDirectionHorizontal)
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
        int originRow = this.row;
        int originCol = this.col;
        Tile originTile = level.getTileAt(row, col);
        Tile destinationTile = level.getTileAt(destinationRow, destinationCol);

        this.row = destinationRow;
        this.col = destinationCol;

        originTile.steppedOnBy(null);
        destinationTile.steppedOnBy(this);

        level.updateMoveablePresence(originRow, originCol, this);
        level.updateMoveablePresence(destinationRow, destinationCol, this);
    }

    /**
     * Moves player to given coordinates
     * @param destinationRow The destinationRow of the players destination
     * @param destinationCol The destinationColumn of the players destination
     */
    private void moveTo(int destinationRow, int destinationCol, Direction direction) {
        int originRow = this.row;
        int originCol = this.col;
        Tile originTile = level.getTileAt(this.row, this.col);
        IcyTile destinationTile = (IcyTile) level.getTileAt(destinationRow, destinationCol);

        this.row = destinationRow;
        this.col = destinationCol;

        originTile.steppedOnBy(null);
        destinationTile.steppedOnBy(this, direction);

        level.updateMoveablePresence(originRow, originCol, this);
        level.updateMoveablePresence(destinationRow, destinationCol, this);
    }

    /**
     * Getter
     * @return row of the movable
     */
    int getRow() {
        return row;
    }

    /**
     * Getter.
     * @return column of the movable
     */
    int getCol() {
        return col;
    }

    /**
     * Getter.
     * @return model of the level
     */
    protected Level getLevel(){
        return this.level;
    }

    /**
     * Getter.
     * @param direction the {@link Direction} to look up
     * @return Tile 1 field in given direction
     */
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

    protected void setRow(int row) {
        this.row = row;
    }

    protected void setCol(int col) {
        this.col = col;
    }
}

/**
 * Refinement of Movable.
 * Contains what Player and Guards share.
 */
abstract class Character extends Movable {
    private Direction direction; //direction the character is looking at
    private Axis axis; //the axis player is looking at

    /**
     * Constructor.
     * @param row initial row
     * @param col initial column
     * @param direction initial direction
     * @param level model of the level
     */
    public Character(int row, int col, Direction direction, Level level) {
        super(row, col, level);
        this.direction = direction;
        setAxis(direction);
    }

    /**
     * Checks if the character has collision in given direction.
     * Gets the blocking moveable out of the way if possible.
     * @param direction the direction that should be checked for collision.
     * @return true if character has collision in that direction
     */
    @Override
    boolean checkCollision(Direction direction) {
        setAxis(direction);
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
                        && destinationGuard.checkCollision(destinationGuardOppositeDirection);
                if (destinationHasCollision)
                    return true;
                destinationGuard.move();
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
    }

    /**
     * Getter.
     * @return direction character is looking at
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Setter.
     * @param direction new direction
     */
    protected void rotate(Direction direction) {
        this.direction = direction;
        setAxis(direction);
    }

    /**
     * Setter.
     * Sets axis from direction.
     * @param direction direction the character is facing
     */
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

    /**
     * Getter.
     * @return the {@link Axis} player is moving on.
     */
    Axis getAxis() {
        return axis;
    }
}

/**
 * Player Class
 */
class Player extends Character {
    /**
     * Constructor
     * @param row initial row
     * @param col initial column
     * @param level model of the level
     */
    public Player(int row, int col, Level level) {
        super(row, col, Direction.UP, level);
    }

    @Override
    void move(Direction direction) {
        super.move(direction);
        getLevel().setPlayerPos(getRow(), getCol());
    }

    /**
     * custom clone method.
     * @return a new object of the {@link Player} with the same initial variable contents
     */
    public Player clone() {
        return new Player(getRow(), getCol(), getLevel());
    }

    @Override
    public boolean isPlayer() {
        return true;
    }
}

/**
 * Guard class.
 */
class Guard extends Character {
    private boolean moved; //was guard already moved this "turn"

    /**
     * Consturctor.
     * @param row initial row
     * @param col initial column
     * @param direction initial direction the guard will be facing
     * @param level model of the level
     */
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

    /**
     * rotates guard to its left or right side
     * @param rotation {@link Rotation}
     */
    void rotateTo(Rotation rotation) {
        switch (getDirection()) {
            case DOWN:
                if (rotation == Rotation.RIGHT) {
                    rotate(Direction.LEFT);
                    break;
                }
                rotate(Direction.RIGHT);
                break;
            case LEFT:
                if (rotation == Rotation.RIGHT) {
                    rotate(Direction.UP);
                    break;
                }
                rotate(Direction.DOWN);
                break;
            case RIGHT:
                if (rotation == Rotation.RIGHT) {
                    rotate(Direction.DOWN);
                    break;
                }
                rotate(Direction.UP);
                break;
            case UP:
                if (rotation == Rotation.RIGHT) {
                    rotate(Direction.RIGHT);
                    break;
                }
                rotate(Direction.LEFT);
                break;
        }
    }

    /**
     * Checks if player is visible for this guard.
     * Ends game if so.
     */
    public void canSeePlayer() {
        if(getAxis() == Axis.HORIZONTAL) {
            int horizontal = Tools.dir2col(getDirection());
            int checkRow = getRow();

            for (int i = 1;; i++) {
                int checkCol;
                if (horizontal > 0)
                    checkCol = getCol() + i;
                else {
                    checkCol = getCol() - i;
                }
                Tile checkTile = getLevel().getTileAt(checkRow, checkCol);
                if(checkTile == null || checkTile.isWall())
                    return;
                if(checkTile.isCurvedIcyTile() && ((CurvedIcyTile) checkTile).getHorizontal() == getDirection())
                    return;
                Movable tileContent = checkTile.contains();
                if(tileContent == null)
                    continue;
                if(tileContent.isGuard() || tileContent.isCrate())
                    return;
                if(tileContent.isPlayer()) {
                    getLevel().lose();
                    return;
                }
            }
        }
    }

    /**
     * Moves guard to its backside if possible.
     */
    public void turn() {
        Direction oppositeDirection = Tools.getOppositeDirection(getDirection());
        if (moved)
            return;
        if (!checkCollision(oppositeDirection))
            move(oppositeDirection);
    }

    /**
     * Moves guard 1 field in the direction it is facing if possible.
     * Otherwise {@link #turn()}.
     */
    public void move() {
        if (moved)
            return;
        if (!checkCollision(getDirection())) {
            move(getDirection());
        }
        else
            turn();
    }

    @Override
    void move(Direction direction) {
        super.move(direction);
        setAxis(direction);
        moved = true;
        if(getLevel().getTileAt(getRow(), getCol()).isRotationPassage())
            rotateTo(((RotationPassage) getLevel().getTileAt(getRow(), getCol())).getRotation());
    }

    /**
     * Resets {@link #moved} to false.
     */
    public void resetMoved() {
        moved = false;
    }

    /**
     * Custom clone method.
     * @return a new object of the {@link Guard} with the same initial variable contents
     */
    public Guard clone() {
        return new Guard(getRow(), getCol(), getDirection(), getLevel());
    }

    @Override
    public boolean isGuard() {
        return true;
    }
}

/**
 * Crate class
 */
class Crate extends Movable {
    /**
     * Constructor.
     * @param row initial row
     * @param col initial column
     * @param level model of the level
     */
    public Crate(int row, int col, Level level) {
        super(row, col, level);
    }

    @Override
    boolean checkCollision(Direction direction) {
        int targetRow = Tools.dir2row(direction) + getRow();
        int targetCol = Tools.dir2col(direction) + getCol();

        Tile targetTile = super.getLevel().getTileAt(targetRow, targetCol);
        Movable targetTileContent = targetTile.contains();

        if(targetTile.isPitfall() && targetTileContent == null) {
            return false;
        }
        return super.checkCollision(direction);
    }

    @Override
    void move(Direction direction) {
        int targetRow = getRow() + Tools.dir2row(direction);
        int targetCol = getCol() + Tools.dir2col(direction);
        Tile targetTile = getLevel().getTileAt(targetRow, targetCol);
        Pitfall pit;
        if(targetTile.isPitfall() && !((pit = (Pitfall)targetTile)).isFilled()) {
            pit.fill();
            getLevel().updateTile(targetRow, targetCol);
            getLevel().popcrate(this);
            getLevel().getTileAt(getRow(), getCol()).steppedOnBy(null);
            getLevel().updateMoveablePresence(targetRow, targetCol, this);
            int tmprow = getRow();
            int tmpcol = getCol();
            remove();
            getLevel().updateMoveablePresence(tmprow, tmpcol, this);
        } else
            super.move(direction);
    }

    @Override
    public boolean isCrate() {
        return true;
    }

    /**
     * Removes this specific crate from the crates array in the model
     */
    private void remove(){
        setRow(-1);
        setCol(-1);
    }

    /**
     * Custom clone method.
     * @return a new object of the {@link Crate} with the same initial variable contents
     */
    public Crate clone() {
        return new Crate(getRow(), getCol(), getLevel());
    }
}
