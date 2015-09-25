package info.jayharris.othello;

import com.google.common.collect.Iterables;

import java.util.Random;
import java.util.Set;

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
        Set<Othello.Board.Square> moves = OthelloUtils.getAllMoves(othello, color);
        return Iterables.get(moves, random.nextInt(moves.size()));
    }
}
