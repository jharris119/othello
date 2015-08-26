package info.jayharris.othello;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

public class OthelloPlayerWithMoveList extends OthelloPlayer {

    List<Othello.Board.Square> moves;
    Iterator<Othello.Board.Square> iterator;

    public OthelloPlayerWithMoveList(Othello othello, Othello.Color color) {
        this(othello, color, Lists.newLinkedList());
    }

    public OthelloPlayerWithMoveList(Othello othello, Othello.Color color, List<Othello.Board.Square> moves) {
        super(othello, color);
        this.moves = moves;
        this.iterator = moves.iterator();
    }

    @Override
    public Othello.Board.Square getMove() {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
}
