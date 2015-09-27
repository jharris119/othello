package info.jayharris.othello;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OthelloUtilsTest {

    Othello othello;
    OthelloBoardBuilder builder;

    @Before
    public void setUp() throws Exception {
        OthelloPlayer black = mock(OthelloPlayer.class), white = mock(OthelloPlayer.class);

        othello = new Othello(black, white);
        builder = new OthelloBoardBuilder(othello);
    }

    @Test
    public void testGetAllMoves() throws Exception {
        Othello.Board board;
        Set<Othello.Board.Square> expected;

        String s;
        s = "        " +
            "        " +
            "  b  b  " +
            "   bbb  " +
            "   wwww " +
            "   w    " +
            "        " +
            "        ";

        board = builder.build(s);

        expected = Stream.of("c6", "e6", "f6", "g6", "h6", "c7", "d7").
                map(board::getSquare).
                collect(Collectors.toSet());
        assertEquals(expected, OthelloUtils.getAllMoves(board, Othello.Color.BLACK));

        s = "    w   " +
            "   bwww " +
            "  wbbwwb" +
            "  wbbww " +
            "  wwww  " +
            "        " +
            "        " +
            "        ";
        board = builder.build(s);

        expected = Stream.of("f1", "g1", "h1", "b2", "h2", "b3", "b4", "h4", "b5", "g5", "b6", "c6", "d6", "e6", "f6", "g6").
                map(board::getSquare).
                collect(Collectors.toSet());
        assertEquals(expected, OthelloUtils.getAllMoves(board, Othello.Color.BLACK));
    }
}