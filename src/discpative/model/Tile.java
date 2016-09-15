package discpative.model;

import discpative.controller.Direction;
import discpative.controller.Rotation;
import discpative.io.Out;
import discpative.tools.Tools;

/**
 * Basic abstraction of every tile
 */
public abstract class Tile {
    private Movable containing; //Movable that is on this tile

    /**
     * Consturctor.
     */
    Tile() {}

    /**
     * Constructor.
     * @param containing a {@link Movable} that is initially contained
     */
    Tile(Movable containing) {
        this.containing = containing;
    }

    /**
     * Setter.
     * @param movable new {@link Movable}
     */
    void steppedOnBy(Movable movable) {
        containing = movable;
    }

    /**
     * Getter.
     * @return {@link #containing}
     */
    public Movable contains() {
        return containing;
    }

    public boolean isWall() {
        return false;
    }

    public boolean isEmptypassage() {
        return false;
    }

    public boolean isObjective() {
        return false;
    }

    public boolean isPitfall() {
        return false;
    }

    public boolean isRotationPassage() {
        return false;
    }

    public boolean isIcyTile() {
        return false;
    }

    public boolean isCurvedIcyTile() {
        return false;
    }

    public boolean isFilled(){
        return  false;
    }

    /**
     * Getter.
     * Solely used for the rotation of {@link CurvedIcyTile}.
     * @return the main {@link Direction}
     */
    public Direction getDirection() {
        return null;
    }
}

/**
 * Refinement of Tile for all walkable tiles
 */
abstract class Passage extends Tile {
    /**
     * Constructor.
     */
    Passage() {
        super();
    }

    /**
     * Constructor.
     * @param movable initial movable to be contained
     */
    Passage(Movable movable) {
        super(movable);
    }
}

/**
 * Refinement of {@link Passage} to basic floor.
 */
class EmptyPassage extends Passage {
    /**
     * Constructor.
     */
    public EmptyPassage() {
        super();
    }

    /**
     * Constructor
     * @param movable initial {@link Movable} to be contained
     */
    public EmptyPassage(Movable movable) {
        super(movable);
    }

    @Override
    public boolean isEmptypassage() {
        return true;
    }
}

/**
 * Refinement of {@link Tile} to basic wall
 */
class Wall extends Tile {
    @Override
    void steppedOnBy(Movable movable) {
        assert false : "cannot step on walls.";
    }

    @Override
    public boolean isWall() {
        return true;
    }
}

/**
 * Refinement of {@link EmptyPassage} to a Passage that rotates guards
 */
class RotationPassage extends EmptyPassage {
    protected final Rotation rotation; //left or right rotation

    /**
     * Constructor.
     * @param rotation right or left, where the player should be rotated to
     * @param movable initial movable
     */
    public RotationPassage(Rotation rotation, Movable movable) {
        super(movable);
        this.rotation = rotation;
    }

    /**
     * Constructor.
     * @param rotation right or left, where the player should be rotated to
     */
    public RotationPassage(Rotation rotation) {
        super();
        this.rotation = rotation;
    }

    /**
     * Getter.
     * @return left or right
     */
    public Rotation getRotation() {
        return this.rotation;
    }

    @Override
    public boolean isRotationPassage() {
        return true;
    }
}

/**
 * Refinement from a {@link Passage} to a pitfall
 */
class Pitfall extends Passage {
    private boolean filled = false;// can movables walt on here

    /**
     * Constructor.
     */
    public Pitfall() {
        super();
    }

    @Override
    void steppedOnBy(Movable movable) {
        super.steppedOnBy(movable);
    }

    /**
     * Sets filled to true
     */
    public void fill() {
        filled = true;
    }

    public boolean isFilled() {
        return filled;
    }

    @Override
    public boolean isPitfall() {
        return true;
    }
}

/**
 * Objetive Tile
 */
class Objective extends Passage {
    /**
     * Constructor
     */
    Objective() {
        super();
    }

    /**
     * Constructor.
     * @param movable a {@link Movable} that is initially contained
     */
    Objective(Movable movable) {
        super(movable);
    }

    @Override
    void steppedOnBy(Movable movable) {
        if (movable.isPlayer())
            movable.getLevel().winGame();
        super.steppedOnBy(movable);
}

    @Override
    public boolean isObjective() {
        return true;
    }
}

/**
 * Slippery tile, controlls movement directions.
 */
class IcyTile extends EmptyPassage {
    protected Direction icyDirection; //next movement direction for stepped on movable

    @Override
    public boolean isIcyTile() {
        return true;
    }

    /**
     * Setter.
     * @param movable new {@link Movable}
     * @param direction the {@link Direction} for the next move of the movable
     */
    void steppedOnBy(Movable movable, Direction direction) {
        super.steppedOnBy(movable);
        icyDirection = direction;
    }

    public Direction getIcyDirection() {
        return icyDirection;
    }
}

/**
 * The direction gives the side in which 1 of the entrances is facing.
 * The other one is facing 90 degrees clockwise.
 */
class CurvedIcyTile extends IcyTile {
    private Direction vertical; //vertical open side of the curved icy tile
    private Direction horizontal; //horizontal open side of the curved icy tile
    Direction mainDirection; //main direction of the icy tile, used for graphic only

    /**
     * Constructor.
     * @param vertical vertical open side
     * @param horizontal horizontal open side
     * @param mainDirection main direction for graphic
     */
    CurvedIcyTile(Direction vertical, Direction horizontal, Direction mainDirection) {
        this.vertical = vertical;
        this.horizontal = horizontal;
        this.mainDirection = mainDirection;
    }

    @Override
    void steppedOnBy(Movable movable, Direction direction) {
        super.steppedOnBy(movable);
        Direction opppositeDirection = Tools.getOppositeDirection(direction);
        if (opppositeDirection == vertical)
            icyDirection = horizontal;
        else if (opppositeDirection == horizontal)
            icyDirection = vertical;
        else
            Out.println("Error at collision with CurvedIcyTile");
    }

    @Override
    public boolean isCurvedIcyTile() {
        return true;
    }

    @Override
    public boolean isIcyTile() {
        return false;
    }

    public Direction getVertical() {
        return vertical;
    }

    public Direction getHorizontal() {
        return horizontal;
    }

    @Override
    public Direction getDirection() {
        return mainDirection;
    }
}

