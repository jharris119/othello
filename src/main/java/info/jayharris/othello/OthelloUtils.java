package info.jayharris.othello;

import com.google.common.collect.Iterables;
import info.jayharris.othello.Othello.*;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class OthelloUtils {

    /**
     * Gets all legal moves for {@code color} on the given board.
     *
     * @param board the board
     * @param color the color
     * @return a set of legal moves for {@code color} on {@code board}
     */
    public static Set<Board.Square> getAllMoves(Board board, Color color) {
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
}
