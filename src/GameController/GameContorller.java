package GameController;

import java.util.ArrayList;
import In_Out.*;

/**
 * Created by jpaus on 17.05.16.
 */
public class GameContorller {
    private char[][] board;

    public GameContorller(int level) {
        String filename = "res/Level" + level + ".txt";
        openBoard(filename);
    }

    private void openBoard(String filename) {
    	ArrayList<String> lineArray = new ArrayList<String>();
        In.open(filename);
        String line;
        for (int i = 0; (line = In.readLine()) != null ; i++) {
    		lineArray.set(i, line);
    	}
        int longestLine = 0;
        for(String line: lineArray) {
            int len = line.length();
            if(len > longestLine)
                longestLine = len;
        }

    }


}
