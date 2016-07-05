package discpative.model;

import discpative.controller.Direction;
import discpative.controller.Rotation;

abstract class Tile {
    private int steppedOnCounter = 0;
    private Movable containing;

    Tile() {}

    Tile(Movable containing) {
        this.containing = containing;
    }

    void steppedOnBy(Movable movable) {
        containing = movable;
        if(containing != null && containing.getClass() == Player.class)
            steppedOnCounter++;
    }
    public int getSteppedOnCount() {
        return steppedOnCounter;
    }

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
}

abstract class Passage extends Tile {
    Passage() {
        super();
    }

    Passage(Movable movable) {
        super(movable);
    }
}

class EmptyPassage extends Passage {
    public EmptyPassage() {
        super();
    }

    public EmptyPassage(Movable m) {
        super(m);
    }

    @Override
    public boolean isEmptypassage() {
        return true;
    }
}

class Wall extends Tile {
    @Override
    void steppedOnBy(Movable movable) {
        assert false : "cannot step on walls.";
    }

    @Override
    public int getSteppedOnCount() {
        return 0;
    }

    @Override
    public boolean isWall() {
        return true;
    }
}

class RotationPassage extends EmptyPassage {
    protected final Rotation rotation;

    public RotationPassage(Rotation rotation, Movable movable) {
        super(movable);
        this.rotation = rotation;
    }
    public RotationPassage(Rotation rotation) {
        super();
        this.rotation = rotation;
    }

    @Override
    public void steppedOnBy(Movable movable) {
        if (movable.getClass() == Guard.class) {
            Guard guard = (Guard) movable;
            guard.rotateTo(this.rotation);
            super.steppedOnBy(guard);
        } else {
            super.steppedOnBy(movable);
        }

    }

    public Rotation getRotation() {
        return this.rotation;
    }

    @Override
    public boolean isRotationPassage() {
        return true;
    }
}

class Pitfall extends Passage {
    private boolean filled = false;

    public Pitfall() {
        super();
    }

    public Pitfall(Movable movable) {
        super();
        super.steppedOnBy(movable);
    }

    @Override
    public void steppedOnBy(Movable movable) {
        if (!filled && movable.getClass() == Crate.class)
            filled = true;
        else
            super.steppedOnBy(movable);
    }

    @Override
    public int getSteppedOnCount() {
        return 0;
    }

    @Override
    public Movable contains() {
        return null;
    }

    public boolean isFilled() {
        return filled;
    }

    @Override
    public boolean isPitfall() {
        return true;
    }
}

class Objective extends Passage {

    Objective() {
        super();
    }

    Objective(Movable movable) {
        super(movable);
    }

    @Override
    void steppedOnBy(Movable movable) {
        if (movable.getClass() == Player.class) {
            // TODO: 21.06.16 Game win
        } else {
            super.steppedOnBy(movable);
        }

    }

    @Override
    public boolean isObjective() {
        return true;
    }
}

/**
 * The direction gives the side in which 1 of the entrances is facing.
 * The other one is facing 90 degrees clockwise.
 */
class IcyTile extends Wall {
    private Direction directionOne;
    private Direction directionTwo;

    IcyTile(Direction directionOne, Direction directionTwo) {
        this.directionOne = directionOne;
        this.directionTwo = directionTwo;
    }

    void steppedOnBy(Movable movable, Direction direction) {
        super.steppedOnBy(movable);
        //TODO Icy Tile
    }

    @Override
    public boolean isIcyTile() {
        return true;
    }

    public Direction getDirectionOne() {
        return directionOne;
    }

    public Direction getDirectionTwo() {
        return directionTwo;
    }
}

