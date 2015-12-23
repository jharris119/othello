package info.jayharris.othello;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import info.jayharris.othello.Othello.*;
import info.jayharris.othello.Othello.Board.Square;
import org.apache.commons.lang3.tuple.Pair;

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
     * Get the stable discs on the board.
     *
     * @param board the board
     * @return a set of all stable discs on the board
     */
    public static Set<Board.Square> getStableDiscs(Board board) {
        if (board.getCorners().stream().noneMatch(Board.Square::isOccupied)) {
            return Collections.emptySet();
        }

        for (Board.Square square : board.getCorners()) {

        }



        return null;
    }


    public static Set<Square> getStableDiscsFromCorner(Square square) {
        final Set<Square> stable = Sets.newHashSet();
        Set<Square> examined = Sets.newHashSet();
        Queue<Square> queue = Lists.newLinkedList();







        Predicate<Square> isNullOrStableP = (s) -> {
            System.err.println("    isNullOrStableP: " + s);
            boolean i = Objects.isNull(s) || stable.contains(s);        // the color also has to match
            System.err.println("    " + i);
            return i;
        };
        Predicate<Square> examinedContainsP = examined::contains;
        Square curr;

        queue.add(square);
        while (!queue.isEmpty()) {
            curr = queue.remove();

            System.err.println("curr: " + curr.toString());

            if(ImmutableList.of(
                    Pair.of(curr.get_w(), curr.get_e()),
                    Pair.of(curr.get_nw(), curr.get_se()),
                    Pair.of(curr.get_n(), curr.get_s()),
                    Pair.of(curr.get_ne(), curr.get_sw())
            ).stream()
                    .allMatch((pair) -> {
                        System.err.println("  allMatch");
                        System.err.println(String.format("  %s", pair.getLeft()));
                        System.err.println(String.format("  %s", pair.getRight()));
                        return isNullOrStableP.test(pair.getLeft()) || isNullOrStableP.test(pair.getRight());
                    })) {
                stable.add(curr);
                queue.addAll(curr.getOccupiedNeighbors().stream().filter(examinedContainsP.negate()).collect(Collectors.toSet()));
            }

            examined.add(curr);
        }

        return stable;
    }

//    public static Set<Square> getStableDiscsFromCorner(Square square) {
//        final Set<Square> stable = Sets.newHashSet(), notStable = Sets.newHashSet();
//        Queue<Square> queue = Lists.newLinkedList();
//        Predicate<Square> isUnchangingP = (s) -> s == null || stable.contains(s);
//        Predicate<Pair<Square, Square>> isUnchangingPairP = (pair) -> isUnchangingP.test(pair.getLeft()) || isUnchangingP.test(pair.getRight());
//
//        queue.add(square);
//
//        Stream<Pair<Square, Square>> pairs;
//        Square curr;
//        while (!queue.isEmpty()) {
//            curr = queue.remove();
//            pairs = ImmutableList.of(
//                    Pair.of(curr.get_e(), curr.get_w()),
//                    Pair.of(curr.get_ne(), curr.get_sw()),
//                    Pair.of(curr.get_n(), curr.get_s()),
//                    Pair.of(curr.get_se(), curr.get_nw())).stream();
//
//            if (pairs.allMatch(isUnchangingPairP)) {
//                stable.add(curr);
//                for (Square neighbor : curr.getOccupiedNeighbors()) {
//                    if (!(stable.contains(neighbor) || notStable.contains(neighbor))) {
//                        queue.add(neighbor);
//                    }
//                }
//            }
//            else {
//                notStable.add(curr);
//            }
//
//            pairs.close();
//        }
//
//        return stable;
//    }

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
