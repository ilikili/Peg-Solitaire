package pegsolitaire;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ManualPlay extends Play {

    private MouseAdapter mouse;

    public ManualPlay(PegBoard board) {
        super(board);
    }

    @Override
    public void activate() {
        mouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                int c = e.getX() / board.getCellSize();
                int r = e.getY() / board.getCellSize();

                if (!board.isInside(r, c)) return;
                if (!board.isValidCell(r, c)) return;

                // Select peg
                if (board.hasPeg(r, c)) {
                    board.setSelected(r, c);
                    board.repaint();
                    return;
                }

                // Attempt move
                if (board.hasSelection() && !board.hasPeg(r, c)) {

                    int fromR = board.getSelectedRow();
                    int fromC = board.getSelectedCol();
                    int toR   = r;
                    int toC   = c;

                    // Compute the jumped over peg before the move
                    int overR = (fromR + toR) / 2;
                    int overC = (fromC + toC) / 2;

                    // Check if the move is legal
                    boolean legal =
                    	    board.isValidCell(overR, overC) &&
                    	    board.hasPeg(fromR, fromC) &&
                    	    board.hasPeg(overR, overC) &&
                    	    !board.hasPeg(toR, toC);

                    // Perform the move
                    board.attemptMove(fromR, fromC, toR, toC);

                    // Only record if it was a legal jump
                    if (legal) {
                        PegSolitaire.history.add(
                            new MoveRecord(fromR, fromC, overR, overC, toR, toC, System.currentTimeMillis())
                        );
                    }
                    board.repaint();
                }

            }
        };

        board.addMouseListener(mouse);
    }

    @Override
    public void deactivate() {
        if (mouse != null) {
            board.removeMouseListener(mouse);
        }
    }
}
