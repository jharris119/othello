package info.jayharris.othello;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class OthelloTest {

    Othello othello;

    @Before
    public void setUp() {
        othello = new Othello((p) -> null, (p) -> null);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetSquare() {
        assertSame(othello.board.grid[0][0], othello.board.getSquare("a1"));
        assertSame(othello.board.grid[4][6], othello.board.getSquare("E7"));
        assertSame(othello.board.grid[7][7], othello.board.getSquare("h8"));

        thrown.expect(IllegalArgumentException.class);
        othello.board.getSquare("");
        othello.board.getSquare("abc");
        othello.board.getSquare("5h");
        othello.board.getSquare("d06");
    }

    @Test
    public void testGetMooreNeighborhood() {
        Othello.Board.Square square;
        Set<Othello.Board.Square> expected, actual;

        square   = othello.getSquare("f5");
        actual   = square.getMooreNeighborhood();
        expected = ImmutableSet.of(square.get_e(), square.get_n(), square.get_ne(), square.get_nw(),
                square.get_s(), square.get_se(), square.get_sw(), square.get_w());
        assertEquals(expected, actual);

        square   = othello.getSquare("b1");
        actual   = square.getMooreNeighborhood();
        expected = ImmutableSet.of(square.get_e(), square.get_s(), square.get_se(), square.get_sw(), square.get_w());
        assertEquals(expected, actual);

        square   = othello.getSquare("a8");
        actual   = square.getMooreNeighborhood();
        expected = ImmutableSet.of(square.get_e(), square.get_n(), square.get_ne());
        assertEquals(expected, actual);
    }

    @Test
    public void testSetPiece() {
        Othello.Board board = othello.board;

        assumeThat(board, new OthelloBoardMatcher(board,
                new HashMap<Othello.Color, Set<String>>() {{
                    this.put(Othello.Color.BLACK, ImmutableSet.of("d5", "e4"));
                    this.put(Othello.Color.WHITE, ImmutableSet.of("d4", "e5"));
                }}
        ));

        assertTrue(board.setPiece(board.getSquare("c4"), Othello.Color.BLACK));
        assertThat(board, new OthelloBoardMatcher(board,
                new HashMap<Othello.Color, Set<String>>() {{
                    this.put(Othello.Color.BLACK, ImmutableSet.of("c4", "d4", "e4", "d5"));
                    this.put(Othello.Color.WHITE, ImmutableSet.of("e5"));
                }}
        ));

        assertTrue(board.setPiece(board.getSquare("e3"), Othello.Color.WHTIE));
        assertThat(board, new OthelloBoardMatcher(board,
                new HashMap<Othello.Color, Set<String>>() {{
                    this.put(Othello.Color.BLACK, ImmutableSet.of("c4", "d4", "d5"));
                    this.put(Othello.Color.WHITE, ImmutableSet.of("e3", "e4", "e5"));
                }}
        ));

        // doesn't flip anything, assert false
        assertFalse(board.setPiece(board.getSquare("e6"), Othello.Color.BLACK));
        assertThat(board, new OthelloBoardMatcher(board,
                new HashMap<Othello.Color, Set<String>>() {{
                    this.put(Othello.Color.BLACK, ImmutableSet.of("c4", "d4", "d5"));
                    this.put(Othello.Color.WHITE, ImmutableSet.of("e3", "e4", "e5"));
                }}
        ));
    }

    class OthelloBoardMatcher extends BaseMatcher<Othello.Board> {

        Map<Othello.Board.Square, Othello.Color> pieces;

        public OthelloBoardMatcher(Othello.Board board, Map<Othello.Color, Set<String>> pieceslist) {
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
}
