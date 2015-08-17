package info.jayharris.othello.player;

import info.jayharris.othello.Othello;

public class ArbitrarySquarePlayer extends ComputerPlayer {

    public ArbitrarySquarePlayer(Othello othello, Othello.Color color) {
        super(othello, color);
    }

    @Override
    public Othello.Board.Square getNextMove() {
        return null;
    }
}
