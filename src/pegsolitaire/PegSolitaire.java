package pegsolitaire;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PegSolitaire {

    public static PegBoard board;
    public static int boardSize = 7; // default
    public static JPanel background;
    
    public static Play mode;
    public static AutomaticPlay auto;
    public static ManualPlay manual;
    
    public static JCheckBox autoBox;
    
    public static MoveHistory history = new MoveHistory();
    public static boolean[][] initialBoard;
    public static ReplayController replayController;
    
    public static JCheckBox recordBox;



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
        // Set bounds BEFORE adding to background
        board.setBounds(600-7, 300+5, 700, 700);
        background.add(board);  // add AFTER bounds
        PegSolitaire.initialBoard = board.copyState();
        replayController = new ReplayController(board, history);
        PegSolitaire.history.clear();
        manual = new ManualPlay(board);
        auto = new AutomaticPlay(board);
        background.revalidate();
        background.repaint();

        // Start in manual mode
        mode = manual;
        mode.activate();
        
        //Replay buttons
        JButton playReplay = new JButton("Play");
        playReplay.addActionListener(e -> {
            PegSolitaire.enterReplayMode();
            replayController.startReplay();
        });
        playReplay.setBounds(50, 500, 150, 40);
        background.add(playReplay);

        JButton pauseReplay = new JButton("Pause");
        pauseReplay.addActionListener(e -> replayController.pauseReplay());
        pauseReplay.setBounds(50, 550, 150, 40);
        background.add(pauseReplay);

        JButton restartReplay = new JButton("Restart");
        restartReplay.addActionListener(e -> {
            PegSolitaire.enterReplayMode();
            replayController.startReplay();
        });
        restartReplay.setBounds(50, 600, 150, 40);
        background.add(restartReplay);

        JButton stepReplay = new JButton("Step");
        stepReplay.addActionListener(e -> {
            PegSolitaire.enterReplayMode();
            replayController.stepForward();
        });

        stepReplay.setBounds(50, 650, 150, 40);
        background.add(stepReplay);

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
        reset.addActionListener(e -> {
        	board.resetBoard();
        	PegSolitaire.history.clear();
        	PegSolitaire.initialBoard = board.copyState(); 
        });
        //x background.add(reset);

        // Options panel (board size + shapes)
        BoardOptionsPanel options = new BoardOptionsPanel();
        options.setBounds(40, 200, 300, 200);
        background.add(options);
        
	     // Auto-play checkbox
        autoBox = new JCheckBox("Auto Play");
        autoBox.setBounds(60, 280, 150, 30);   // adjust position as needed
        autoBox.setOpaque(false);
        autoBox.setForeground(Color.WHITE);
        background.add(autoBox);
        
        autoBox.addActionListener(e -> {
            // Stop current mode
            mode.deactivate();

            if (autoBox.isSelected()) {
                // Switch to automatic
                mode = auto;
                mode.activate();
            } else {
                // Switch back to manual
                mode = manual;
                mode.activate();
            }
        });
        
        // Randomize button
        JButton randomizeButton = new JButton("Randomize Board");
        randomizeButton.setBounds(60, 320, 150, 30);
        background.add(randomizeButton);
        
        randomizeButton.addActionListener(e -> {
            int originalCount = board.countPegs();
            // Get all valid cells
            List<Point> valid = board.getAllValidCells();
            // Special case: 1 peg allowed to be isolated
            if (originalCount == 1) {
                Collections.shuffle(valid);
                Point p = valid.get(0);

                // Clear board
                for (Point q : valid) board.setPeg(q.x, q.y, false);

                board.setPeg(p.x, p.y, true);
                board.repaint();
                PegSolitaire.restartMode();
                PegSolitaire.history.add(new RandomizeRecord(board.copyState()));
                return;
            }
            List<Point> selection = new ArrayList<>();
            while (true) {
                // Shuffle valid cells
                Collections.shuffle(valid);

                // Pick first N cells
                selection.clear();
                for (int i = 0; i < originalCount; i++) {
                    selection.add(valid.get(i));
                }

                // Check adjacency rule
                boolean ok = true;

                for (Point p : selection) {
                    boolean hasNeighbor = false;

                    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
                    for (int[] d : dirs) {
                        Point n = new Point(p.x + d[0], p.y + d[1]);
                        if (selection.contains(n)) {
                            hasNeighbor = true;
                            break;
                        }
                    }

                    if (!hasNeighbor) {
                        ok = false;
                        break;
                    }
                }

                if (ok) break; // Valid selection found
            }
            // Clear board
            for (Point q : valid) board.setPeg(q.x, q.y, false);
            // Place pegs
            for (Point p : selection) {
                board.setPeg(p.x, p.y, true);
            }
            board.clearSelection();
            board.repaint();
            PegSolitaire.replayController.stopReplay();
            PegSolitaire.history.add(new RandomizeRecord(board.copyState()));
        });

     // OK button logic
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

                // Re-center board
                int cell = board.getCellSize();
                int grid = newSize * cell;
                int x = (background.getWidth() - grid) / 2;
                int y = (background.getHeight() - grid) / 2;
                board.setBounds(x, y, grid, grid);

                history.clear();

                background.revalidate();
                background.repaint();

                PegSolitaire.restartMode();
            } catch (NumberFormatException ex) {
                System.out.println("Invalid size input");
            }
        });

    // Record Checkbox
    recordBox = new JCheckBox("Record");
    recordBox.setFont(new Font("Verdana", Font.BOLD, 16));
    recordBox.setBounds(40, 420, 200, 40);
    recordBox.setOpaque(false);
    //x background.add(recordBox);

    frame.setVisible(true);
    
    } //End main

    public static void restartMode() {
        if (mode != null) {
            mode.deactivate();
        }

        if (autoBox.isSelected()) {
            mode = auto = new AutomaticPlay(board);
        } else {
            mode = manual = new ManualPlay(board);
        }

        mode.activate();
    }
    
    public static void disableAllModes() {
        if (mode != null) mode.deactivate();
    }
    
    public static void enterReplayMode() {
        if (mode != null) mode.deactivate();
        mode = null; // no active play mode during replay
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


