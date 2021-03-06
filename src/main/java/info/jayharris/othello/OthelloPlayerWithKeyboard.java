package info.jayharris.othello;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * An OthelloPlayer that takes moves from keyboard input.
 */
public class OthelloPlayerWithKeyboard extends OthelloPlayer {

    BufferedReader reader;

    public OthelloPlayerWithKeyboard(Othello othello, Othello.Color color) {
        super(othello, color);
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }
    
    @Override
    public Othello.Board.Square getMove() {
        System.out.println(othello.board);

        System.out.println(String.format("%s's turn. Place disc at: ",
                color == Othello.Color.WHITE ? "White" : "Black"));

        Othello.Board.Square square = null;
        while (square == null) {
            try {
                square = othello.getSquare(reader.readLine());
            }
            catch (IllegalArgumentException iax) {
                // noop
            }
            catch (IOException iox) {
                iox.printStackTrace();
                throw new RuntimeException(iox);
            }
        }
        return square;
    }
}
