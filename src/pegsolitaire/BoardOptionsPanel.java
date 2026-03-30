package pegsolitaire;

import javax.swing.*;
import java.awt.*;

public class BoardOptionsPanel extends JPanel {

    JLabel sizeLabel;
    JTextField sizeField;
    JButton okButton;
    
    public JRadioButton english;
    public JRadioButton european;
    public JRadioButton diamond;

    public BoardOptionsPanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        // --- Board size input ---
        sizeLabel = new JLabel("Board Size (max 13):");
        sizeField = new JTextField(3);
        sizeField.setText(String.valueOf(PegSolitaire.boardSize)); // show current size

        // --- Board shape radios (future feature) ---
        english  = new JRadioButton("English");
        european = new JRadioButton("Hexagon");
        diamond  = new JRadioButton("Diamond");

        ButtonGroup shapeGroup = new ButtonGroup();
        shapeGroup.add(english);
        shapeGroup.add(european);
        shapeGroup.add(diamond);

        english.setSelected(true); // default

        // --- OK button ---
        okButton = new JButton("OK");
        add(okButton);

        // Add components
        add(sizeLabel);
        add(sizeField);
        add(english);
        add(european);
        add(diamond);
    }
}
