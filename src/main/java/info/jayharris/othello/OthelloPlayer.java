package info.jayharris.othello;

public abstract class OthelloPlayer {

    public final Othello othello;
    public final Othello.Color color;

    public OthelloPlayer(Othello othello, Othello.Color color) {
        this.othello = othello;
        this.color = color;
    }

    public abstract Othello.Board.Square getMove();
}
