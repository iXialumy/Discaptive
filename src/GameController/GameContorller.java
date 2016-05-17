package GameController;

import java.util.ArrayList;
import In_Out.*;

/**
 * Created by jpaus on 17.05.16.
 */
class GameContorller {
    private char[][] board;

    GameContorller(int level) {
        String filename = "file:res/Level" + level + ".txt";
        openBoard(filename);
    }

    private void openBoard(String filename) {
    	ArrayList<String> lineArray = new ArrayList<String>();
        In.open(filename);
        String line;
        for (int i = 0; true ; i++) {
            line = In.readLine(); //TODO: fix
            if (line == null)
                break;
            lineArray.set(i, line);
    	}
        int longestLine = 0;
        for(String line2: lineArray) {
            int len = line2.length();
            if(len > longestLine) {
                longestLine = len;
            }
        }
        for (int i = 0; i < lineArray.size(); i++) {
            for (int j = 0; j < longestLine; j++) {
                board[i][j] = lineArray.get(i).charAt(j);
            }
        }
    }
    
    String getBoardf() {
        String output = "";
        for (int i = 0, boardLength = board.length; i < boardLength; i++) {
            char[] aBoard = board[i];
            for (int j = 0; j < aBoard.length; j++) {
                output += aBoard[j];
            }
            output += "\n";
        }
        return output;
    }

}
