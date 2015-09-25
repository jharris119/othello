package info.jayharris.othello;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OthelloPlayerWithKeyboardTest {

    Othello othello;

    @Before
    public void setUp() throws Exception {
        othello = new Othello(OthelloPlayerWithKeyboard.class, OthelloPlayerWithKeyboard.class);
    }

    @Test
    public void testGetMove() throws Exception {
        BufferedReader reader = mock(BufferedReader.class);
        when(reader.readLine()).thenReturn("c4", "e3", "f4", "q9", "f6");

        ((OthelloPlayerWithKeyboard) othello.white).reader = reader;
        ((OthelloPlayerWithKeyboard) othello.black).reader = reader;

        assertEquals(othello.getSquare("c4"), othello.white.getMove());
        assertEquals(othello.getSquare("e3"), othello.black.getMove());
        assertEquals(othello.getSquare("f4"), othello.white.getMove());
        assertEquals(othello.getSquare("f6"), othello.black.getMove());
    }
}