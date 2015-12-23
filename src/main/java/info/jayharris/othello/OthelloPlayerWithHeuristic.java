package info.jayharris.othello;

import java.util.Set;

public class OthelloPlayerWithHeuristic extends OthelloPlayer {

    public OthelloPlayerWithHeuristic(Othello othello, Othello.Color color) {
        super(othello, color);
    }

    @Override
    public Othello.Board.Square getMove() {
        // collect all of the legal moves

        // apply the heuristic to each of them

        // choose the move with the greatest probability of winning

        return null;
    }

    private double heuristic(Othello.Board board) {
        Othello.Color winner;

        if (OthelloUtils.isGameOver(board)) {
            return (winner = OthelloUtils.winner(board)) == null ? 0.5 : (winner == color ? 1.0 : 0.0);
        }

        else {
            // return calculateHeuristicValue(board);
        }

        return 0.0;
    }

    private int calculateHeuristicValue(Othello.Board board) {
        int liberties = OthelloUtils.getAllMoves(board, this.color.opposite()).size();

        Set<Othello.Board.Square> stableDiscs = OthelloUtils.getStableDiscs(board);

        return 0;
    }
}
