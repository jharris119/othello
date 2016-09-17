package info.jayharris.othello;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.HashMap;
import java.util.Map;

public class OthelloBoardMatcher2 extends BaseMatcher<Othello.Board> {

    Othello.Board board;
    Map<Othello.Board.Square, Othello.Color> pieces;

    public OthelloBoardMatcher2(Othello.Board board) {
        this.board = board;
        this.pieces = initMap(board);
    }

    @Override
    public boolean matches(Object item) {
        return this.matches((Othello.Board) item);
    }

    public boolean matches(Othello.Board board) {
        return initMap(board).equals(pieces);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("expected:\n" + board);
    }

    private static Map<Othello.Board.Square, Othello.Color> initMap(Othello.Board board) {
        Othello.Board.Square square;
        Othello.Color color;

        Map<Othello.Board.Square, Othello.Color> map = new HashMap<>();
        for (int rank = 0; rank < board.SQUARES_PER_SIDE; ++rank) {
            for (int file = 0; file < board.SQUARES_PER_SIDE; ++file) {
                color = (square = board.getSquare(rank, file)).getColor();
                if (color != null) {
                    map.put(square, color);
                }
            }
        }
        return map;
    }
}
