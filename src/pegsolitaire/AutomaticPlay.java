package pegsolitaire;

public class AutomaticPlay extends Play {

    private boolean running = false;

    public AutomaticPlay(PegBoard board) {
        super(board);
    }

    @Override
    public void activate() {
        running = true;

        new Thread(() -> {
            while (running) {

                // Try to find and perform one legal move
                boolean moved = performOneMove();

                board.repaint();

                // If no move was found → stop
                if (!moved) {
                    running = false;
                    break;
                }

                // Wait 1 second before next move
                try { Thread.sleep(1000); } catch (Exception e) {}
            }
        }).start();
    }

    @Override
    public void deactivate() {
        running = false;
    }

    /**
     * Scans the board for the first legal move and performs it.
     * Returns true if a move was made, false if no moves exist.
     */
    private boolean performOneMove() {
        System.out.println("Checking moves...");

        int size = board.getBoardSize();

        // Loop through all cells
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {

                // Must be a peg to start a move
                if (!board.hasPeg(r, c)) continue;

                // Try all 4 directions
                if (isLegal(r, c, r - 2, c)) {
                    board.attemptMove(r, c, r - 2, c);

                    // RECORD MOVE HERE
                    int overR = (r + (r - 2)) / 2;
                    int overC = c;
                    PegSolitaire.history.add(
                        new MoveRecord(r, c, overR, overC, r - 2, c, System.currentTimeMillis())
                    );

                    System.out.println("Trying move from " + r + "," + c);
                    return true;
                }

                // Down: (r+2, c)
                if (isLegal(r, c, r + 2, c)) {
                    board.attemptMove(r, c, r + 2, c);

                    // RECORD MOVE HERE
                    int overR = (r + (r + 2)) / 2;
                    int overC = c;
                    PegSolitaire.history.add(
                        new MoveRecord(r, c, overR, overC, r + 2, c, System.currentTimeMillis())
                    );

                    System.out.println("Trying move from " + r + "," + c);
                    return true;
                }

                // Left: (r, c-2)
                if (isLegal(r, c, r, c - 2)) {
                    board.attemptMove(r, c, r, c - 2);

                    // RECORD MOVE HERE
                    int overR = r;
                    int overC = (c + (c - 2)) / 2;
                    PegSolitaire.history.add(
                        new MoveRecord(r, c, overR, overC, r, c - 2, System.currentTimeMillis())
                    );

                    System.out.println("Trying move from " + r + "," + c);
                    return true;
                }

                // Right: (r, c+2)
                if (isLegal(r, c, r, c + 2)) {
                    board.attemptMove(r, c, r, c + 2);

                    // RECORD MOVE HERE
                    int overR = r;
                    int overC = (c + (c + 2)) / 2;
                    PegSolitaire.history.add(
                        new MoveRecord(r, c, overR, overC, r, c + 2, System.currentTimeMillis())
                    );

                    System.out.println("Trying move from " + r + "," + c);
                    return true;
                }
            }
        }

        return false; // no moves found
    }

    /**
     * Checks if jumping from (sr,sc) to (tr,tc) is legal.
     */
    private boolean isLegal(int sr, int sc, int tr, int tc) {

        // Must be inside board
        if (!board.isInside(tr, tc)) return false;

        // Must be valid target cell
        if (!board.isValidCell(tr, tc)) return false;

        // Target must be empty
        if (board.hasPeg(tr, tc)) return false;

        // Must be exactly 2 spaces away in straight line
        int dr = tr - sr;
        int dc = tc - sc;

        if (!((Math.abs(dr) == 2 && dc == 0) ||
              (Math.abs(dc) == 2 && dr == 0))) {
            return false;
        }

        // Middle cell must contain a peg
        int mr = (sr + tr) / 2;
        int mc = (sc + tc) / 2;

        return board.hasPeg(mr, mc);
    }

    public boolean isRunning() {
        return running;
    }
}
