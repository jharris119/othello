package info.jayharris.othello;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static info.jayharris.othello.Othello.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;

public class OthelloTest_alpha {

    Othello othello;

    static Field boardField, currentField;

    public OthelloTest_alpha() throws Exception {
        boardField = Othello.class.getDeclaredField("board");
        boardField.setAccessible(true);

        currentField = Othello.class.getDeclaredField("current");
        currentField.setAccessible(true);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testNextPly() throws Exception {
        othello = new Othello(OthelloPlayerWithMoveList.class, OthelloPlayerWithMoveList.class);
        String s;

        // f4 is illegal
        Iterator<Othello.Board.Square> moves = ImmutableList.of("c4", "c5", "e6", "f5", "c6", "b5", "f4", "d6", "c3", "a4", "d7").
                stream().
                map(othello::getSquare).
                iterator();

        OthelloPlayerWithMoveList _black = (OthelloPlayerWithMoveList) othello.black;
        OthelloPlayerWithMoveList _white = (OthelloPlayerWithMoveList) othello.white;
        _black.setIterator(moves);
        _white.setIterator(moves);

        othello.nextPly();
        s = "        " +
            "        " +
            "        " +
            "  bbb   " +
            "   bw   " +
            "        " +
            "        " +
            "        " +
            "        ";
        assertThat(othello.board, new OthelloBoardMatcher(s));
        assertEquals(currentField.get(othello), othello.white);

        othello.nextPly();
        s = "        " +
            "        " +
            "        " +
            "  bbb   " +
            "  www   " +
            "        " +
            "        " +
            "        " +
            "        ";
        assertThat(othello.board, new OthelloBoardMatcher(s));
        assertEquals(currentField.get(othello), othello.black);

        othello.nextPly();
        s = "        " +
            "        " +
            "        " +
            "  bbb   " +
            "  wbb   " +
            "    b   " +
            "        " +
            "        " +
            "        ";
        assertThat(othello.board, new OthelloBoardMatcher(s));
        assertEquals(currentField.get(othello), othello.white);

        othello.nextPly();
        s = "        " +
            "        " +
            "        " +
            "  bbb   " +
            "  wwww  " +
            "    b   " +
            "        " +
            "        " +
            "        ";
        assertThat(othello.board, new OthelloBoardMatcher(s));
        assertEquals(currentField.get(othello), othello.black);

        othello.nextPly();
        s = "        " +
            "        " +
            "        " +
            "  bbb   " +
            "  bbww  " +
            "  b b   " +
            "        " +
            "        " +
            "        ";
        assertThat(othello.board, new OthelloBoardMatcher(s));
        assertEquals(currentField.get(othello), othello.white);

        othello.nextPly();
        s = "        " +
            "        " +
            "        " +
            "  bbb   " +
            " wwwww  " +
            "  b b   " +
            "        " +
            "        " +
            "        ";
        assertThat(othello.board, new OthelloBoardMatcher(s));
        assertEquals(currentField.get(othello), othello.black);

        othello.nextPly();
        s = "        " +
            "        " +
            "        " +
            "  bbb   " +
            " wwbww  " +
            "  bbb   " +
            "        " +
            "        " +
            "        ";
        assertThat(othello.board, new OthelloBoardMatcher(s));
        assertEquals(currentField.get(othello), othello.white);

        othello.nextPly();
        s = "        " +
            "        " +
            "  w     " +
            "  wwb   " +
            " wwbww  " +
            "  bbb   " +
            "        " +
            "        " +
            "        ";
        assertThat(othello.board, new OthelloBoardMatcher(s));
        assertEquals(currentField.get(othello), othello.black);

        othello.nextPly();
        s = "        " +
            "        " +
            "  w     " +
            "b wwb   " +
            " bwbww  " +
            "  bbb   " +
            "        " +
            "        " +
            "        ";
        assertThat(othello.board, new OthelloBoardMatcher(s));
        assertEquals(currentField.get(othello), othello.white);

        othello.nextPly();
        s = "        " +
            "        " +
            "  w     " +
            "b wwb   " +
            " bwwww  " +
            "  bww   " +
            "   w    " +
            "        " +
            "        ";
        assertThat(othello.board, new OthelloBoardMatcher(s));
        assertEquals(currentField.get(othello), othello.black);
    }

    @Test
    public void testCountWhiteOverBlack() throws Exception {
        othello = new Othello();

        int whites = 0, blacks = 0;
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < Math.pow(othello.board.SQUARES_PER_SIDE, 2); ++i) {
            if (Math.random() < 0.25) {
                s.append(' ');
            }
            else if (Math.random() < 0.5) {
                s.append('b');
                ++blacks;
            }
            else {
                s.append('w');
                ++whites;
            }
        }

        boardField.set(othello, OthelloBoardBuilder.build(othello, s.toString()));
        assertEquals(whites - blacks, othello.board.countWhiteOverBlack());
    }


    @Test
    public void testGetSquare() {
        othello = new Othello();

        assertSame(othello.board.grid[0][0], othello.board.getSquare("a1"));
        assertSame(othello.board.grid[6][4], othello.board.getSquare("E7"));
        assertSame(othello.board.grid[7][7], othello.board.getSquare("h8"));

        thrown.expect(IllegalArgumentException.class);
        othello.board.getSquare("");
        othello.board.getSquare("abc");
        othello.board.getSquare("5h");
        othello.board.getSquare("d06");
    }

    @Test
    public void testIsLegalMoveForColor() throws Exception {
        othello = new Othello();

        String s;
        Othello.Board.Square square;

        s = "       b" +
            "       b" +
            " wbbb  b" +
            " w     b" +
            "  w     " +
            "   w    " +
            "    w   " +
            "     b  ";
        boardField.set(othello, OthelloBoardBuilder.build(othello, s));

        square = othello.getSquare("f3");
        assertTrue(othello.board.isLegalMoveForColor(square, Color.WHITE));
        assertFalse(othello.board.isLegalMoveForColor(square, Color.BLACK));

        square = othello.getSquare("a3");
        assertTrue(othello.board.isLegalMoveForColor(square, Color.BLACK));
        assertFalse(othello.board.isLegalMoveForColor(square, Color.WHITE));

        square = othello.getSquare("h5");
        assertFalse(othello.board.isLegalMoveForColor(square, Color.BLACK));
        assertFalse(othello.board.isLegalMoveForColor(square, Color.WHITE));
    }

    @Test
    public void testGetMooreNeighborhood() throws Exception {
        othello = new Othello();

        Othello.Board.Square square;
        Set<Othello.Board.Square> expected, actual;

        square = othello.getSquare("f5");
        actual = square.getMooreNeighborhood();
        expected = ImmutableSet.of(square.get_e(), square.get_n(), square.get_ne(), square.get_nw(),
                square.get_s(), square.get_se(), square.get_sw(), square.get_w());
        assertEquals(expected, actual);

        square = othello.getSquare("b1");
        actual = square.getMooreNeighborhood();
        expected = ImmutableSet.of(square.get_e(), square.get_s(), square.get_se(), square.get_sw(), square.get_w());
        assertEquals(expected, actual);

        square = othello.getSquare("a8");
        actual = square.getMooreNeighborhood();
        expected = ImmutableSet.of(square.get_e(), square.get_n(), square.get_ne());
        assertEquals(expected, actual);
    }

//    @Test
//    public void testSetPiece() {
//        othello = new Othello();
//        Othello.Board board = othello.board;
//
//        assumeThat(board, new OthelloBoardMatcher(new HashMap<Othello.Color, Set<String>>() {{
//            this.put(Color.BLACK, ImmutableSet.of("d5", "e4"));
//            this.put(Color.WHITE, ImmutableSet.of("d4", "e5"));
//        }}));
//
//        assertTrue(board.setPiece(board.getSquare("c4"), Color.BLACK));
//        assertThat(board, new OthelloBoardMatcher(new HashMap<Othello.Color, Set<String>>() {{
//            this.put(Color.BLACK, ImmutableSet.of("c4", "d4", "e4", "d5"));
//            this.put(Color.WHITE, ImmutableSet.of("e5"));
//        }}));
//
//        assertTrue(board.setPiece(board.getSquare("e3"), Color.WHITE));
//        assertThat(board, new OthelloBoardMatcher(new HashMap<Othello.Color, Set<String>>() {{
//            this.put(Color.BLACK, ImmutableSet.of("c4", "d4", "d5"));
//            this.put(Color.WHITE, ImmutableSet.of("e3", "e4", "e5"));
//        }}));
//
//        // TODO: more complicated board configurations
//
//        // doesn't flip anything, assert false
//        assertFalse(board.setPiece(board.getSquare("e6"), Color.BLACK));
//        assertThat(board, new OthelloBoardMatcher(new HashMap<Othello.Color, Set<String>>() {{
//            this.put(Color.BLACK, ImmutableSet.of("c4", "d4", "d5"));
//            this.put(Color.WHITE, ImmutableSet.of("e3", "e4", "e5"));
//        }}));
//    }

//    @Test
//    public void testGetMovesFor() throws Exception {
//        othello = new Othello();
//        Set<Othello.Board.Square> expected;
//
//        expected = Sets.newHashSet("d3", "c4", "f5", "e6")
//                .stream()
//                .map((str) -> othello.board.getSquare(str))
//                .collect(Collectors.toSet());
//        assertEquals(expected, othello.board.getMovesFor(Color.BLACK));
//
//        othello.board.setPiece(othello.getSquare("d3"), Color.BLACK);
//        othello.board.setPiece(othello.getSquare("c3"), Color.WHITE);
//        othello.board.setPiece(othello.getSquare("c4"), Color.BLACK);
//        othello.board.setPiece(othello.getSquare("e3"), Color.WHITE);
//
//        expected = Sets.newHashSet("b2", "c2", "d2", "e2", "f2", "f3", "f4", "f5", "f6")
//                .stream()
//                .map((str) -> othello.board.getSquare(str))
//                .collect(Collectors.toSet());
//        assertEquals(expected, othello.board.getMovesFor(Color.BLACK));
//    }

//    @Test
//    public void testIsGameOver() throws Exception {
//        othello = new Othello();
//
//        String s;
//
//        s = "       b" +
//            "w w  w b" +
//            "wwwwwwbb" +
//            "wbwwwwbb" +
//            "wbwwbwbb" +
//            "wwwbwbbb" +
//            "  wbbbbb" +
//            "  wwbbbb";
//        boardField.set(othello, OthelloBoardBuilder.build(othello, s));
//        fringeAdjacentField.set(othello,
//                Sets.newHashSet("a1", "b1", "c1", "d1", "e1", "f1", "g1", "b2", "d2", "e2", "g7", "a7", "b7", "b8")
//                    .stream()
//                    .map((str) -> othello.board.getSquare(str))
//                    .collect(Collectors.toSet()));
//        assertFalse(othello.isGameOver());
//
//        s = "w bbbbbb" +
//            "wwbbbbbb" +
//            "wwwbbbbb" +
//            "wwbwbbbb" +
//            "wwwbwbbb" +
//            "wwbwbwbb" +
//            "wwbbwbwb" +
//            "wwwwwwww";
//        boardField.set(othello, OthelloBoardBuilder.build(othello, s));
//        fringeAdjacentField.set(othello, Collections.singleton(othello.getSquare("b1")));
//        assertTrue(othello.isGameOver());
//
//        s = "bbbbbbbb" +
//            "bwwwwwbb" +
//            "wbbwbbwb" +
//            "wbbbbwbb" +
//            "wbwbbwbb" +
//            "wwwwbbwb" +
//            "wwwwwbbb" +
//            "wbbbbbbb";
//        boardField.set(othello, OthelloBoardBuilder.build(othello, s));
//        fringeAdjacentField.set(othello, Collections.emptySet());
//        assertTrue(othello.isGameOver());
//    }

    class OthelloBoardMatcher extends BaseMatcher<Othello.Board> {

        Map<Othello.Board.Square, Othello.Color> pieces;

        public OthelloBoardMatcher(String boardString) {
            this(new HashMap<Color, Set<String>>() {{
                this.put(Color.BLACK, Sets.newHashSet());
                this.put(Color.WHITE, Sets.newHashSet());

                for (int rank = 0; rank < othello.board.SQUARES_PER_SIDE; ++rank) {
                    for (int file = 0; file < othello.board.SQUARES_PER_SIDE; ++file) {
                        char c = boardString.charAt(rank * othello.board.SQUARES_PER_SIDE + file);
                        if (c == 'b' || c == 'B') {
                            this.get(Color.BLACK).add("" + (char)('a' + file) + (rank + 1));
                        }
                        else if (c == 'w' || c == 'W') {
                            this.get(Color.WHITE).add("" + (char)('a' + file) + (rank + 1));
                        }
                    }
                }
            }});
        }

        public OthelloBoardMatcher(Map<Othello.Color, Set<String>> pieceslist) {
            pieces = Maps.newHashMap();
            for (String str : pieceslist.get(Color.BLACK)) {
                pieces.put(othello.getSquare(str), Color.BLACK);
            }
            for (String str : pieceslist.get(Color.WHITE)) {
                pieces.put(othello.getSquare(str), Color.WHITE);
            }
        }

        @Override
        public boolean matches(Object item) {
            Othello.Board board = (Othello.Board) item;

            Othello.Board.Square square;
            for (int rank = 0; rank < board.SQUARES_PER_SIDE; ++rank) {
                for (int file = 0; file < board.SQUARES_PER_SIDE; ++file) {
                    square = board.getSquare(rank, file);
                    if (square.getColor() != pieces.getOrDefault(square, null)) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("match all board pieces to colors in given map");
        }
    }
}