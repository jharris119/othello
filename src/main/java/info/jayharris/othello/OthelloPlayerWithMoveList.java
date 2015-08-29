package info.jayharris.othello;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

/**
 * An OthelloPlayer who just plays the next move off a given list.
 * He's useful for testing purposes.
 */
public class OthelloPlayerWithMoveList extends OthelloPlayer {

    Iterator<Othello.Board.Square> iterator;

    public OthelloPlayerWithMoveList(Othello othello, Othello.Color color) {
        this(othello, color, Lists.newLinkedList());
    }

    public OthelloPlayerWithMoveList(Othello othello, Othello.Color color, List<Othello.Board.Square> moves) {
        super(othello, color);
        this.iterator = moves.iterator();
    }

    public void setIterator(Iterator<Othello.Board.Square> iterator) {
        this.iterator = iterator;
    }

    @Override
    public Othello.Board.Square getMove() {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
}
