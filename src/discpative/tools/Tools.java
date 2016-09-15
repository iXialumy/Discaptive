package discpative.tools;

import discpative.controller.Direction;

/**
 * Collection of useful Operators
 *
 * @author jpaus
 * @version 1.1
 */
public class Tools {
    /**
     * Converts a {@link Direction} into a difference in rows
     * @param direction One of the {@link Direction}s
     * @return difference in rows
     */
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

    /**
     * Converts a {@link Direction} into a difference in columns
     * @param direction One of the {@link Direction}s
     * @return difference in columns
     */
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

    /**
     * Converts difference in row and column into a {@link Direction}
     * @param rowDelta difference in rows
     * @param colDelta difference in rows
     * @return the corresponding {@link Direction}, but only if moving a single tile
     */
    public static Direction delta2dir(int rowDelta, int colDelta) {
        if(rowDelta == 0) {
            if(colDelta == 1)
                return Direction.RIGHT;
            if(colDelta == -1)
                return Direction.LEFT;
        } else if(colDelta == 0) {
            if(rowDelta == 1)
                return Direction.DOWN;
            if(rowDelta == -1)
                return Direction.UP;
        }
        return null;
    }

    /**
     * The opposite of a specific {@link Direction}
     * @param direction the {@link Direction} you want to know the opposite of
     * @return the opposite of given {@link Direction}
     */
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
