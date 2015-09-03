package info.jayharris.othello;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Random;

public class OthelloPlayerRandomMove extends OthelloPlayer {

    public OthelloPlayerRandomMove(Othello othello, Othello.Color color) {
        super(othello, color);
    }

    @Override
    public Othello.Board.Square getMove() {
        Collection<Othello.Board.Square> moves = othello.getMovesFor(color);

        Preconditions.checkState(!moves.isEmpty());

        return Iterables.get(moves, new Random().nextInt(moves.size()));
    }
}
