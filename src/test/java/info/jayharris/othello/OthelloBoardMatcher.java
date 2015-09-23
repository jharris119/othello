package info.jayharris.othello;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class OthelloBoardMatcher extends BaseMatcher<Othello.Board> {

    Othello othello;
    Map<Othello.Board.Square, Othello.Color> pieces;

    public OthelloBoardMatcher(Othello othello, String boardString) {
        this(othello, new HashMap<Othello.Color, Set<String>>() {{
            this.put(Othello.Color.BLACK, Sets.newHashSet());
            this.put(Othello.Color.WHITE, Sets.newHashSet());

            for (int rank = 0; rank < othello.board.SQUARES_PER_SIDE; ++rank) {
                for (int file = 0; file < othello.board.SQUARES_PER_SIDE; ++file) {
                    char c = boardString.charAt(rank * othello.board.SQUARES_PER_SIDE + file);
                    if (c == 'b' || c == 'B') {
                        this.get(Othello.Color.BLACK).add("" + (char)('a' + file) + (rank + 1));
                    }
                    else if (c == 'w' || c == 'W') {
                        this.get(Othello.Color.WHITE).add("" + (char)('a' + file) + (rank + 1));
                    }
                }
            }
        }});
    }

    public OthelloBoardMatcher(Othello othello, Map<Othello.Color, Set<String>> pieceslist) {
        this.othello = othello;
        pieces = Maps.newHashMap();
        for (String str : pieceslist.get(Othello.Color.BLACK)) {
            pieces.put(othello.getSquare(str), Othello.Color.BLACK);
        }
        for (String str : pieceslist.get(Othello.Color.WHITE)) {
            pieces.put(othello.getSquare(str), Othello.Color.WHITE);
        }
    }

    @Override
    public boolean matches(Object item) {
        Othello.Board board = (Othello.Board) item;

        Othello.Board.Square square;
        for (int rank = 0; rank < board.SQUARES_PER_SIDE; ++rank) {
            for (int file = 0; file < board.SQUARES_PER_SIDE; ++file) {
                square = board.getSquare(rank, file);
                if (square.getColor() != pieces.getOrDefault(square, null)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("match all board pieces to colors in given map");
    }
}