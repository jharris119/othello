package info.jayharris.othello;

import com.google.common.collect.ImmutableSet;
import static org.junit.Assume.*;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class BoardTest {

    Othello othello;
    OthelloBoardBuilder builder;

    @Before
    public void setUp() throws Exception {
        OthelloPlayer black = mock(OthelloPlayer.class), white = mock(OthelloPlayer.class);

        othello = new Othello(black, white);
        builder = new OthelloBoardBuilder(othello);
    }

    @Test
    public void testSetPiece() {
        Othello.Board board = othello.board;

        assumeThat(board, new OthelloBoardMatcher(othello, new HashMap<Othello.Color, Set<String>>() {{
            this.put(Othello.Color.BLACK, ImmutableSet.of("d5", "e4"));
            this.put(Othello.Color.WHITE, ImmutableSet.of("d4", "e5"));
        }}));
        assumeThat(squares(board, "d4", "d5", "e4", "e5"), CoreMatchers.is(board.getOccupied()));
        assumeThat(squares(board, "d4", "d5", "e4", "e5"), CoreMatchers.is(board.getFrontier()));
        assumeThat(squares(board, "c3", "c4", "c5", "c6", "d3", "d6", "e3", "e6", "f3", "f4", "f5", "f6"),
                CoreMatchers.is(board.getAccessible()));

        assertTrue(board.setPiece(board.getSquare("c4"), Othello.Color.BLACK));
        assertThat(board, new OthelloBoardMatcher(othello, new HashMap<Othello.Color, Set<String>>() {{
            this.put(Othello.Color.BLACK, ImmutableSet.of("c4", "d4", "e4", "d5"));
            this.put(Othello.Color.WHITE, ImmutableSet.of("e5"));
        }}));
        assertThat(squares(board, "c4", "d4", "d5", "e4", "e5"), CoreMatchers.is(board.getOccupied()));
        assertThat(squares(board, "c4", "d4", "d5", "e4", "e5"), CoreMatchers.is(board.getFrontier()));
        assertThat(squares(board, "b3", "b4", "b5", "c3", "c5", "c6", "d3", "d6", "e3", "e6", "f3", "f4", "f5", "f6"),
                CoreMatchers.is(board.getAccessible()));

        assertTrue(board.setPiece(board.getSquare("e3"), Othello.Color.WHITE));
        assertThat(board, new OthelloBoardMatcher(othello, new HashMap<Othello.Color, Set<String>>() {{
            this.put(Othello.Color.BLACK, ImmutableSet.of("c4", "d4", "d5"));
            this.put(Othello.Color.WHITE, ImmutableSet.of("e3", "e4", "e5"));
        }}));
        assertThat(squares(board, "c4", "d4", "d5", "e3", "e4", "e5"), CoreMatchers.is(board.getOccupied()));
        assertThat(squares(board, "c4", "d4", "d5", "e3", "e4", "e5"), CoreMatchers.is(board.getFrontier()));
        assertThat(squares(board, "b3", "b4", "b5", "c3", "c5", "c6", "d2", "d3", "d6", "e2", "e6", "f2", "f3", "f4", "f5", "f6"),
                CoreMatchers.is(board.getAccessible()));

        // TODO: more complicated board configurations

        // TODO: a configuration where !board.getOccupied().equals(board.getFrontier())

        // doesn't flip anything, assert false
        assertFalse(board.setPiece(board.getSquare("e6"), Othello.Color.BLACK));
        assertThat(board, new OthelloBoardMatcher(othello, new HashMap<Othello.Color, Set<String>>() {{
            this.put(Othello.Color.BLACK, ImmutableSet.of("c4", "d4", "d5"));
            this.put(Othello.Color.WHITE, ImmutableSet.of("e3", "e4", "e5"));
        }}));
    }

    @Test
    public void testIsLegal() throws Exception {
        String s;
        Othello.Board board;

        s = "       b" +
            "       b" +
            " wbbb  b" +
            " w     b" +
            "  w     " +
            "   w    " +
            "    w   " +
            "     b  ";
        board = builder.build(s);

        assertTrue(board.isLegal(board.getSquare("f3"), Othello.Color.WHITE));
        assertFalse(board.isLegal(board.getSquare("f3"), Othello.Color.BLACK));

        assertTrue(board.isLegal(board.getSquare("a3"), Othello.Color.BLACK));
        assertFalse(board.isLegal(board.getSquare("a3"), Othello.Color.WHITE));

        assertFalse(board.isLegal(board.getSquare("h5"), Othello.Color.WHITE));
        assertFalse(board.isLegal(board.getSquare("h5"), Othello.Color.BLACK));
    }

    @Test
    public void testGetSquaresToFlip_Square_Color() throws Exception {
        String s;
        Othello.Board board;
        Set<Othello.Board.Square> expected, actual;

        s = "       b" +
            "       b" +
            " wbbb  b" +
            " w     b" +
            "  w     " +
            "   w    " +
            "    w   " +
            "     b  ";
        board = builder.build(s);

        expected = Stream.of("b3", "b4", "c5", "d6", "e7").map(othello::getSquare).collect(Collectors.toSet());
        actual = board.getSquaresToFlip(board.getSquare("a3"), Othello.Color.BLACK);
        assertEquals(expected, actual);

        expected = Collections.emptySet();
        actual = board.getSquaresToFlip(board.getSquare("g2"), Othello.Color.WHITE);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetSquaresToFlip_Square_Color_Direction() throws Exception {
        String s;
        Othello.Board board;
        Set<Othello.Board.Square> expected, actual;

        s = "       b" +
            "       b" +
            " wbbb  b" +
            " w     b" +
            "  w     " +
            "   w    " +
            "    w   " +
            "     b  ";
        board = builder.build(s);

        expected = Stream.of("b3").map(othello::getSquare).collect(Collectors.toSet());
        actual = board.getSquaresToFlip(board.getSquare("a3"), Othello.Color.BLACK, Othello.Board.Square::get_e);
        assertEquals(expected, actual);

        expected = Collections.emptySet();
        actual = board.getSquaresToFlip(board.getSquare("a3"), Othello.Color.WHITE, Othello.Board.Square::get_se);
        assertEquals(expected, actual);

        expected = Stream.of("b4", "c5", "d6", "e7").map(othello::getSquare).collect(Collectors.toSet());
        actual = board.getSquaresToFlip(board.getSquare("a3"), Othello.Color.BLACK, Othello.Board.Square::get_se);
        assertEquals(expected, actual);

        expected = Collections.emptySet();
        actual = board.getSquaresToFlip(board.getSquare("g1"), Othello.Color.WHITE, Othello.Board.Square::get_n);
        assertEquals(expected, actual);

        expected = Collections.emptySet();
        actual = board.getSquaresToFlip(board.getSquare("h5"), Othello.Color.WHITE, Othello.Board.Square::get_n);
        assertEquals(expected, actual);
    }

    private Set<Othello.Board.Square> squares(Othello.Board board, String... squares) {
        return Stream.of(squares).map(board::getSquare).collect(Collectors.toSet());
    }
}
