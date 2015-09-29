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
