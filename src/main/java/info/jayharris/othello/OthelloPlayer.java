package info.jayharris.othello;

import info.jayharris.othello.strategy.GetMoveStrategy;

public class OthelloPlayer {

    public final Othello othello;
    public final Othello.Color color;
    public final GetMoveStrategy getMove;

    // TODO: wrap GetMoveStrategy directly in the Player object
    public OthelloPlayer(Othello othello, Othello.Color color, GetMoveStrategy getMove) {
        this.othello = othello;
        this.color = color;
        this.getMove = getMove;
    }
}