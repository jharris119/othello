package info.jayharris.othello;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SquareTest {

    Othello othello;
    OthelloBoardBuilder builder;

    Field boardField;
    Random random;

    public SquareTest() throws Exception {
        boardField = Othello.class.getDeclaredField("board");
        boardField.setAccessible(true);
    }

    @Before
    public void setUp() throws Exception {
        OthelloPlayer black = mock(OthelloPlayer.class), white = mock(OthelloPlayer.class);

        othello = new Othello(black, white);
        builder = new OthelloBoardBuilder(othello);

        boardField.set(othello, builder.build("  w       ww    w www   wwwwww  wwwww   wwww     wbb    w w b   "));
    }

    @Test
    public void testGetUnoccupiedNeighbors() throws Exception {
        Othello.Board.Square test;
        Set<Othello.Board.Square> expected;

        test = othello.getSquare("c1");
        expected = Stream.of("b1", "b2", "d1").map(othello::getSquare).collect(Collectors.toSet());
        assertEquals(expected, test.getUnoccupiedNeighbors());

        test = othello.getSquare("a4");
        expected = Stream.of("b3").map(othello::getSquare).collect(Collectors.toSet());
        assertEquals(expected, test.getUnoccupiedNeighbors());

        test = othello.getSquare("d7");
        expected = Stream.of("e6", "e7", "d8").map(othello::getSquare).collect(Collectors.toSet());
        assertEquals(expected, test.getUnoccupiedNeighbors());

        test = othello.getSquare("f4");
        expected = Stream.of("f3", "g3", "g4", "f5", "g5").map(othello::getSquare).collect(Collectors.toSet());
        assertEquals(expected, test.getUnoccupiedNeighbors());

        test = othello.getSquare("d4");
        expected = Collections.emptySet();
        assertEquals(expected, test.getUnoccupiedNeighbors());

        test = othello.getSquare("a8");
        expected = Stream.of("a7", "b8").map(othello::getSquare).collect(Collectors.toSet());
        assertEquals(expected, test.getUnoccupiedNeighbors());
    }

    @Test
    public void testIsOccupied() throws Exception {
        Collection<Othello.Board.Square> occupied = Collections2.transform(
                ImmutableSet.of("c1", "c2", "d2", "a3", "c3", "d3", "e3", "a4", "b4", "c4", "d4", "e4", "f4", "a5",
                        "b5", "c5", "d5", "e5", "a6", "b6", "c6", "d6", "b7", "c7", "d7", "a8", "c8", "e8"),
                othello::getSquare
        );

        Othello.Board.Square square;
        for (int i = 0; i < 10; ++i) {
            square = randomSquare();
            assertEquals(occupied.contains(square), square.isOccupied());
        }
    }

    @Test
    public void testIsFrontier() throws Exception {
        Collection<Othello.Board.Square> frontier = Collections2.transform(
                ImmutableSet.of("c1", "c2", "d2", "a3", "c3", "d3", "e3", "a4", "b4", "c4", "e4", "f4", "d5", "e5",
                        "a6", "b6", "d6", "b7", "c7", "d7", "a8", "c8", "e8"),
                othello::getSquare
        );

        Othello.Board.Square square;
        for (int i = 0; i < 25; ++i) {
            square = randomSquare();
            assertEquals(String.format("%s", square), frontier.contains(square), square.isFrontier());
        }
    }

    public Othello.Board.Square randomSquare() {
        return othello.board.getSquare(
                getRandom().nextInt(othello.board.SQUARES_PER_SIDE),
                getRandom().nextInt(othello.board.SQUARES_PER_SIDE));
    }

    private Random getRandom() {
        if (random == null) {
            random = new Random();
        }
        return random;
    }
}