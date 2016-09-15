package discpative.model;

import discpative.view.ViewInterface;

public interface LevelInterface {
    /**
     * Register a new {@link discpative.view.View} in the model
     * @param view the new top view
     */
    void registerView (ViewInterface view);

    /**
     * Remove a view from the model
     * @param view the to be removed view
     */
    void unregisterView (ViewInterface view);

    /**
     * Getter.
     * @return total number of rows in the level
     */
    int getRowCount ();

    /**
     * Getter.
     * @return total number of columns in the level
     */
    int getColCount ();


    /**
     * Getter.
     * @param row a row of the level
     * @param col a column of the level
     * @return the {@link Tile} at given coordinates
     */
    Tile getTileAt(int row, int col);

    /**
     * Getter.
     * @param row row coordinate
     * @param col col coordinate
     * @return returns if the player is at given coordinates
     */
    boolean isPlayerAt (int row, int col);

    /**
     * Getter.
     * @return number of moves done by the player
     */
    int getMoveCount();
}
