package info.jayharris.othello;

import java.lang.reflect.Method;

class OthelloBoardBuilder {

    static Method squareSetPieceMethod;

    static {
        try {
            squareSetPieceMethod = Othello.Board.Square.class.getDeclaredMethod("setPiece", Othello.Color.class);
            squareSetPieceMethod.setAccessible(true);
        } catch (NoSuchMethodException e) { }
    }

    public Othello othello;

    public OthelloBoardBuilder(Othello othello) {
        this.othello = othello;
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
            squareSetPieceMethod.invoke(board.getSquare(rank, file), color);

            file = (file + 1) % board.SQUARES_PER_SIDE;
            if (file == 0) {
                ++rank;
            }
        }
        return board;
    }

    public Othello.Board buildReal() {
        return buildReal(Integer.MAX_VALUE);
    }

    public Othello.Board buildReal(int halfplies) {
        Othello testOthello = new Othello(OthelloPlayerRandomMove.class, OthelloPlayerRandomMove.class);

        for (int i = 0; i < halfplies; ++i) {
            if(!testOthello.nextPly()) {
                break;
            }
        }

        return testOthello.board;
    }
}
