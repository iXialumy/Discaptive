package discpative.model;

import discpative.view.ViewInterface;

public interface LevelInterface {
    void registerView (ViewInterface view);
    void unregisterView (ViewInterface view);

    int getRowCount ();
    int getColCount ();



    Tile getTileAt(int row, int col);
    boolean isPlayerAt (int row, int col);

    int getMoveCount();
}
