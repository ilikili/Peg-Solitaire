package pegsolitaire;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PegBoardShapeTest {

    @Test
    public void testEnglishShapeSelected() {
        PegBoard board = new PegBoard(7);
        board.setShape(PegBoard.Shape.ENGLISH);

        // English corners must be invalid
        assertFalse(board.isValidCell(0, 0));
        assertFalse(board.isValidCell(0, 6));
        assertFalse(board.isValidCell(6, 0));
        assertFalse(board.isValidCell(6, 6));

        // English cross center must be valid
        assertTrue(board.isValidCell(3, 3));
        assertTrue(board.isValidCell(3, 1));
        assertTrue(board.isValidCell(1, 3));
    }

    @Test
    public void testEuropeanShapeSelected() {
        PegBoard board = new PegBoard(7);
        board.setShape(PegBoard.Shape.EUROPEAN);

        // European corners must be invalid
        assertFalse(board.isValidCell(0, 0));
        assertFalse(board.isValidCell(0, 6));
        assertFalse(board.isValidCell(6, 0));
        assertFalse(board.isValidCell(6, 6));

        // European diagonal expansions must be valid
        assertTrue(board.isValidCell(1, 1));
        assertTrue(board.isValidCell(1, 5));
        assertTrue(board.isValidCell(5, 1));
        assertTrue(board.isValidCell(5, 5));

        // Center must be valid
        assertTrue(board.isValidCell(3, 3));
    }

    @Test
    public void testDiamondShapeSelected() {
        PegBoard board = new PegBoard(7);
        board.setShape(PegBoard.Shape.DIAMOND);

        // Diamond corners must be invalid
        assertFalse(board.isValidCell(0, 0));
        assertFalse(board.isValidCell(0, 6));
        assertFalse(board.isValidCell(6, 0));
        assertFalse(board.isValidCell(6, 6));

        // Diamond edges at Manhattan distance = mid must be valid
        assertTrue(board.isValidCell(0, 3)); // top
        assertTrue(board.isValidCell(3, 0)); // left
        assertTrue(board.isValidCell(3, 6)); // right
        assertTrue(board.isValidCell(6, 3)); // bottom

        // Center must be valid
        assertTrue(board.isValidCell(3, 3));
    }
}