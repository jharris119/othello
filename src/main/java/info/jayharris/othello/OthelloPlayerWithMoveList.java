package info.jayharris.othello;

import java.util.Queue;

public class OthelloPlayerWithMoveList extends OthelloPlayer {

    Queue<Othello.Board.Square> moves;

    public OthelloPlayerWithMoveList(Othello othello, Othello.Color color, Queue<Othello.Board.Square> moves) {
        super(othello, color);
        this.moves = moves;
    }

    @Override
    public Othello.Board.Square getMove() {
        return moves.poll();
    }
}
