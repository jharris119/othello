package info.jayharris.othello;

public class OthelloPlayerArbitraryMove extends OthelloPlayer {

    public OthelloPlayerArbitraryMove(Othello othello, Othello.Color color) { super(othello, color); }

    @Override
    public Othello.Board.Square getMove() {
        return othello.getMovesFor(this).iterator().next();
    }
}
