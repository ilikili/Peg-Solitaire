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
                    board.attemptMove(board.getSelectedRow(),
                                      board.getSelectedCol(),
                                      r, c);
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
