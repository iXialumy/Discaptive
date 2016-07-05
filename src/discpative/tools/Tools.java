package discpative.tools;

import discpative.controller.Direction;

public class Tools {
    public static int dir2row(Direction direction) {
        switch (direction){
            case UP:
                return -1;
            case DOWN:
                return +1;
            default:
                return 0;
        }
    }

    public static int dir2col(Direction direction) {
        switch (direction){
            case RIGHT:
                return 1;
            case LEFT:
                return -1;
            default:
                return 0;
        }
    }

    public static Direction getOppositeDirection(Direction direction) {
        Direction oppositeDirection = Direction.DOWN;
        switch (direction) {
            case UP:
                oppositeDirection = Direction.DOWN;
                break;
            case DOWN:
                oppositeDirection = Direction.UP;
                break;
            case LEFT:
                oppositeDirection = Direction.RIGHT;
                break;
            case RIGHT:
                oppositeDirection = Direction.LEFT;
                break;
        }

        return oppositeDirection;
    }
}
