package info.jayharris.othello.strategy;

import info.jayharris.othello.Othello;
import info.jayharris.othello.OthelloPlayer;

@FunctionalInterface
public interface GetMoveStrategy {
    Othello.Board.Square getNextMove(final OthelloPlayer player);
}
