package pegsolitaire;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PegBoard extends JPanel implements MouseListener {

    private int size;                // board size (5–13)
    private int cellSize = 50;       // pixel size of each square
    private boolean[][] pegs;        // true = peg present
    private Point selected = null;   // currently selected peg
    
    public enum Shape { ENGLISH, EUROPEAN, DIAMOND }
    public Shape shape = Shape.ENGLISH;
    
    public enum WinState {
        NONE,
        WIN,
        PERFECT_WIN
    }

    public void setShape(Shape s) {
        this.shape = s;
        resetBoard();
    }

    public PegBoard(int size) {
        this.size = Math.min(size, 13);
        this.size = Math.max(this.size, 5);
        this.setPreferredSize(new Dimension(this.size * cellSize, this.size * cellSize));
        this.addMouseListener(this);
        resetBoard();
    }

    public void resetBoard() {
        pegs = new boolean[size][size];

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                pegs[r][c] = isValidCell(r, c);
            }
        }

        // Remove center peg
        int mid = size / 2;
        pegs[mid][mid] = false;

        selected = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {

                int x = c * cellSize;
                int y = r * cellSize;

                // Draw hollow yellow square
                g2.setColor(Color.YELLOW);
                if (isValidCell(r,c))
                	g2.drawRect(x, y, cellSize, cellSize);

                // Draw peg if present
                if (pegs[r][c]) {
                    g2.setColor(Color.YELLOW);
                    g2.fillOval(x + 10, y + 10, cellSize - 20, cellSize - 20);
                }

                // Highlight selected peg
                if (selected != null && selected.x == r && selected.y == c) {
                    g2.setColor(Color.ORANGE);
                    g2.drawRect(x + 3, y + 3, cellSize - 6, cellSize - 6);
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int c = e.getX() / cellSize;
        int r = e.getY() / cellSize;

        if (r < 0 || c < 0 || r >= size || c >= size) return;
        if (!isValidCell(r, c)) return; // ignore clicks in invalid cells

        // If clicking a peg → select it
        if (pegs[r][c]) {
            selected = new Point(r, c);
            repaint();
            return;
        }

        // If clicking an empty square → try to move
        if (selected != null && !pegs[r][c]) {
            attemptMove(selected.x, selected.y, r, c);
        }
    }

    private void attemptMove(int sr, int sc, int tr, int tc) {

        // All involved cells must be valid English cells
        if (!isValidCell(sr, sc) || !isValidCell(tr, tc)) {
            selected = null;
            repaint();
            return;
        }

        int dr = tr - sr;
        int dc = tc - sc;

        // Vertical move
        if (Math.abs(dr) == 2 && dc == 0) {
            int midR = sr + dr / 2;
            int midC = sc;

            if (!isValidCell(midR, midC)) {
                selected = null;
                repaint();
                return;
            }

            if (pegs[sr][sc] && pegs[midR][midC] && !pegs[tr][tc]) {
                pegs[sr][sc] = false;
                pegs[midR][midC] = false;
                pegs[tr][tc] = true;
            }
        }

        // Horizontal move
        if (Math.abs(dc) == 2 && dr == 0) {
            int midR = sr;
            int midC = sc + dc / 2;

            if (!isValidCell(midR, midC)) {
                selected = null;
                repaint();
                return;
            }

            if (pegs[sr][sc] && pegs[midR][midC] && !pegs[tr][tc]) {
                pegs[sr][sc] = false;
                pegs[midR][midC] = false;
                pegs[tr][tc] = true;
            }
        }

        selected = null;
        checkWin();
        repaint();
    }

    public int getCellSize() {
        return cellSize;
    }

    private boolean isEnglishCell(int r, int c, int size) {
        int mid = size / 2;

        boolean inMiddleRowBand = (r >= mid - 1 && r <= mid + 1);
        boolean inMiddleColBand = (c >= mid - 1 && c <= mid + 1);

        return inMiddleRowBand || inMiddleColBand;
    }
    private boolean isEuropeanCell(int r, int c, int size) {
        int mid = size / 2;
        int dr = Math.abs(r - mid);
        int dc = Math.abs(c - mid);

        // Manhattan distance rule
        return (dr + dc) <= (mid + 1);
    }
    private boolean isDiamondCell(int r, int c, int size) {
        int mid = size / 2;
        int dr = Math.abs(r - mid);
        int dc = Math.abs(c - mid);

        // Manhattan distance diamond
        return (dr + dc) <= mid;
    }

    public boolean isValidCell(int r, int c) {
        return switch (shape) {
            case ENGLISH  -> isEnglishCell(r, c, size);
            case EUROPEAN -> isEuropeanCell(r, c, size);
            case DIAMOND -> isDiamondCell(r, c, size);
        };
    }

    public void setBoardSize(int newSize) {
        this.size = Math.min(newSize, 13);
        this.size = Math.max(this.size, 5);
        this.setPreferredSize(new Dimension(this.size * cellSize, this.size * cellSize));
        resetBoard();
        revalidate();
        repaint();
    }
    
    public void checkWin() {
        int count = 0;
        int lastR = -1, lastC = -1;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (pegs[r][c]) {
                    count++;
                    lastR = r;
                    lastC = c;

                    if (count > 1) return; // no win
                }
            }
        }

        // If exactly one peg remains
        if (count == 1) {
            // Check perfect win
            int mid = size / 2;
            if (lastR == mid && lastC == mid) {
                JOptionPane.showMessageDialog(this, "Perfect win! You finished in the center!");
            } else {
                JOptionPane.showMessageDialog(this, "You won the game!");
            }
        }
    }
    
    private void checkPerfectWin() {
        int mid = size / 2;

        // If the center has the only peg → perfect win
        if (pegs[mid][mid]) {
            JOptionPane.showMessageDialog(this, "Perfect win! You finished in the center!");
        }
    }
    
    public WinState getWinState() {
        int count = 0;
        int lastR = -1, lastC = -1;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (pegs[r][c]) {
                    count++;
                    lastR = r;
                    lastC = c;

                    if (count > 1) return WinState.NONE;
                }
            }
        }

        if (count == 1) {
            int mid = size / 2;
            if (lastR == mid && lastC == mid) {
                return WinState.PERFECT_WIN;
            }
            return WinState.WIN;
        }
        return WinState.NONE;
    }

    public boolean[][] getPegs() {
        return pegs;
    }

    // Unused but required
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
