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

        System.out.print(String.format("%s's turn. Place disc at: ",
                color == Othello.Color.WHITE ? "White" : "Black"));

        Othello.Board.Square square = null;
        while (square == null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                square = othello.getSquare(br.readLine());
            }
            catch (IOException |IllegalArgumentException e) {
                square = null;
            }
        }
        return square;
    }

    public static OthelloPlayerWithKeyboard create(Othello othello, Othello.Color color) {
        return new OthelloPlayerWithKeyboard(othello, color);
    }
}
