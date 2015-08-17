package info.jayharris.othello.player;

import info.jayharris.othello.Othello;
import info.jayharris.othello.OthelloPlayer;

public abstract class ComputerPlayer extends OthelloPlayer {
    public ComputerPlayer(Othello othello, Othello.Color color) {
        super(othello, color);
    }
}
