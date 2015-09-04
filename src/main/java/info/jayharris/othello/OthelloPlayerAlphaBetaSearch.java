package info.jayharris.othello;

import com.google.common.collect.ImmutableSet;

public class OthelloPlayerAlphaBetaSearch extends OthelloPlayer {

    public OthelloPlayerAlphaBetaSearch(Othello othello, Othello.Color color) {
        super(othello, color);
    }

    @Override
    public Othello.Board.Square getMove() {
        return null;
    }

    protected double getLikelihoodOfWinning() {
        if (getBoard().hasMovesFor(Othello.Color.WHITE) || getBoard().hasMovesFor(Othello.Color.BLACK)) {
        }
        else {
            int score = getScore();

            if (score == 0) {
                return 0.5;
            }
            else if (score > 0) {
                return color == Othello.Color.WHITE ? 1.0 : 0.0;
            }
            else {
                return color == Othello.Color.BLACK ? 1.0 : 0.0;
            }
        }

        return alphaBetaPruningSearch(0);
    }

    protected double alphaBetaPruningSearch(int depth) {
        return 0.0;
    }

    protected Othello.Board getBoard() {
        return othello.board;
    }

    /**
     * Gets how many more discs this player has than his opponent.
     *
     * @return the count of this player's discs minus his opponent's discs
     */
    public int getScore() {
        return getBoard().countWhiteOverBlack() * (color == Othello.Color.WHITE ? 1 : -1);
    }
}
