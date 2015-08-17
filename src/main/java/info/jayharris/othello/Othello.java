package info.jayharris.othello;

import com.google.common.base.Preconditions;
import info.jayharris.othello.strategy.GetMoveKeyboard;
import info.jayharris.othello.strategy.GetMoveStrategy;

import java.util.regex.*;

public class Othello {

    final Board board;
    final OthelloPlayer black, white;
    private OthelloPlayer current;

    public enum Color {
        BLACK, WHITE;

        public Color opposite() {
            return this == Color.BLACK ? Color.WHITE : Color.BLACK;
        }
    }

    public Othello(GetMoveStrategy blackGetMove, GetMoveStrategy whiteGetMove) {
        board = new Board();

        int p = board.SQUARES_PER_SIDE / 2 - 1;
        board.grid[p][p].color = Color.WHITE;
        board.grid[p + 1][p].color = Color.BLACK;
        board.grid[p][p + 1].color = Color.BLACK;
        board.grid[p + 1][p + 1].color = Color.WHITE;

        black = new OthelloPlayer(this, Color.BLACK, blackGetMove);
        white = new OthelloPlayer(this, Color.WHITE, whiteGetMove);
    }

    /**
     * Get the square referred to via algebriac notation.
     *
     * Note that "a1" is the <i>upper</i>-left square, unlike in chess
     * algebriac notation.
     *
     * @param square the square name, in algebriac notation
     * @return the square
     * @throws java.lang.IllegalArgumentException if the algebriac notation
     * is invalid or refers to a non-existent square
     */
    public Board.Square getSquare(String square) {
        return board.getSquare(square);
    }

    public class Board {

        final int SQUARES_PER_SIDE = 8;     // SQUARES_PER_SIDE should be even for symmetry's sake
        final Square[][] grid;

        Board() {
            grid = new Square[SQUARES_PER_SIDE][SQUARES_PER_SIDE];

            for (int rank = 0; rank < SQUARES_PER_SIDE; ++rank) {
                for (int file = 0; file < SQUARES_PER_SIDE; ++file) {
                    grid[rank][file] = new Square(rank, file);
                }
            }
        }

        protected Square getSquare(String square) {
            Pattern pattern = Pattern.compile("^([a-z]+)([1-9]\\d*)$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(square);
            Preconditions.checkArgument(matcher.matches());

            int rank = Integer.valueOf(matcher.group(1), 26) - 10,
                    file = Integer.valueOf(matcher.group(2)) - 1;

            return getSquare(rank, file);
        }

        /**
         * Get the square at {@code (rank, file)}.
         *match
         * @param rank the rank {@code 0 <= rank < SQUARES_PER_SIDE}
         * @param file the file {@code 0 <= file < SQUARES_PER_SIDE}
         * @return the square
         */
        protected Square getSquare(final int rank, final int file) {
            Preconditions.checkArgument(rank >= 0 && rank < SQUARES_PER_SIDE);
            Preconditions.checkArgument(file >= 0 && file < SQUARES_PER_SIDE);

            return grid[rank][file];
        }

        public class Square {
            private final int rank, file;
            private Color color;

            Square(final int rank, final int file) {
                this.rank = rank;
                this.file = file;
                this.color = null;
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int rank = 0; rank < SQUARES_PER_SIDE; ++rank) {
                for (int file = 0; file < SQUARES_PER_SIDE; ++file) {
                    if (grid[rank][file].color == Color.BLACK) {
                        sb.append('B');
                    }
                    else if (grid[rank][file].color == Color.WHITE) {
                        sb.append('w');
                    }
                    else {
                        sb.append('•');
                    }
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    public static void main(String... args) {
        GetMoveStrategy gmsBlack = new GetMoveKeyboard();
        GetMoveStrategy gmsWhite = new GetMoveKeyboard();

        Othello o = new Othello(gmsBlack::getNextMove, gmsWhite::getNextMove);
        System.out.println(o.board);
    }
}
