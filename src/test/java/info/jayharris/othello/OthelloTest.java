package info.jayharris.othello;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class OthelloTest {

    Othello othello;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testNextPly() throws Exception {
        othello = new Othello(OthelloPlayerWithMoveList.class, OthelloPlayerWithMoveList.class);
        String s;

        // f4 is illegal
        Iterator<Othello.Board.Square> moves = Stream.of("c4", "c5", "e6", "f5", "c6", "b5", "f4", "d6", "c3", "a4", "d7").
                map(othello::getSquare).
                iterator();

        OthelloPlayerWithMoveList _black = (OthelloPlayerWithMoveList) othello.black;
        OthelloPlayerWithMoveList _white = (OthelloPlayerWithMoveList) othello.white;
        _black.setIterator(moves);
        _white.setIterator(moves);

        s = "        " +
            "        " +
            "        " +
            "  bbb   " +
            "   bw   " +
            "        " +
            "        " +
            "        " +
            "        ";
        assertEquals(othello.white, othello.nextPly());
        assertThat(othello.board, new OthelloBoardMatcher(othello, s));


        s = "        " +
            "        " +
            "        " +
            "  bbb   " +
            "  www   " +
            "        " +
            "        " +
            "        " +
            "        ";
        assertEquals(othello.black, othello.nextPly());
        assertThat(othello.board, new OthelloBoardMatcher(othello, s));

        s = "        " +
            "        " +
            "        " +
            "  bbb   " +
            "  wbb   " +
            "    b   " +
            "        " +
            "        " +
            "        ";
        assertEquals(othello.white, othello.nextPly());
        assertThat(othello.board, new OthelloBoardMatcher(othello, s));

        s = "        " +
            "        " +
            "        " +
            "  bbb   " +
            "  wwww  " +
            "    b   " +
            "        " +
            "        " +
            "        ";
        assertEquals(othello.black, othello.nextPly());
        assertThat(othello.board, new OthelloBoardMatcher(othello, s));

        s = "        " +
            "        " +
            "        " +
            "  bbb   " +
            "  bbww  " +
            "  b b   " +
            "        " +
            "        " +
            "        ";
        assertEquals(othello.white, othello.nextPly());
        assertThat(othello.board, new OthelloBoardMatcher(othello, s));

        s = "        " +
            "        " +
            "        " +
            "  bbb   " +
            " wwwww  " +
            "  b b   " +
            "        " +
            "        " +
            "        ";
        assertEquals(othello.black, othello.nextPly());
        assertThat(othello.board, new OthelloBoardMatcher(othello, s));

        s = "        " +
            "        " +
            "        " +
            "  bbb   " +
            " wwbww  " +
            "  bbb   " +
            "        " +
            "        " +
            "        ";
        assertEquals(othello.white, othello.nextPly());
        assertThat(othello.board, new OthelloBoardMatcher(othello, s));

        s = "        " +
            "        " +
            "  w     " +
            "  wwb   " +
            " wwbww  " +
            "  bbb   " +
            "        " +
            "        " +
            "        ";
        assertEquals(othello.black, othello.nextPly());
        assertThat(othello.board, new OthelloBoardMatcher(othello, s));

        s = "        " +
                "        " +
                "  w     " +
                "b wwb   " +
                " bwbww  " +
                "  bbb   " +
                "        " +
                "        " +
                "        ";
        assertEquals(othello.white, othello.nextPly());
        assertThat(othello.board, new OthelloBoardMatcher(othello, s));

        s = "        " +
            "        " +
            "  w     " +
            "b wwb   " +
            " bwwww  " +
            "  bww   " +
            "   w    " +
            "        " +
            "        ";
        assertEquals(othello.black, othello.nextPly());
        assertThat(othello.board, new OthelloBoardMatcher(othello, s));
    }
}