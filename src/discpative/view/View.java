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
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class View extends Region implements ViewInterface {

    private LevelInterface levelInterface;
    private LevelController levelController;



    protected int rowCount, colCount;
    private Group[][] tileGraphic;
    private Text statusLine;
    protected Shape playerGraphic;
    private static final int TILE_SHAPE_INDEX = 0;
    private static final int FIGURE_SHAPE_INDEX = 1;


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
        levelInterface.registerView(this);
    }


    //...

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
    public void updatePlayerPresence (int row, int col) {
        ObservableList<Node> layers = tileGraphic[row][col].getChildren();
        if (levelInterface.isPlayerAt(row, col))
            layers.add(FIGURE_SHAPE_INDEX, playerGraphic);
        else
            layers.remove(FIGURE_SHAPE_INDEX);
    }
    public void updateCratePresence(int row, int col) {
        ObservableList<Node> layers = tileGraphic[row][col].getChildren();
        if (levelInterface.getTileAt(row, col).contains() != null && levelInterface.getTileAt(row, col).contains().isCrate()) {
            layers.add(FIGURE_SHAPE_INDEX, makeCrateGraphic());
        }
        else if (layers.size() > 1)
            layers.remove(FIGURE_SHAPE_INDEX);
    }

    public void updatePitfall(int row, int col) {
        //TODO
    }
    public void updateStatusLine () {
        statusLine.setText("Anzahl Züge: " + levelInterface.getMoveCount ());
    }
    public void updateTile(int row, int col) {
        ObservableList<Node> layers = tileGraphic[row][col].getChildren();
        Tile t = levelInterface.getTileAt(row, col);
        layers.set(TILE_SHAPE_INDEX, makeTileGraphic(t));
    }

    public void announceLevelComplete() {
        statusLine.setText("Level gelöst in " + levelInterface.getMoveCount() + " Zügen!");
        statusLine.setFill(Color.GREEN);
        statusLine.setFont(Font.font("System", FontWeight.BOLD, 16));
        playerGraphic.setFill(Color.MEDIUMSPRINGGREEN);
    }





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
                levelController.handleMove(dir);
                event.consume();
            }
        }
    }


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


    protected Node makeCrateGraphic() {
        return makeTileRect(Color.BROWN, null);
    }

    protected Node makeGuardGraphic() {     //Player mit anderer Farbe
        Circle c = new Circle(0, 0, 18);
        c.setFill(Color.RED);
        c.setStroke(Color.DARKBLUE);
        c.setStrokeWidth(1);
        c.setStrokeType(StrokeType.INSIDE);
        return c;
    }

    protected Node makePassageGraphic() {
        return makeTileRect(Color.WHITE,null);
    }

    protected Node makePitfallGraphic() {
        return makeTileRect(Color.BLACK,null);
    }

    protected Node makePlayerGoalGraphic() {
        return makeTileRect(Color.GREEN,null);
    }

    protected Shape makePlayerGraphic() {
        Circle c = new Circle(0, 0, 18);
        c.setFill(Color.CORNFLOWERBLUE);
        c.setStroke(Color.DARKBLUE);
        c.setStrokeWidth(1);
        c.setStrokeType(StrokeType.INSIDE);
        //c.setRotate(90);
        return c;
    }

    protected Node makeTurnerGraphic() {
        return makeTileRect(Color.LIGHTPINK,null);
    }

    protected Node makeWallGraphic() {
        return makeTileRect(Color.DIMGRAY, null);
    }

    protected Node makeIcyTileGraphic() {return  makeTileRect(Color.LIGHTCYAN, null);}



    protected Node makeTileGraphic(Tile tile) {
        if (tile.isPitfall())
            return makePitfallGraphic();
        else if (tile.isObjective())
            return makePlayerGoalGraphic();
        else if (tile.isRotationPassage())
            return makeTurnerGraphic();
        else if (tile.isWall())
            return makeWallGraphic();
        else if (tile.isIcyTile())
            return makeIcyTileGraphic();
        else if (tile.isCurvedIcyTile()) {
            //TODO
            return null;
        }
        else
            return makePassageGraphic();
    }




    protected double computePrefWidth (double height) {
        return colCount * 48;
    }
    protected double computePrefHeight (double width) {
        return rowCount * 48 + 32;
    }

    protected void layoutChildren () {
        super.layoutChildren();
        for (int row = 0; row < rowCount; row++)
            for (int col = 0; col < colCount; col++) {
                Group g = tileGraphic[row][col];
                g.relocate(col * 48, row * 48);
            }
        statusLine.relocate(4, rowCount * 48);
    }

    //...

}