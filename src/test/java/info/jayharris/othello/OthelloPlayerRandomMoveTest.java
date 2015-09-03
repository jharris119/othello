package info.jayharris.othello;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class OthelloPlayerRandomMoveTest {

    Field currentField;

    public OthelloPlayerRandomMoveTest() throws Exception {
        currentField = Othello.class.getDeclaredField("current");
        currentField.setAccessible(true);
    }

    @Test
    public void testGetMove() throws Exception {
        Othello othello = new Othello(OthelloPlayerRandomMove.class, OthelloPlayerRandomMove.class);

        for (int i = 0; i < 10; ++i) {
            OthelloPlayer p = othello.getCurrentPlayer();
            Othello.Board.Square s = p.getMove();

            assertTrue(othello.board.setPiece(s, p.color));

            // switch player -- let's assume at this point that we can just switch players
            currentField.set(othello, p == othello.black ? othello.white : othello.black);
        }
    }
}