package discpative.controller;

import discpative.io.Out;
import discpative.model.Level;
import discpative.view.View;

public class LevelController {

    private Level level;
    private boolean active;

    public LevelController(Level level) {
        this.level = level;
        active = true;
    }

    public void handleMove(Direction direction) {
        if (!active)
            return;
        if (level.canPlayerMoveTo(direction))
            level.movePlayerTo(direction);
    }

    public void handleCompletion(View view) {
        active ^= true;
        Out.println("Level done!");
    }
}
