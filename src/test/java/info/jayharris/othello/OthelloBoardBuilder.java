package info.jayharris.othello;

class OthelloBoardBuilder {
    public static Othello.Board build(Othello othello, String str) throws Exception {
        Othello.Board board = othello.new Board();

        int rank = 0, file = 0;
        for (char c : str.toCharArray()) {
            Othello.Color color = null;
            if (c == 'b' || c == 'B') {
                color = Othello.Color.BLACK;
            } else if (c == 'w' || c == 'W') {
                color = Othello.Color.WHITE;
            }
            OthelloTest.squareSetPieceMethod.invoke(board.getSquare(rank, file), color);

            file = (file + 1) % board.SQUARES_PER_SIDE;
            if (file == 0) {
                ++rank;
            }
        }
        return board;
    }

    public static Othello.Board buildReal(Othello othello, String str, int halfplies) {
        Othello testOthello = new Othello(OthelloPlayerRandomMove.class, OthelloPlayerRandomMove.class);

        for (int i = 0; i < halfplies && !othello.isGameOver(); ++i) {
            othello.nextPly();
        }

        return testOthello.board;
    }
}
