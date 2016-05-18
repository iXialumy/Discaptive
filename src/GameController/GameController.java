package GameController;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by jpaus on 17.05.16.
 */
class GameController {
    private char[][] board;

    GameController(int level) {
        String filename = "res/Level" + level + ".txt";
        try {
            openBoard(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openBoard(String filename) throws IOException {
    	ArrayList<String> lineArray = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));

        String line = bufferedReader.readLine();
        for (int i = 0; line != null; i++) {
            lineArray.add(i, line);
            line = bufferedReader.readLine();
        }
        int longestLine = 0;
        for(String line2: lineArray) {
            int len = line2.length();
            if(len > longestLine) {
                longestLine = len;
            }
        }
        board = new char[lineArray.size()][longestLine];
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
