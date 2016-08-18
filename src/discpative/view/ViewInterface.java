package discpative.view;

public interface ViewInterface {

    void updateGuardPresence(int row, int col);
    void updatePlayerPresence(int row, int col);
    void updateCratePresence(int row, int col);
    void updateStatusLine();
    void updateTile(int row, int col);

    void announceLevelComplete();
}