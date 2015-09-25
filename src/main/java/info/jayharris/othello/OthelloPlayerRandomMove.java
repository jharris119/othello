package info.jayharris.othello;

import com.google.common.base.Preconditions;

/**
 * An OthelloPlayer that returns a randomly-selected legal move.
 */
public class OthelloPlayerRandomMove extends OthelloPlayer {

    public OthelloPlayerRandomMove(Othello othello, Othello.Color color) {
        super(othello, color);
    }

    @Override
    public Othello.Board.Square getMove() {
        return Preconditions.checkNotNull(OthelloUtils.getRandomMove(othello, color));
    }
}
