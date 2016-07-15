package info.jayharris.othello;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Othello {

    protected final Board board;
    protected final OthelloPlayer black, white;
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
    public final static ImmutableList<Function<Board.Square, Board.Square>> cardinals = ImmutableList.of(
            Board.Square::get_n, Board.Square::get_e, Board.Square::get_s, Board.Square::get_w
    );

    public Othello() {
        this(OthelloPlayerWithKeyboard.class, OthelloPlayerWithKeyboard.class);
    }

    public Othello(Class<? extends OthelloPlayer> blacktype, Class<? extends OthelloPlayer> whitetype) {
        board = new Board();
        initBoard();

        black = this.buildPlayer(blacktype, Color.BLACK);
        white = this.buildPlayer(whitetype, Color.WHITE);
        current = this.black;
    }

    public Othello(OthelloPlayer black, OthelloPlayer white) {
        board = new Board();
        initBoard();

        this.black = black;
        this.white = white;
        current = this.black;
    }

    private void initBoard() {
        int p = board.SQUARES_PER_SIDE / 2 - 1;
        board.forceSetPiece(board.getSquare(p, p), Color.WHITE);
        board.forceSetPiece(board.getSquare(p + 1, p), Color.BLACK);
        board.forceSetPiece(board.getSquare(p, p + 1), Color.BLACK);
        board.forceSetPiece(board.getSquare(p + 1, p + 1), Color.WHITE);
    }

    public OthelloPlayer play() {
        Color winner;

        while (nextPly() != null);

        return (winner = OthelloUtils.winner(board)) == null ? null : (winner == Color.WHITE ? white : black);
    }

    /**
     * Plays the next ply.
     *
     * That is, get the current player's next (legal) move, play it, and
     * update the board state and game state.
     *
     * @return the player whose turn it is after this ply, or {@code null} if
     *  neither player can move
     */
    protected OthelloPlayer nextPly() {
        Board.Square move;
        do {
            move = current.getMove();
        } while (!board.setPiece(move, current.color));

        if (hasMoveFor(getOtherPlayer())) {
            current = getOtherPlayer();
        }
        else if (!hasMoveFor(current)) {
            current = null;
        }
        return current;
    }

    /**
     * Determines if there's at least one legal move for {@code player}.
     *
     * @param player the play
     * @return {@code true} iff there's at least one legal move for {@code player}
     */
    public boolean hasMoveFor(OthelloPlayer player) {
        return board.hasMove(player.color);
    }

    /**
     * Gets the current player.
     *
     * @return the current player
     */
    public OthelloPlayer getCurrentPlayer() {
        return current;
    }

    /**
     * Gets the non-current player.
     *
     * @return the player who's not the current player
     */
    public OthelloPlayer getOtherPlayer() {
        return current == white ? black : white;
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
     * algebraic notation.
     *
     * @param square the square name, in algebraic notation
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
        private final Set<Board.Square> occupied, frontier, accessible;

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

            occupied = Sets.newHashSet();
            frontier = Sets.newHashSet();
            accessible = Sets.newHashSet();
        }

        /**
         * Puts a {@code color} disc on {@code square}, if legal, and flips
         * the necessary discs.
         *
         * @param square the square
         * @param color the color
         * @return {@code true} iff {@code square} is a legal move for {@code color}
         */
        protected boolean setPiece(Square square, Color color) {
            Preconditions.checkNotNull(square);
            Preconditions.checkArgument(square.getColor() == null);

            return setPiece(square, color, false);
        }

        /**
         * Force set a disc on a square, even if it's not a legal move.
         *
         * Force-setting a disc doesn't flip any surrounding discs.
         *
         * @param square the square
         * @param color the color
         * @return {@code true}
         */
        private boolean forceSetPiece(Square square, Color color) {
            Preconditions.checkNotNull(square);

            return setPiece(square, color, true);
        }

        private boolean setPiece(Square square, Color color, boolean force) {
            Set<Square> toFlip = getSquaresToFlip(square, color);
            if (!toFlip.isEmpty() || force) {
                square.setPiece(color);

                if (!force) {
                    toFlip.forEach(Square::flip);
                }

                occupied.add(square);
                accessible.remove(square);
                if (square.isFrontier()) {
                    frontier.add(square);
                    accessible.addAll(square.getUnoccupiedNeighbors());
                }

                return true;
            }
            return false;
        }

        /**
         * Determines if {@code square} is a legal move for {@code color}
         *
         * @param square the square
         * @param color the color
         * @return {@code true} iff {@code square} is a legal move for {@code color}
         */
        protected boolean isLegal(Square square, Color color) {
            Preconditions.checkNotNull(square);

            return !square.isOccupied() && directions.stream().anyMatch((dir) ->
                !this.getSquaresToFlip(square, color, dir).isEmpty()
            );
        }

        /**
         * Gets the squares whose discs would be flipped if {@code color} were
         * to play at {@code start}.
         *
         * @param start the square to play
         * @param color the color to play
         * @return a set of {@code Square}s
         */
        protected Set<Square> getSquaresToFlip(Square start, Color color) {
            Preconditions.checkNotNull(start);
            Preconditions.checkArgument(start.getColor() == null);

            Set<Square> toFlip = Sets.newHashSet();
            for (Function<Square, Square> direction : directions) {
                toFlip.addAll(getSquaresToFlip(start, color, direction));
            }
            return toFlip;
        }

        /**
         * Gets the squares in {@code direction} whose discs would be flipped if
         * {@code color} were to play at {@code start}.
         *
         * @param start the square to play
         * @param color the color to play
         * @param direction the direction
         * @return a (possibly empty) set of {@code Square}s
         */
        // TODO: make me private
        protected Set<Square> getSquaresToFlip(Square start, Color color, Function<Square, Square> direction) {
            Square current = direction.apply(start);

            Set<Square> toFlip = Sets.newHashSet();
            while (current != null && current.getColor() == color.opposite()) {
                toFlip.add(current);
                current = direction.apply(current);
            }

            if (current == null || current.getColor() == null) {
                toFlip.clear();
            }
            return toFlip;
        }

        /**
         * Determines if {@code color} has any legal moves
         *
         * @param color the color
         * @return {@code true} iff {@code color} has at least one legal move
         */
        protected boolean hasMove(Color color) {
            return accessible.stream().anyMatch((square) -> isLegal(square, color));
        }

        /**
         * Gets the square referenced by the given algebraic notation.
         *
         * @param square algebraic notation reference for the square
         * @return the square
         */
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

        public Set<Square> getOccupied() {
            return Collections.unmodifiableSet(occupied);
        }

        public Set<Square> getFrontier() {
            return Collections.unmodifiableSet(frontier);
        }

        public Set<Square> getAccessible() {
            return Collections.unmodifiableSet(accessible);
        }

        public Set<Square> getCorners() {
            return ImmutableSet.of(
                    getSquare(0, 0),
                    getSquare(0, SQUARES_PER_SIDE - 1),
                    getSquare(SQUARES_PER_SIDE - 1, 0),
                    getSquare(SQUARES_PER_SIDE - 1, SQUARES_PER_SIDE - 1)
            );
        }

        public class Square {

            private final int rank, file;
            private Color color;

            private Square _n, _ne, _e, _se, _s, _sw, _w, _nw;

            private Predicate<Square> isOccupiedP = Square::isOccupied,
                                      isFrontierP = Square::isFrontier;

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
             * Gets the {@link Square}s adjacent to {@code this} that are unoccupied.
             *
             * @return a set of adjacent, unoccupied {@code Square}s
             */
            public Set<Square> getUnoccupiedNeighbors() {
                return directions.stream().
                        map((dir) -> dir.apply(this)).
                        filter(Objects::nonNull).
                        filter(isOccupiedP.negate()).
                        collect(Collectors.toSet());
            }

            /**
             * Gets the {@link Square}s adjacent to {@code this} that are occupied.
             *
             * @return a set of adjacent, occupied {@code Square}s
             */
            public Set<Square> getOccupiedNeighbors() {
                return directions.stream().
                        map((dir) -> dir.apply(this)).
                        filter(Objects::nonNull).
                        filter(isOccupiedP).
                        collect(Collectors.toSet());
            }

            /**
             * Gets the {@link Square}s orthogonally adjacent to {@code this}.
             *
             * @return a set of orthogonally adjacent squares
             */
            public Set<Square> getOrthogonalNeighbors() {
                return cardinals.stream().
                        map((dir) -> dir.apply(this)).
                        filter(Objects::nonNull).
                        collect(Collectors.toSet());
            }

            /**
             * Gets whether this square is occupied.
             *
             * @return {@code true} iff this square has a disc on it
             */
            public boolean isOccupied() {
                return this.getColor() != null;
            }

            /**
             * Gets whether this a frontier square, meaning that it's a occupied
             * square adjacent to at least one unoccupied square.
             *
             * @return {@code true} iff this square is a frontier square
             */
            public boolean isFrontier() {
                if (!isOccupied()) {
                    return false;
                }

                Square neighbor;
                for (Function<Square, Square> direction : directions) {
                    if ((neighbor = direction.apply(this)) != null && !neighbor.isOccupied()) {
                        return true;
                    }
                }
                return false;
            }

            /* ****************************************************************
             * Accessor methods
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

            public String getAlgebraicNotation() {
                return String.valueOf((char) ('a' + file)) + (rank + 1);
            }

            @Override
            public String toString() {
                return "Square{" +
                        "(" + getAlgebraicNotation() + ") " +
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
        Othello o = new Othello(OthelloPlayerWithKeyboard.class, OthelloPlayerRandomMove.class);
        o.play();
        System.out.println(o.board);
    }
}
