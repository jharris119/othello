package info.jayharris.othello;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class OthelloTest {

    Othello othello;

    @Before
    public void setUp() {
        othello = new Othello();
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
}
