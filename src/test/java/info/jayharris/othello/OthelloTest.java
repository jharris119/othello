package info.jayharris.othello;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Set;

import static org.junit.Assert.*;

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
        expected = ImmutableSet.of(square.get_e(),square.get_s(), square.get_se(), square.get_sw(), square.get_w());
        assertEquals(expected, actual);

        square   = othello.getSquare("a8");
        actual   = square.getMooreNeighborhood();
        expected = ImmutableSet.of(square.get_e(),square.get_n(), square.get_ne());
        assertEquals(expected, actual);
    }
}
