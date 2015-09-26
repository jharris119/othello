package info.jayharris.othello;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class OthelloBoardBuilder {

    static Field boardField;
    static Method boardForceSetPieceMethod;

    public Othello othello;

    public OthelloBoardBuilder(Othello othello) {
        this.othello = othello;

        try {
            boardField = Othello.class.getDeclaredField("board");
            boardField.setAccessible(true);

            boardForceSetPieceMethod = Othello.Board.class.getDeclaredMethod(
                    "forceSetPiece", Othello.Board.Square.class, Othello.Color.class);
            boardForceSetPieceMethod.setAccessible(true);
        } catch (NoSuchMethodException|NoSuchFieldException e) { }
    }

    public Othello.Board build(String str) throws Exception {
        Othello.Board board = othello.new Board();

        int rank = 0, file = 0;
        for (char c : str.toCharArray()) {
            Othello.Color color = null;
            if (c == 'b' || c == 'B') {
                color = Othello.Color.BLACK;
            } else if (c == 'w' || c == 'W') {
                color = Othello.Color.WHITE;
            }

            if (color != null) {
                boardForceSetPieceMethod.invoke(board, board.getSquare(rank, file), color);
            }

            file = (file + 1) % board.SQUARES_PER_SIDE;
            if (file == 0) {
                ++rank;
            }
        }

        boardField.set(othello, board);
        return board;
    }
}
