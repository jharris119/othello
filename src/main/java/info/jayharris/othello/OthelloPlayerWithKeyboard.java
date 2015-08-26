package info.jayharris.othello;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OthelloPlayerWithKeyboard extends OthelloPlayer {

    public OthelloPlayerWithKeyboard(Othello othello, Othello.Color color) {
        super(othello, color);
    }

    @Override
    public Othello.Board.Square getMove() {
        System.out.println(othello.board);

        System.out.println(String.format("%s's turn. Place disc at: ",
                color == Othello.Color.WHITE ? "White" : "Black"));

        Othello.Board.Square square = null;
        while (square == null) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                square = othello.getSquare(br.readLine());
            }
            catch (IOException |IllegalArgumentException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return square;
    }
}
