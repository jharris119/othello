package info.jayharris.othello;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import info.jayharris.othello.strategy.GetMoveKeyboard;
import info.jayharris.othello.strategy.GetMoveStrategy;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public final static ImmutableList<Function<Board.Square, Board.Square>> directions = ImmutableList.of(
            Board.Square::get_n, Board.Square::get_ne, Board.Square::get_e, Board.Square::get_se,
            Board.Square::get_s, Board.Square::get_sw, Board.Square::get_w, Board.Square::get_nw
    );

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
     * Gets the square referred to via algebriac notation.
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
                    Square me = grid[rank][file] = new Square(rank, file);

                    if (file > 0) {
                        me._w = grid[rank][file - 1];
                        me._w._e = me;
                    }
                    if (rank > 0) {
                        if (file > 0) {
                            me._nw = grid[rank - 1][file - 1];
                            me._nw._se = me;
                        }
                        me._n = grid[rank - 1][file];
                        me._n._s = me;
                        if (file + 1 < SQUARES_PER_SIDE) {
                            me._ne = grid[rank - 1][file + 1];
                            me._ne._sw = me;
                        }
                    }
                }
            }
        }

        /**
         * If this a legal move, places a disc of the given color on the given
         * square and flips the appropriate discs.
         *
         * @param square the {@link Square}
         * @param color the {@link Color}
         * @return {@code true} iff this is a valid move for the given player
         */
//        protected boolean setPiece(Square square, Color color) {
//            boolean legal = false;
//
//            if (square.getColor() != null) {
//                return false;
//            }
//
//            for (deltarank = -1; deltarank <= 1; ++deltarank) {
//                for (deltafile = -1; deltafile <= 1; ++deltafile) {
//                    if (deltarank == 0 && deltafile == 0) {
//                        continue;
//                    }
//
//                    if (flipLine(square, color, deltarank, deltafile)) {
//                        legal = true;
//                    }
//                }
//            }
//
//            return legal;
//        }

        public boolean flipLine(Square square, Color color, )

        protected Square getSquare(String square) {
            Pattern pattern = Pattern.compile("^([a-z])([1-9]\\d*)$");
            Matcher matcher = pattern.matcher(square.toLowerCase());
            Preconditions.checkArgument(matcher.matches());

            int rank = matcher.group(1).charAt(0) - 'a',
                    file = Integer.valueOf(matcher.group(2)) - 1;

            return getSquare(rank, file);
        }

        /**
         * Gets the square at {@code (rank, file)}.
         *
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

            private Square _n, _ne, _e, _se, _s, _sw, _w, _nw;
            private Set<Square> neighbors = null;

            Square(final int rank, final int file) {
                this.rank = rank;
                this.file = file;
                this.color = null;
            }

            /**
             * Gets the {@link Square}s in this {@code Square}'s Moore neighborhood.
             *
             * @return a set of {@code Square}s adjacent to this
             */
            public Set<Square> getMooreNeighborhood() {
                if (neighbors == null) {
                    neighbors = Sets.newHashSet(_n, _ne, _e, _se, _s, _sw, _w, _nw);
                    neighbors.removeIf(Objects::isNull);
                }
                return neighbors;
            }

            /* ****************************************************************
             * Reader methods
             * ****************************************************************/
            public int getRank() {
                return rank;
            }

            public int getFile() {
                return file;
            }

            public Color getColor() {
                return color;
            }

            public Square get_n() {
                return _n;
            }

            public Square get_ne() {
                return _ne;
            }

            public Square get_e() {
                return _e;
            }

            public Square get_se() {
                return _se;
            }

            public Square get_s() {
                return _s;
            }

            public Square get_sw() {
                return _sw;
            }

            public Square get_w() {
                return _w;
            }

            public Square get_nw() {
                return _nw;
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
