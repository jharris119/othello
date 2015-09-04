package info.jayharris.othello;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Othello {

    final Board board;
    final OthelloPlayer black, white;
    private OthelloPlayer current;

    private final Set<Board.Square> occupied,
                                    fringeAdjacent;     // the fringe is the set of discs bounded by at least one
                                                        // empty square — this is a set of those empty squares

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

    public Othello() {
        this(OthelloPlayerWithKeyboard.class, OthelloPlayerWithKeyboard.class);
    }

    public Othello(Class<? extends OthelloPlayer> blacktype, Class<? extends OthelloPlayer> whitetype) {
        board = new Board();

        int p = board.SQUARES_PER_SIDE / 2 - 1;
        board.grid[p][p].setPiece(Color.WHITE);
        board.grid[p + 1][p].setPiece(Color.BLACK);
        board.grid[p][p + 1].setPiece(Color.BLACK);
        board.grid[p + 1][p + 1].setPiece(Color.WHITE);

        occupied = new HashSet<Board.Square>() {{
            this.add(board.grid[p][p]);
            this.add(board.grid[p + 1][p]);
            this.add(board.grid[p][p + 1]);
            this.add(board.grid[p + 1][p + 1]);
        }};

        black = this.buildPlayer(blacktype, Color.BLACK);
        white = this.buildPlayer(whitetype, Color.WHITE);
        current = this.black;

        fringeAdjacent = new HashSet<Board.Square>() {{
            occupied.forEach((square) -> this.addAll(square.getMooreNeighborhood()));
        }};
        fringeAdjacent.removeIf(Board.Square::isOccupied);
    }

    /**
     * Plays the game.
     *
     * @return the winner, or {@code null} if a tie
     */
    public OthelloPlayer play() {
        while (nextPly()) {
            // noop
        };

        board.noMoreMoves = true;
        int count = board.countWhiteOverBlack();
        return count == 0 ? null : count > 0 ? white : black;
    }

    /**
     * Play the next ply of the game, then set the current player to the
     * correct player.
     *
     * @return false iff the game is over
     */
    protected boolean nextPly() {
        Preconditions.checkState(hasMovesFor(current));

        Board.Square move;
        do {
            move = current.getMove();
        } while (!board.setPiece(move, current.color));

        current = (current == white ? black : white);
        if (hasMovesFor(current)) {
            return true;
        }
        current = (current == white ? black : white);
        return hasMovesFor(current);
    }

    /**
     * Gets the current player.
     *
     * @return the current player
     */
    public OthelloPlayer getCurrentPlayer() {
        return current;
    }

    protected Set<Board.Square> getMovesFor(OthelloPlayer player) {
        Preconditions.checkArgument(player.othello == this);

        return board.getMovesFor(player.color);
    }

    protected boolean hasMovesFor(OthelloPlayer player) {
        Preconditions.checkArgument(player.othello == this);

        return board.hasMovesFor(player.color);
    }

    public boolean isGameOver() {
        return board.noMoreMoves;
    }

    /**
     * Counts how many more white pieces than black pieces there are on the
     * board.
     *
     * @return the number of white pieces on the board minus the number of
     * black pieces on the board
     */
    public int countWhiteOverBlack() {
        int count = 0;
        for (int rank = 0; rank < board.SQUARES_PER_SIDE; ++rank) {
            for (int file = 0; file < board.SQUARES_PER_SIDE; ++file) {
                Color color = board.getSquare(rank, file).getColor();
                if (color == Color.WHITE) {
                    ++count;
                }
                else if (color == Color.BLACK) {
                    --count;
                }
            }
        }

        return count;
    }

    private OthelloPlayer buildPlayer(Class<? extends OthelloPlayer> type, Color color) {
        try {
            Constructor ctor = type.getConstructor(Othello.class, Othello.Color.class);
            return (OthelloPlayer) ctor.newInstance(this, color);
        }
        catch(NoSuchMethodException|InvocationTargetException|InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }
        return new OthelloPlayerWithKeyboard(this, color);
    }

    /**
     * Gets the square referred to via algebraic notation.
     *
     * Note that "a1" is the <i>upper</i>-left square, unlike in chess
     * algebriac notation.
     *
     * @param square the square name, in algebriac notation
     * @return the square
     * @throws java.lang.IllegalArgumentException if the algebraic notation
     * is invalid or refers to a non-existent square
     */
    public Board.Square getSquare(String square) {
        return board.getSquare(square);
    }

    public class Board {

        final int SQUARES_PER_SIDE = 8;     // SQUARES_PER_SIDE should be even for symmetry's sake
        final Square[][] grid;
        private boolean noMoreMoves = false;

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

                    Set<Square> m = square.getMooreNeighborhood();
                    m.removeIf(Square::isOccupied);
                    fringeAdjacent.remove(square);
                    fringeAdjacent.addAll(m);
                }
            });

            return square.getColor() != null;
        }

        /**
         * Determines if {@code square} is a legal move for {@code color}.
         *
         * @param square the square
         * @param color the color
         * @return {@code true} iff there is at least one opposite-colored
         * disc in a straight line between this square and a same-colored disc
         */
        protected boolean isLegalMoveForColor(Square square, Color color) {
            for (Function<Square, Square> direction : directions) {
                Square current = direction.apply(square);
                if (current != null && current.getColor() == color.opposite()) {
                    do {
                        current = direction.apply(current);
                    } while (current != null && current.getColor() == color.opposite());
                    if (current != null && current.getColor() == color) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Gets all squares that are legal moves for the given color.
         *
         * @param color the color
         * @return a set of {@code Square}s that are legal moves for {@code color}
         */
        protected Set<Board.Square> getMovesFor(Color color) {
            Set<Board.Square> moves = Sets.newHashSet();
            fringeAdjacent.forEach((square) -> {
                if (board.isLegalMoveForColor(square, color)) {
                    moves.add(square);
                }
            });
            return moves;
        }

        /**
         * Determines if there exists a legal move for the given color.
         *
         * @param color the color
         * @return {@code true} iff there is a legal move for {@code color}
         */
        protected boolean hasMovesFor(Color color) {
            return fringeAdjacent.stream().anyMatch((square) -> isLegalMoveForColor(square, color));
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

        /**
         * Counts how many more white pieces than black pieces there are on the
         * board.
         *
         * @return the number of white pieces on the board minus the number of
         * black pieces on the board
         */
        public int countWhiteOverBlack() {
            int count = 0;
            for (int rank = 0; rank < SQUARES_PER_SIDE; ++rank) {
                for (int file = 0; file < SQUARES_PER_SIDE; ++file) {
                    Color color = getSquare(rank, file).getColor();
                    if (color == Color.WHITE) {
                        ++count;
                    }
                    else if (color == Color.BLACK) {
                        --count;
                    }
                }
            }

            return count;
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

            /**
             * Gets whether this square has already been played.
             *
             * @return {@code true} iff this square has a disc on it
             */
            public boolean isOccupied() {
                return this.getColor() != null;
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

            @Override
            public String toString() {
                return "Square{" +
                        "rank=" + rank +
                        ", file=" + file +
                        ", color=" + color +
                        '}';
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
        Othello o = new Othello(OthelloPlayerWithKeyboard.class, OthelloPlayerArbitraryMove.class);
        o.play();
        System.out.println(o.board);
    }
}
