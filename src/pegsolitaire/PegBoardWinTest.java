package pegsolitaire;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PegBoardWinTest {

    @Test
    public void testRegularWinDetected() {
        PegBoard board = new PegBoard(7);
        board.setShape(PegBoard.Shape.ENGLISH);

        // Clear all pegs
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                board.getPegs()[r][c] = false;
            }
        }

        // Leave one peg NOT in the center
        board.getPegs()[0][3] = true;

        assertEquals(PegBoard.WinState.WIN, board.getWinState());
    }

    @Test
    public void testPerfectWinDetected() {
        PegBoard board = new PegBoard(7);
        board.setShape(PegBoard.Shape.ENGLISH);

        // Clear all pegs
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                board.getPegs()[r][c] = false;
            }
        }

        // Leave one peg in the center
        int mid = 7 / 2;
        board.getPegs()[mid][mid] = true;

        assertEquals(PegBoard.WinState.PERFECT_WIN, board.getWinState());
    }

    @Test
    public void testNoWinWhenMultiplePegsRemain() {
        PegBoard board = new PegBoard(7);
        board.setShape(PegBoard.Shape.ENGLISH);

        // Ensure at least 2 pegs exist
        board.getPegs()[3][3] = true;
        board.getPegs()[3][4] = true;

        assertEquals(PegBoard.WinState.NONE, board.getWinState());
    }
}
