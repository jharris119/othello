package info.jayharris.othello;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.util.Iterator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OthelloTest {

    Othello othello;

    static Field colorField;

    public OthelloTest() throws Exception {
        colorField = OthelloPlayer.class.getDeclaredField("color");
        colorField.setAccessible(true);
    }

    @Before
    public void setUp() throws Exception {
        OthelloPlayer black = mock(OthelloPlayer.class), white = mock(OthelloPlayer.class);
        Answer nextMove = new Answer<Othello.Board.Square>() {
            Iterator<String> iter = ImmutableList.
                    of("c4", "c5", "e6", "f5", "c6", "b5", "f4", "d6", "c3", "a4", "d7").
                    iterator();

            public Othello.Board.Square answer(InvocationOnMock mock) {
                return othello.getSquare(iter.next());
            }
        };

        colorField.set(black, Othello.Color.BLACK);
        colorField.set(white, Othello.Color.WHITE);
        when(black.getMove()).thenAnswer(nextMove);
        when(white.getMove()).thenAnswer(nextMove);

        othello = new Othello(black, white);
    }

    @Test
    public void testNextPly() throws Exception {
        String s;
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