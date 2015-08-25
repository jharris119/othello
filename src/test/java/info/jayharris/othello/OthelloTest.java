package info.jayharris.othello;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static info.jayharris.othello.Othello.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;

public class OthelloTest {

    Othello othello;

    static Field boardField;
    static Method squareSetPieceMethod;

    public OthelloTest() throws Exception {
        boardField = Othello.class.getDeclaredField("board");
        boardField.setAccessible(true);

        squareSetPieceMethod = Othello.Board.Square.class.getDeclaredMethod("setPiece", Color.class);
        squareSetPieceMethod.setAccessible(true);
    }

    @Before
    public void setUp() {
        othello = new Othello();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetSquare() {
        assertSame(othello.board.grid[0][0], othello.board.getSquare("a1"));
        assertSame(othello.board.grid[6][4], othello.board.getSquare("E7"));
        assertSame(othello.board.grid[7][7], othello.board.getSquare("h8"));

        thrown.expect(IllegalArgumentException.class);
        othello.board.getSquare("");
        othello.board.getSquare("abc");
        othello.board.getSquare("5h");
        othello.board.getSquare("d06");
    }

    @Test
    public void testIsLegalMoveForColor() throws Exception {
        String s;
        Othello.Board.Square square;

        s = "       b" +
            "       b" +
            " wbbb  b" +
            " w     b" +
            "  w     " +
            "   w    " +
            "    w   " +
            "     b  ";
        Othello.Board b = OthelloBoardBuilder.build(othello, s);
        boardField.set(othello, OthelloBoardBuilder.build(othello, s));

        square = othello.getSquare("f3");
        assertTrue(othello.board.isLegalMoveForColor(square, Color.WHITE));
        assertFalse(othello.board.isLegalMoveForColor(square, Color.BLACK));

        square = othello.getSquare("a3");
        assertTrue(othello.board.isLegalMoveForColor(square, Color.BLACK));
        assertFalse(othello.board.isLegalMoveForColor(square, Color.WHITE));

        square = othello.getSquare("h5");
        assertFalse(othello.board.isLegalMoveForColor(square, Color.BLACK));
        assertFalse(othello.board.isLegalMoveForColor(square, Color.WHITE));
    }

    @Test
    public void testGetMooreNeighborhood() throws Exception {
        Othello.Board.Square square;
        Set<Othello.Board.Square> expected, actual;

        square = othello.getSquare("f5");
        actual = square.getMooreNeighborhood();
        expected = ImmutableSet.of(square.get_e(), square.get_n(), square.get_ne(), square.get_nw(),
                square.get_s(), square.get_se(), square.get_sw(), square.get_w());
        assertEquals(expected, actual);

        square = othello.getSquare("b1");
        actual = square.getMooreNeighborhood();
        expected = ImmutableSet.of(square.get_e(), square.get_s(), square.get_se(), square.get_sw(), square.get_w());
        assertEquals(expected, actual);

        square = othello.getSquare("a8");
        actual = square.getMooreNeighborhood();
        expected = ImmutableSet.of(square.get_e(), square.get_n(), square.get_ne());
        assertEquals(expected, actual);
    }

    @Test
    public void testSetPiece() {
        Othello.Board board = othello.board;

        assumeThat(board, new OthelloBoardMatcher(new HashMap<Othello.Color, Set<String>>() {{
            this.put(Color.BLACK, ImmutableSet.of("d5", "e4"));
            this.put(Color.WHITE, ImmutableSet.of("d4", "e5"));
        }}));

        assertTrue(board.setPiece(board.getSquare("c4"), Color.BLACK));
        assertThat(board, new OthelloBoardMatcher(new HashMap<Othello.Color, Set<String>>() {{
            this.put(Color.BLACK, ImmutableSet.of("c4", "d4", "e4", "d5"));
            this.put(Color.WHITE, ImmutableSet.of("e5"));
        }}));

        assertTrue(board.setPiece(board.getSquare("e3"), Color.WHITE));
        assertThat(board, new OthelloBoardMatcher(new HashMap<Othello.Color, Set<String>>() {{
            this.put(Color.BLACK, ImmutableSet.of("c4", "d4", "d5"));
            this.put(Color.WHITE, ImmutableSet.of("e3", "e4", "e5"));
        }}));

        // TODO: more complicated board configurations

        // doesn't flip anything, assert false
        assertFalse(board.setPiece(board.getSquare("e6"), Color.BLACK));
        assertThat(board, new OthelloBoardMatcher(new HashMap<Othello.Color, Set<String>>() {{
            this.put(Color.BLACK, ImmutableSet.of("c4", "d4", "d5"));
            this.put(Color.WHITE, ImmutableSet.of("e3", "e4", "e5"));
        }}));
    }

    @Test
    public void testGetMovesFor() throws Exception {
        Set<Othello.Board.Square> expected;

        expected = Sets.newHashSet("d3", "c4", "f6", "e5").stream().map(othello::getSquare).collect(Collectors.toSet());
        assertEquals(expected, othello.getMovesFor(Color.BLACK));
    }

    @Test
    public void testIsGameOver() throws Exception {
        String s;

        s = "       b" +
            "w w  w b" +
            "wwwwwwbb" +
            "wbwwwwbb" +
            "wbwwbwbb" +
            "wwwbwbbb" +
            "  wbbbbb" +
            "  wwbbbb";
        boardField.set(othello, OthelloBoardBuilder.build(othello, s));
        assertFalse(othello.isGameOver());

        s = "w bbbbbb" +
            "wwbbbbbb" +
            "wwwbbbbb" +
            "wwbwbbbb" +
            "wwwbwbbb" +
            "wwbwbwbb" +
            "wwbbwbwb" +
            "wwwwwwww";
        boardField.set(othello, OthelloBoardBuilder.build(othello, s));
        assertTrue(othello.isGameOver());

        s = "bbbbbbbb" +
            "bwwwwwbb" +
            "wbbwbbwb" +
            "wbbbbwbb" +
            "wbwbbwbb" +
            "wwwwbbwb" +
            "wwwwwbbb" +
            "wbbbbbbb";
        boardField.set(othello, OthelloBoardBuilder.build(othello, s));
        assertTrue(othello.isGameOver());
    }

    static class OthelloBoardBuilder {
        public static Othello.Board build(Othello othello, String str) throws Exception {
            Othello.Board board = othello.new Board();

            int rank = 0, file = 0;
            for (char c : str.toCharArray()) {
                Color color = null;
                if (c == 'b' || c == 'B') {
                    color = Color.BLACK;
                }
                else if (c == 'w' || c == 'W') {
                    color = Color.WHITE;
                }
                squareSetPieceMethod.invoke(board.getSquare(rank, file), color);

                file = (file + 1) % board.SQUARES_PER_SIDE;
                if (file == 0) {
                    ++rank;
                }
            }
            return board;
        }
    }

    class OthelloBoardMatcher extends BaseMatcher<Othello.Board> {

        Map<Othello.Board.Square, Othello.Color> pieces;

        public OthelloBoardMatcher(Map<Othello.Color, Set<String>> pieceslist) {
            pieces = Maps.newHashMap();
            for (String str : pieceslist.get(Color.BLACK)) {
                pieces.put(othello.getSquare(str), Color.BLACK);
            }
            for (String str : pieceslist.get(Color.WHITE)) {
                pieces.put(othello.getSquare(str), Color.WHITE);
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
