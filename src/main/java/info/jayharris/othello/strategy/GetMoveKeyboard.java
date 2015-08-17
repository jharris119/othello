package info.jayharris.othello.strategy;

import info.jayharris.othello.Othello;
import info.jayharris.othello.OthelloPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetMoveKeyboard implements GetMoveStrategy {
    @Override
    public Othello.Board.Square getNextMove(final OthelloPlayer player) {
        Othello othello = player.othello;
        Othello.Color color = player.color;

        System.out.print(String.format("%s's turn. Place disc at: ",
                color == Othello.Color.WHITE ? "White" : "Black"));

        Othello.Board.Square square = null;
        while (square == null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                square = othello.getSquare(br.readLine());
            }
            catch (IOException|IllegalArgumentException e) {
                square = null;
            }
        }
        return square;
    }
}
