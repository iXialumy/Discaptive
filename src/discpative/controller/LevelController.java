package discpative.controller;

import discpative.io.Out;
import discpative.model.Level;
import discpative.view.ViewInterface;

/**
 * Level Controller.
 *
 * Look up {@link ControllerInterface} or {@link #LevelController(Level)} for more information.
 */
public class LevelController implements ControllerInterface{
    private Level level; //model of the level
    private boolean active; //status of the level

    /**
     * Constructor.
     *
     * @param level model of the level
     */
    public LevelController(Level level) {
        this.level = level;
        active = true;
    }

    @Override
    public void handleMove(ViewInterface view, Direction direction) {
        if (active && level.canPlayerMoveTo(direction))
            level.movePlayerTo(direction);
    }

    @Override
    public void handleClick(ViewInterface view, int row, int col) {
        if(active && level.canplayerMoveTo(row, col))
        level.movePlayerTo(row, col);
    }

    @Override
    public void handleComplete(ViewInterface view) {
        active ^= true;
        Out.println("Level done!");
    }
}
