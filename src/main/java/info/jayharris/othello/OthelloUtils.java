package info.jayharris.othello;

import com.google.common.collect.Iterables;
import info.jayharris.othello.Othello.*;

import java.util.Set;
import java.util.stream.Collectors;

public class OthelloUtils {

    public static Set<Board.Square> getAllMoves(Othello othello, Color color) {
        return getAllMoves(othello.board, color);
    }

    public static Set<Board.Square> getAllMoves(Board board, Color color) {
        return board.getAccessible().
                stream().
                filter((square) -> board.isLegal(square, color)).
                collect(Collectors.toSet());
    }

    public static Board.Square getRandomMove(Othello othello, Color color, java.util.Random random) {
        return getRandomMove(othello.board, color, random);
    }

    public static Board.Square getRandomMove(Board board, Color color, java.util.Random random) {
        if (random == null) {
            random = new java.util.Random();
        }

        Set<Board.Square> moves = getAllMoves(board, color);
        if (moves.isEmpty()) {
            return null;
        }
        return Iterables.get(moves, random.nextInt(moves.size()));
    }
}
