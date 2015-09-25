package info.jayharris.othello;

import com.google.common.base.Preconditions;

import java.util.Random;

/**
 * An OthelloPlayer that returns a randomly-selected legal move.
 */
public class OthelloPlayerRandomMove extends OthelloPlayer {

    public final Random random;

    public OthelloPlayerRandomMove(Othello othello, Othello.Color color) {
        super(othello, color);
        random = new Random();
    }

    @Override
    public Othello.Board.Square getMove() {
        return Preconditions.checkNotNull(OthelloUtils.getRandomMove(othello, color, random));
    }
}
