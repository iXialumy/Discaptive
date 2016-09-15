package discpative.controller;

import discpative.view.ViewInterface;

/**
 * Interface for the Controller
 *
 * @author jpaus
 * @version 1.1 final
 */
public interface ControllerInterface {

    /**
     * handles the Click on a tile on the screen
     * @param view the active {@link discpative.view.View} where the click should be handled on
     * @param row the row the click happened in
     * @param col the col the click happened in
     */
    void handleClick (ViewInterface view, int row, int col);

    /**
     * hanles the buttonpress triggered move on the screen
     * @param view the active {@link discpative.view.View} where the movement should be handled on
     * @param direction the direction the player shall move
     */
    void handleMove(ViewInterface view, Direction direction);

    /**
     * lets you know you completed the level
     * @param view the active {@link discpative.view.View} where the announcement should be handled on
     */
    void handleComplete (ViewInterface view);
}
