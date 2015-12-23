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

    @Test
    public void testGetRandomMove() throws Exception {
        Othello.Board board;

        String s;
        s = "        " +
            "    b   " +
            "   bb   " +
            "  wwwb  " +
            " bbwwbww" +
            "   bbb  " +
            "    b   " +
            "     b  ";
        board = builder.build(s);

        Othello.Color c;
        for (int i = 0; i < 10; ++i) {
            c = (i % 2 == 0 ? Othello.Color.WHITE : Othello.Color.BLACK);
            assertTrue(board.isLegal(OthelloUtils.getRandomMove(board, c), c));
        }
    }

    @Test
    public void testGetStableDiscs() throws Exception {
        Othello.Board board;
        Set<Othello.Board.Square> expected;

        String s;
        s = "        " +
            "        " +
            "        " +
            "   wb   " +
            "   bw   " +
            "    bwb " +
            "      w " +
            "      bw";
        board = builder.build(s);
        expected = Stream.of("h8").map(board::getSquare).collect(Collectors.toSet());
        assertEquals(expected, OthelloUtils.getStableDiscsFromCorner(board.getSquare("h8")));

//        s = "wwwb    " +
//            "  b     " +
//            " bwb    " +
//            " bbbb   " +
//            "  www   " +
//            "        " +
//            "        " +
//            "        ";
//        board = builder.build(s);
//        expected = Stream.of("a1", "b1", "c1").map(board::getSquare).collect(Collectors.toSet());
//        assertEquals(expected, OthelloUtils.getStableDiscsFromCorner(board.getSquare("a1")));

//        s = "   www  " +
//            "  bwwb b" +
//            "wbwwwwbb" +
//            "wwwwbwbb" +
//            "wwwbwwbb" +
//            "wwwwwbbb" +
//            "  bbbbbb" +
//            " bbbbbbb";
//        board = builder.build(s);
//
//        expected = Stream.of("h2", "g3", "h3", "g4", "h4", "g5", "h5", "d6", "f6", "g6", "h6", "c7", "d7", "e7", "f7", "g7", "h7", "b8", "c8", "d8", "e8", "f8", "g8", "h8").
//                map(board::getSquare).
//                collect(Collectors.toSet());
//        assertEquals(expected, OthelloUtils.getStableDiscs(board));
    }
}