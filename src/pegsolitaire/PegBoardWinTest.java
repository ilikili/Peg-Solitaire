package pegsolitaire;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
    
    @Test
    public void testManualPlaySelectsPeg() {
        PegBoard board = new PegBoard(7);
        ManualPlay manual = new ManualPlay(board);
        manual.activate();

        // Click on a known peg (center top)
        MouseEvent click = new MouseEvent(board, 0, 0, 0,
                board.getCellSize() * 3,
                board.getCellSize() * 1,
                1, false);

        for (MouseListener ml : board.getMouseListeners()) {
            ml.mousePressed(click);
        }

        assertTrue(board.hasSelection());
    }

    @Test
    public void testManualPlayAttemptsMove() {
        PegBoard board = new PegBoard(7);
        ManualPlay manual = new ManualPlay(board);
        manual.activate();

        int cell = board.getCellSize();

        // Select peg at (3,1)
        MouseEvent select = new MouseEvent(board, 0, 0, 0,
                cell * 3, cell * 1, 1, false);

        // Target empty at (3,3)
        MouseEvent move = new MouseEvent(board, 0, 0, 0,
                cell * 3, cell * 3, 1, false);

        for (MouseListener ml : board.getMouseListeners()) {
            ml.mousePressed(select);
            ml.mousePressed(move);
        }

        // After a legal jump, (3,1) should now be empty
        assertFalse(board.hasPeg(1, 3));
    }
    
    @Test
    public void testAutomaticPlayMakesAMove() throws Exception {
        PegBoard board = new PegBoard(7);
        AutomaticPlay auto = new AutomaticPlay(board);

        int before = board.countPegs();

        auto.activate();
        Thread.sleep(1200); // allow one move
        auto.deactivate();

        int after = board.countPegs();

        assertTrue(after < before); // a peg should have been removed
    }
    
    @Test
    public void testAutomaticPlayStopsWhenNoMovesRemain() throws Exception {
        PegBoard board = new PegBoard(7);

        // Clear every cell, valid or invalid
        for (int r = 0; r < board.getBoardSize(); r++) {
            for (int c = 0; c < board.getBoardSize(); c++) {
                board.setPeg(r, c, false);
            }
        }

        AutomaticPlay auto = new AutomaticPlay(board);
        auto.activate();

        // Give the thread time to detect no moves
        Thread.sleep(500);

        assertFalse(auto.isRunning());
    }
}