package pegsolitaire;

import javax.swing.Timer;

public class ReplayController {

    private PegBoard board;
    private MoveHistory history;

    private int index = 0;
    private boolean playing = false;
    private Timer timer;

    // adjust as you like
    private int delay = 600;

    public ReplayController(PegBoard board, MoveHistory history) {
        this.board = board;
        this.history = history;
    }

    /**
     * Start replay from the beginning.
     */
    public void startReplay() {

        // If paused, just resume
        if (!playing && timer != null) {
            playing = true;
            timer.start();
            return;
        }

        // If starting fresh, reset everything
        stopReplay();

        if (history.isEmpty()) return;

        board.loadState(PegSolitaire.initialBoard);
        index = 0;
        playing = true;

        timer = new Timer(delay, e -> playNextMove());
        timer.start();
    }


    /**
     * Stop replay and destroy timer.
     */
    public void stopReplay() {
        playing = false;

        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    /**
     * Pause replay without resetting index.
     */
    public void pauseReplay() {
        playing = false;

        if (timer != null) {
            timer.stop();
        }
    }

    /**
     * Step forward exactly one move.
     */
    public void stepForward() {
        if (index >= history.size()) return;

        boolean old = playing;
        playing = true;      // temporarily allow playNextMove to run

        playNextMove();

        playing = old;
    }

    /**
     * Apply the next move in history.
     */
    private void playNextMove() {
        if (!playing) return;

        if (index >= history.size()) {
            stopReplay();
            return;
        }

        MoveRecord move = history.get(index);

        if (move instanceof RandomizeRecord rr) {
            board.loadState(rr.newBoardState);
            board.repaint();
            index++;
            return;
        }

        // normal move
        board.attemptMove(move.fromR, move.fromC, move.toR, move.toC);
        index++;
        board.repaint();
    }

}
