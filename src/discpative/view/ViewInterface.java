package discpative.view;

public interface ViewInterface {
    void updateTile (int row, int col);
    void updatePlayerPresence (int row, int col);

    void updateStatusLine ();
    void announceLevelComplete ();
}
