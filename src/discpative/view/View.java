package discpative.view;

import discpative.controller.Direction;
import discpative.controller.LevelController;
import discpative.model.LevelInterface;
import discpative.model.Tile;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Visuals of the game.
 *
 * Contains graphics creation and user input.
 * @author jpaus
 * @version 1.2 final
 */
public class View extends Region implements ViewInterface {

    private LevelInterface levelInterface; //interface for the model
    private LevelController levelController; //interface for the controller

    protected int rowCount, colCount; //number of rows and columns in the level
    private Group[][] tileGraphic; //2d array of all tile graphics
    private Text statusLine; //status line under the view of the level
    protected Shape playerGraphic; //graphic for the player
    private static final int TILE_SHAPE_INDEX = 0; //index of tiles under movables
    private static final int FIGURE_SHAPE_INDEX = 1; //index of movables, above tiles

    /**
     * Constructor.
     * @param levelInterface interface for the model
     * @param levelController interface for the controller
     */
    public View(LevelInterface levelInterface, LevelController levelController) {
        this.levelInterface = levelInterface;
        this.levelController = levelController;


        this.levelInterface = levelInterface;
        rowCount = levelInterface.getRowCount();
        colCount = levelInterface.getColCount();
        playerGraphic = makePlayerGraphic();
        tileGraphic = new Group[rowCount][colCount];
        ObservableList<Node> myChildren = getChildren();
        for (int row = 0; row < rowCount; row++)
            for (int col = 0; col < colCount; col++) {
                Tile t = levelInterface.getTileAt(row, col);
                Group g = new Group(makeTileGraphic(t));
                if (levelInterface.isPlayerAt(row, col))
                    g.getChildren().add(playerGraphic);
                if (levelInterface.getTileAt(row, col).contains() != null
                        && levelInterface.getTileAt(row, col).contains().isGuard())
                    g.getChildren().add(makeGuardGraphic());
                if (levelInterface.getTileAt(row, col).contains() != null
                        && levelInterface.getTileAt(row, col).contains().isCrate())
                    g.getChildren().add(makeCrateGraphic());
                tileGraphic[row][col] = g;
                myChildren.add(g);
            }
        statusLine = new Text("Los geht's...");
        myChildren.add(statusLine);
        setOnKeyPressed(new KeyPressHandler());
        setOnMouseClicked(new MouseClickHandler());
        levelInterface.registerView(this);
    }


    /**
     * Add/Remove guard graphic at coordinates
     * @param row row coordinate
     * @param col column coordinate
     */
    public void updateGuardPresence(int row, int col) {
        ObservableList<Node> layers = tileGraphic[row][col].getChildren();
        if (levelInterface.getTileAt(row, col).contains() != null
                && levelInterface.getTileAt(row, col).contains().isGuard()) {
            if (layers.size() > 1)
                layers.remove(FIGURE_SHAPE_INDEX);
            layers.add(FIGURE_SHAPE_INDEX, makeGuardGraphic());
        }
        else
            layers.remove(FIGURE_SHAPE_INDEX);
    }

    /**
     * Add/Remove player graphic at coordinates
     * @param row row coordinate
     * @param col column coordinate
     */
    public void updatePlayerPresence (int row, int col) {
        ObservableList<Node> layers = tileGraphic[row][col].getChildren();
        if (levelInterface.isPlayerAt(row, col))
            layers.add(FIGURE_SHAPE_INDEX, playerGraphic);
        else
            layers.remove(FIGURE_SHAPE_INDEX);
    }
    /**
     * Add/Remove crate graphic at coordinates
     * @param row row coordinate
     * @param col column coordinate
     */
    public void updateCratePresence(int row, int col) {
        ObservableList<Node> layers = tileGraphic[row][col].getChildren();
        if (levelInterface.getTileAt(row, col).contains() != null && levelInterface.getTileAt(row, col).contains().isCrate()) {
            layers.add(FIGURE_SHAPE_INDEX, makeCrateGraphic());
        }
        else if (layers.size() > 1)
            layers.remove(FIGURE_SHAPE_INDEX);
    }

    /**
     * Show count of moves
     */
    public void updateStatusLine () {
        statusLine.setText("Anzahl Züge: " + levelInterface.getMoveCount ());
    }

    /**
     * Add/change tile graphic at coordinates
     * @param row row coordinate
     * @param col column coordinate
     */
    public void updateTile(int row, int col) {
        ObservableList<Node> layers = tileGraphic[row][col].getChildren();
        Tile t = levelInterface.getTileAt(row, col);
        layers.set(TILE_SHAPE_INDEX, makeTileGraphic(t));
    }

    /**
     * complete the level
     */
    public void announceLevelComplete() {
        statusLine.setText("Level gelöst in " + levelInterface.getMoveCount() + " Zügen!");
        statusLine.setFill(Color.GREEN);
        statusLine.setFont(Font.font("System", FontWeight.BOLD, 16));
        playerGraphic.setFill(Color.MEDIUMSPRINGGREEN);

        levelController.handleComplete(this);
    }


    /**
     * Class to handle key inputs
     */
    private class KeyPressHandler implements EventHandler<KeyEvent> {
        public void handle(KeyEvent event) {
            KeyCode key  = event.getCode();
            Direction dir = null;
            switch (key) {
                case UP:
                case KP_UP:
                case W:
                    dir = Direction.UP;
                    break;
                case DOWN:
                case KP_DOWN:
                case S:
                    dir = Direction.DOWN;
                    break;
                case LEFT:
                case KP_LEFT:
                case A:
                    dir = Direction.LEFT;
                    break;
                case RIGHT:
                case KP_RIGHT:
                case D:
                    dir = Direction.RIGHT;
                    break;
            }
            if (dir != null) {
                levelController.handleMove(View.this, dir);
                event.consume();
            }
        }
    }

    /**
     * Class to handle mouse click inputs
     */
    private class MouseClickHandler implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
            int row = ((int) event.getY()) / 48;
            int col = ((int) event.getX()) / 48;
            if(0 <= row && row < rowCount && 0 <= col && col < colCount) {
                levelController.handleClick(View.this, row, col);
            }
        }
    }

    /**
     * Basic rectangular graphic that fills the whole square
     * @param fill Base Color
     * @param stroke Outline Color
     * @return Basic rectangular graphic that fills the whole square
     */
    private Rectangle makeTileRect(Color fill, Color stroke) {
        Rectangle r = new Rectangle(-24, -24, 48, 48);
        r.setFill(fill);
        if (stroke != null) {
            r.setStroke(stroke);
            r.setStrokeWidth(1);
            r.setStrokeType(StrokeType.INSIDE);
        }
        return r;
    }

    /**
     * basic crate graphic
     * @return basic crate graphic
     */
    protected Node makeCrateGraphic() {
        //return makeTileRect(Color.BROWN, null);
        Rectangle r = new Rectangle(-18, -18, 36, 36);
        r.setFill(Color.BROWN);
        return r;
    }

    /**
     * basic guard graphic
     * @return basic guard graphic
     */
    protected Node makeGuardGraphic() {     //Player mit anderer Farbe
        Circle c = new Circle(0, 0, 18);
        c.setFill(Color.RED);
        c.setStroke(Color.DARKBLUE);
        c.setStrokeWidth(1);
        c.setStrokeType(StrokeType.INSIDE);
        return c;
    }

    /**
     * basic empty passage graphic
     * @return basic empty passage graphic
     */
    protected Node makePassageGraphic() {
        return makeTileRect(Color.WHITE, null);
    }

    /**
     * pitfall graphic
     * either filled or empty
     * @param filled is the pitfall filled
     * @return pitfall graphic
     */
    protected Node makePitfallGraphic(boolean filled) {
        Rectangle outer = makeTileRect(Color.WHITE, null);
        Rectangle inner = new Rectangle(-20, -20, 40, 40);
        if (filled) {
            inner.setFill(Color.BROWN);
            inner.setStroke(Color.BLACK);
            inner.setStrokeWidth(1.0);
            inner.setStrokeType(StrokeType.INSIDE);
        } else
            inner.setFill(Color.BLACK);

        return new Group(outer, inner);

    }

    /**
     * basic player destiantion tile graphic
     * @return basic player destination tile graphic
     */
    protected Node makePlayerGoalGraphic() {
        return makeTileRect(Color.GREEN,null);
    }

    /**
     * basic player graphic
     * @return basic player graphic
     */
    protected Shape makePlayerGraphic() {
        Circle c = new Circle(0, 0, 18);
        c.setFill(Color.CORNFLOWERBLUE);
        c.setStroke(Color.DARKBLUE);
        c.setStrokeWidth(1);
        c.setStrokeType(StrokeType.INSIDE);
        return c;
    }

    /**
     * basic rotation tile graphic
     * @return basic rotation tile graphic
     */
    protected Node makeTurnerGraphic() {
        return makeTileRect(Color.LIGHTPINK,null);
    }

    /**
     * basic wall graphic
     * @return basic wall graphic
     */
    protected Node makeWallGraphic() {
        return makeTileRect(Color.DIMGRAY, null);
    }

    /**
     * basic icy tile graphic
     * @return basic icy tile graphic
     */
    protected Node makeIcyTileGraphic() {
        return  makeTileRect(Color.LIGHTCYAN, null);
    }

    /**
     * curved icy tile graphic
     * rotated for all possible direction combinations
     * @param direction main direction of the curved icy tile
     * @return curved icy tile graphic
     */
    protected Node makeCurvedIcyTileGraphic(Direction direction) {
        Node base = makeIcyTileGraphic();
        Node wall1 = new Rectangle(-24, -24, 6, 48);
        Node wall2 = new Rectangle(-24, 18, 48, 6);
        Group curved = new Group(base, wall1, wall2);

        switch (direction) {
            case RIGHT:
                curved.setRotate(90);
                break;
            case DOWN:
                curved.setRotate(180);
                break;
            case LEFT:
                curved.setRotate(-90);
                break;
            default:
                break;
        }
        return curved;
    }


    /**
     * aggregator for tile graphics
     * @param tile a tile
     * @return the corresponding graphic
     */
    protected Node makeTileGraphic(Tile tile) {
        if (tile.isPitfall())
            return makePitfallGraphic(tile.isFilled());
        else if (tile.isObjective())
            return makePlayerGoalGraphic();
        else if (tile.isRotationPassage())
            return makeTurnerGraphic();
        else if (tile.isWall())
            return makeWallGraphic();
        else if (tile.isIcyTile())
            return makeIcyTileGraphic();
        else if (tile.isCurvedIcyTile())
            return makeCurvedIcyTileGraphic(tile.getDirection());
        else
            return makePassageGraphic();
    }

    /**
     * calculates the window width
     * @param height irrelevant
     * @return window width
     */
    protected double computePrefWidth (double height) {
        return colCount * 48;
    }

    /**
     * calculates the window height
     * @param width irrelevant
     * @return window height
     */
    protected double computePrefHeight (double width) {
        return rowCount * 48 + 32;
    }

    /**
     * configures the window layout
     */
    protected void layoutChildren () {
        super.layoutChildren();
        for (int row = 0; row < rowCount; row++)
            for (int col = 0; col < colCount; col++) {
                Group g = tileGraphic[row][col];
                g.relocate(col * 48, row * 48);
            }
        statusLine.relocate(4, rowCount * 48);
    }

}