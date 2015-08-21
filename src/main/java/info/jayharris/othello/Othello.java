package info.jayharris.othello;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import info.jayharris.othello.strategy.GetMoveKeyboard;
import info.jayharris.othello.strategy.GetMoveStrategy;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
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
         * Puts a disc of the given color on this square, if legal, and flip
         * the necessary discs.
         *
         * @param square the square
         * @param color the color
         * @return {@code true} iff this move is legal
         */
        public boolean setPiece(Square square, Color color) {
            Preconditions.checkArgument(square.getColor() == null);
            directions.forEach((direction) -> {
                if (flipDiscsInDirection(square, color, direction)) {
                    square.setPiece(color);
                }
            });
            return square.getColor() != null;
        }

        /**
         * Flips all the opposite-colored discs in a given direction.
         *
         * More precisely, given {@code start} and {@code color}, finds the
         * first {@code color}-ed disc <var>D</var> in a {@code direction}-ward
         * line from {@code start}. If <var>D</var> exists, flip all the discs
         * between {@code start} and <var>D</var>, exclusive.
         *
         * @param start the start square
         * @param color the color
         * @param direction the direction
         * @return {@code true} iff discs were flipped
         */
        protected boolean flipDiscsInDirection(Square start, Color color, Function<Square, Square> direction) {
            Square current = direction.apply(start);

            LinkedList<Square> toFlip = Lists.newLinkedList();
            while (current != null && current.getColor() == color.opposite()) {
                toFlip.add(current);
                current = direction.apply(current);
            }

            if (current == null || current.getColor() != color || toFlip.isEmpty()) {
                return false;
            }
            toFlip.forEach(Square::flip);
            return true;
        }

        protected Square getSquare(String square) {
            Pattern pattern = Pattern.compile("^([a-z])([1-9]\\d*)$");
            Matcher matcher = pattern.matcher(square.toLowerCase());
            Preconditions.checkArgument(matcher.matches());

            int file = matcher.group(1).charAt(0) - 'a',
                    rank = Integer.valueOf(matcher.group(2)) - 1;

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
             * Flips the disc in this square, if there is a disc in this square.
             *
             * @return the new disc color, or {@code null} if this square is empty
             */
            private Color tryFlip() {
                try {
                    return flip();
                }
                catch(IllegalStateException e) {
                    return null;
                }
            }

            private Color flip() throws IllegalStateException {
                Preconditions.checkNotNull(this.color);
                return this.color = color.opposite();
            }

            /**
             * Puts a disc with the given color on this square, without regard
             * to whether such a play is legal.
             *
             * @param color the color
             */
            private void setPiece(Color color) {
                this.color = color;
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
