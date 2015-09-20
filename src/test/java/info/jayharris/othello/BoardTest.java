package info.jayharris.othello;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class BoardTest {

    Othello othello;
    OthelloBoardBuilder builder;

    @Before
    public void setUp() throws Exception {
        othello = new Othello(OthelloPlayerWithMoveList.class, OthelloPlayerWithMoveList.class);
        builder = new OthelloBoardBuilder(othello);
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
}
