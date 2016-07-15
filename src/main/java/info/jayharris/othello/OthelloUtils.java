package info.jayharris.othello;

import com.google.common.collect.*;
import info.jayharris.othello.Othello.*;
import info.jayharris.othello.Othello.Board.Square;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OthelloUtils {
    /**
     * Gets all legal moves for {@code color} on the given board.
     *
     * @param board the board
     * @param color the color
     * @return a set of legal moves for {@code color} on {@code board}
     */
    public static Set<Square> getAllMoves(Board board, Color color) {
        return board.getAccessible().
                stream().
                filter((square) -> board.isLegal(square, color)).
                collect(Collectors.toSet());
    }
    
    public static Board.Square getRandomMove(Othello othello, Color color) {
        return getRandomMove(othello.board, color);
    }

    /**
     * Gets a random legal move for {@code color} on the given board, assuming
     * one exists.
     *
     * @param board the board
     * @param color the color
     * @return a legal move for {@code color} on {@code board}, or {@code null}
     *  if no legal move exists
     */
    public static Board.Square getRandomMove(Board board, Color color) {
        Random random = new Random();
        Set<Board.Square> moves = getAllMoves(board, color);
        if (moves.isEmpty()) {
            return null;
        }
        return Iterables.get(moves, random.nextInt(moves.size()));
    }

    /**
     * Gets a set of the stable discs on the board of any color.
     *
     * @param board the board
     * @return a set of stable discs
     */
    public static Set<Square> getStableDiscs(Board board) {
        Set<Square> stable = Sets.newHashSet();
        List<Square> upcoming = board.getCorners()
                .stream()
                .filter(Square::isOccupied)
                .collect(Collectors.toList());
        Set<Square> checking = Sets.newHashSet(upcoming);

        Predicate<Square> pIsStable = new Predicate<Square>() {
            @Override
            public boolean test(Square square) {
                if (!square.isOccupied()) {
                    return false;
                }

                if (traverseSquaresInAllDirections(square).allMatch(Square::isOccupied)) {
                    return true;
                }

                Set<Set<Square>> antiparallelPairs = new HashSet<Set<Square>>() {{
                    this.add(Sets.newHashSet(square.get_n(), square.get_s()));
                    this.add(Sets.newHashSet(square.get_ne(), square.get_sw()));
                    this.add(Sets.newHashSet(square.get_e(), square.get_w()));
                    this.add(Sets.newHashSet(square.get_se(), square.get_nw()));
                }};
                return antiparallelPairs.stream()
                        .allMatch(set -> set.contains(null) ||
                                set.stream().anyMatch(neighbor -> (neighbor.getColor() == square.getColor() && stable.contains(neighbor))) ||
                                set.stream().allMatch(neighbor -> stable.contains(neighbor))

                        );
            }
        };

        while (!upcoming.isEmpty()) {
            Square current = upcoming.remove(0);
            Othello.cardinals.forEach((dir) -> {
                Square next = current;
                while ((next = dir.apply(next)) != null && next.isOccupied() && checking.add(next)) {
                    upcoming.add(next);
                }
            });
            if (pIsStable.test(current)) {
                stable.add(current);
            }
        }
        return stable;
    }

    public static Stream<Square> traverseSquaresInAllDirections(Square current) {
        Stream.Builder<Square> builder = Stream.builder();
        return Othello.directions.stream().flatMap((dir) -> traverseSquaresInDirection(current, dir));
    }

    public static Stream<Square> traverseSquaresInDirection(Square current, Function<Square, Square> direction) {
        Stream.Builder<Square> builder = Stream.builder();
        while ((current = direction.apply(current)) != null) {
            builder.accept(current);
        }
        return builder.build();
    }

    /**
     * Determines if the game is over.
     *
     * @param board the board
     * @return {@code true} iff the game is over
     */
    public static boolean isGameOver(Board board) {
        return board.getAccessible().isEmpty() || !(board.hasMove(Color.BLACK) || board.hasMove(Color.WHITE));
    }

    /**
     * Gets the winning color.
     *
     * @param board the board
     * @return the color with more pieces on the board, or {@code null} if there's a tie
     */
    public static Color winner(Board board) {
        int black = 0;
        Color color;

        for (Board.Square square : board.getAccessible()) {
            if ((color = square.getColor()) == Color.BLACK) {
                ++black;
            }
            else if (color == Color.WHITE) {
                --black;
            }
        }
        return black == 0 ? null : (black > 0 ? Color.BLACK : Color.WHITE);
    }
}
