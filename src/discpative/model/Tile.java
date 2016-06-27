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
}

class Pitfall extends Passage {
    protected boolean filled = false;

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
}


/**
 * The direction gives the side in which 1 of the entrances is facing.
 * The other one is facing 90 degrees clockwise.
 */
class IcyTile extends Wall {
    private Direction direction;

    IcyTile(Direction direction) {
        this.direction = direction;
    }

    @Override
    void steppedOnBy(Movable movable) {
        super.steppedOnBy(null);
    }
}

