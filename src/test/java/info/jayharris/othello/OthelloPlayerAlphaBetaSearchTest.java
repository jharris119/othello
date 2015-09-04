package info.jayharris.othello;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class OthelloPlayerAlphaBetaSearchTest {

    Othello othello;

    static Field boardField, othelloField;

    public OthelloPlayerAlphaBetaSearchTest() throws Exception {
        boardField = Othello.class.getDeclaredField("board");
        boardField.setAccessible(true);

        othelloField = OthelloPlayer.class.getDeclaredField("othello");
        othelloField.setAccessible(true);
    }

    @Test
    public void testGetLikelihoodOfWinning_gameOver() throws Exception {
        othello = new Othello(OthelloPlayerAlphaBetaSearch.class, OthelloPlayerAlphaBetaSearch.class);
        boardField.set(othello, OthelloBoardBuilder.buildReal());

        OthelloPlayerAlphaBetaSearch white = getWhite(othello), black = getBlack(othello);

        int count = white.getScore();
        if (count > 0) {
            assertEquals(1.0, white.getLikelihoodOfWinning(), 0.0);
            assertEquals(0.0, black.getLikelihoodOfWinning(), 0.0);
        }
        else if (count < 0) {
            assertEquals(0.0, white.getLikelihoodOfWinning(), 0.0);
            assertEquals(1.0, black.getLikelihoodOfWinning(), 0.0);
        }
        else {
            assertEquals(0.5, white.getLikelihoodOfWinning(), 0.0);
            assertEquals(0.5, black.getLikelihoodOfWinning(), 0.0);
        }
    }

    @Test
    public void testGetScore() throws Exception {
        othello = new Othello(OthelloPlayerAlphaBetaSearch.class, OthelloPlayerAlphaBetaSearch.class);

        StringBuilder sb = new StringBuilder();
        int whitecount = 0, blackcount = 0;
        for (int i = 0; i < Math.pow(othello.board.SQUARES_PER_SIDE, 2); ++i) {
            if (Math.random() < 0.01) {
                sb.append(' ');
            }
            else if (Math.random() < 0.5) {
                sb.append('b');
                ++blackcount;
            }
            else {
                sb.append('w');
                ++whitecount;
            }
        }

        boardField.set(othello, OthelloBoardBuilder.build(othello, sb.toString()));
//        othelloField.set(othello.white, othello);
//        othelloField.set(othello.black, othello);

        assertEquals(whitecount - blackcount, getWhite(othello).getScore());
        assertEquals(blackcount - whitecount, getBlack(othello).getScore());
    }

    private OthelloPlayerAlphaBetaSearch getWhite(Othello othello) {
        return (OthelloPlayerAlphaBetaSearch) othello.white;
    }

    private OthelloPlayerAlphaBetaSearch getBlack(Othello othello) {
        return (OthelloPlayerAlphaBetaSearch) othello.black;
    }
}