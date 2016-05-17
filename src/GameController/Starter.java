package GameController;

import In_Out.Out;

/**
 * Created by jpaus on 17.05.16.
 */
public class Starter {
    private static GameContorller gc;

    public static void main(String[] args) {
        gc = new GameContorller(1);
        Out.println(gc.getBoardf());
    }

}
