package pegsolitaire;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pegsolitaire.PegBoard.Shape;

public class PegSolitaire {

    public static PegBoard board;
    public static int boardSize = 7; // default
    public static JPanel background;

    public static void main(String[] args) {

        // Create frame
        JFrame frame = new JFrame("Peg Solitaire");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true); // Fullscreen without borders
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen

        // Background panel
        background = new ImageBackgroundPanel("background.png");
        background.setLayout(null);
        frame.setContentPane(background);

        // Create initial board
        board = new PegBoard(boardSize);
        board.setOpaque(false);
        board.setBounds(600, 200, 700, 700);
        background.add(board);

        // Title label
        JLabel titleLabel = new JLabel("Arc Solitaire");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 72)); // fallback
        titleLabel.setBounds(470, 60, 1000, 60);
        titleLabel.setOpaque(false);
        titleLabel.setForeground(new Color(255, 255, 20));
        background.add(titleLabel);

        // Load custom font
        try {
            File fontFile = new File("fonts/ByteBounce.ttf");
            Font arcade = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(140f);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(arcade);

            titleLabel.setFont(arcade);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Reset button
        JButton reset = new JButton("Reset");
        reset.setBounds(40, 100, 120, 30);
        reset.setFocusable(false);
        reset.addActionListener(e -> board.resetBoard());
        background.add(reset);

        // Options panel (board size + shapes)
        BoardOptionsPanel options = new BoardOptionsPanel();
        options.setBounds(40, 200, 300, 200);
        background.add(options);

        // OK button logic (board rebuild)
        options.okButton.addActionListener(e -> {
            try {
                int newSize = Integer.parseInt(options.sizeField.getText().trim());
                newSize = Math.min(newSize, 13);
                newSize = Math.max(newSize, 5);

                board.setBoardSize(newSize);

                // Apply shape selection
                if (options.english.isSelected()) {
                    board.setShape(PegBoard.Shape.ENGLISH);
                } else if (options.european.isSelected()) {
                    board.setShape(PegBoard.Shape.EUROPEAN);
                } else if (options.diamond.isSelected()) {
                    board.setShape(PegBoard.Shape.DIAMOND);
                }

                // ⭐ Re-center board
                int cell = board.getCellSize();
                int grid = newSize * cell;
                int x = (background.getWidth() - grid) / 2;
                int y = (background.getHeight() - grid) / 2;
                board.setBounds(x, y, grid, grid);

                background.revalidate();
                background.repaint();

            } catch (NumberFormatException ex) {
                System.out.println("Invalid size input");
            }
        });

        // Checkbox
        JCheckBox checkBox = new JCheckBox("Record");
        checkBox.setFont(new Font("Verdana", Font.BOLD, 16));
        checkBox.setBounds(40, 420, 200, 40);
        checkBox.setOpaque(false);
        background.add(checkBox);

        frame.setVisible(true);
    }
}

class ImageBackgroundPanel extends JPanel {

    private Image backgroundImage;

    public ImageBackgroundPanel(String imagePath) {
        backgroundImage = new ImageIcon(imagePath).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Scale image to fill the window
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
