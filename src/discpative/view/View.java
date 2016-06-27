package discpative.view;

import discpative.controller.Direction;
import discpative.controller.LevelController;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Region;
import discpative.model.LevelInterface;

/**
 * Created by jpaus on 22.06.16.
 */
public class View extends Region implements ViewInterface {
    private LevelInterface modelInterface;
    private LevelController controller;

    public View(LevelInterface modelInterface, LevelController controller) {
        this.modelInterface = modelInterface;
        this.controller = controller;
        setOnKeyPressed(new KeyPressHandler());
    }

    @Override
    public void updateTile(int row, int col) {

    }

    @Override
    public void updatePlayerPresence(int row, int col) {

    }

    @Override
    public void updateStatusLine() {

    }

    @Override
    public void announceLevelComplete() {

    }

    private class KeyPressHandler implements EventHandler<KeyEvent>{
        public void handle(KeyEvent keyEvent) {
            KeyCode keyCode = keyEvent.getCode();
            Direction direction;

            switch (keyCode) {
                case UP: case KP_UP: case W:
                    direction = Direction.UP;
                    break;
                case DOWN: case KP_DOWN: case S:
                    direction = Direction.DOWN;
                    break;
                case LEFT: case KP_LEFT: case A:
                    direction = Direction.LEFT;
                    break;
                case RIGHT: case KP_RIGHT: case D:
                    direction = Direction.RIGHT;
                    break;
                default:
                    direction = null;
                    break;
            }

            if(direction != null) {
                controller.handleMove(direction);
                modelInterface.drawLevel();
                keyEvent.consume();
            }
        }
    }


}
