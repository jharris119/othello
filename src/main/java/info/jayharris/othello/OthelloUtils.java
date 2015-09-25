package info.jayharris.othello;

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
}
