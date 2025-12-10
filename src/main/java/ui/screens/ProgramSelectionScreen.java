
package ui.screens;

import ui.MobileFrame;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ProgramSelectionScreen extends JPanel {

    private JComboBox<String> collegeComboBox;
    private JComboBox<String> programComboBox;
    private JPanel infoCard;
    private JLabel courseDetailsLabel;

    private final Map<String, String[]> programsByCollege = new HashMap<>();
    private final Map<String, String> programDetails = new HashMap<>();

    public ProgramSelectionScreen() {
        // --- Data Initialization ---
        programsByCollege.put("College of Computer Studies", new String[]{"BS in Information Technology", "BS in Computer Science"});
        programsByCollege.put("College of Business and Accountancy", new String[]{"BS in Accountancy", "BS in Business Administration"});

        programDetails.put("BS in Information Technology", "4 Years, 182 Units");
        programDetails.put("BS in Computer Science", "4 Years, 180 Units");
        programDetails.put("BS in Accountancy", "5 Years, 200 Units");
        programDetails.put("BS in Business Administration", "4 Years, 175 Units");

        // --- UI Setup ---
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Main panel with vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // College Selection
        mainPanel.add(new JLabel("Select College:"));
        collegeComboBox = new JComboBox<>(programsByCollege.keySet().toArray(new String[0]));
        mainPanel.add(collegeComboBox);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer

        // Program Selection
        mainPanel.add(new JLabel("Select Program:"));
        programComboBox = new JComboBox<>();
        mainPanel.add(programComboBox);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer

        // Info Card
        infoCard = new JPanel(new BorderLayout());
        infoCard.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Program Details", TitledBorder.LEFT, TitledBorder.TOP));
        courseDetailsLabel = new JLabel("Please select a program to see details.");
        courseDetailsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoCard.add(courseDetailsLabel, BorderLayout.CENTER);
        mainPanel.add(infoCard);

        add(mainPanel, BorderLayout.CENTER);

        // Proceed Button
        JButton proceedButton = new JButton("Proceed to Block Sectioning");
        add(proceedButton, BorderLayout.SOUTH);

        // --- Action Listeners ---
        collegeComboBox.addActionListener(e -> updateProgramComboBox());
        programComboBox.addActionListener(e -> updateProgramDetails());

        proceedButton.addActionListener(e -> {
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                // This will navigate to BlockSectioningScreen once it's created
                frame.showScreen("BlockSectioning"); 
            }
        });

        // Initial population
        updateProgramComboBox();
        updateProgramDetails();
    }

    private void updateProgramComboBox() {
        String selectedCollege = (String) collegeComboBox.getSelectedItem();
        if (selectedCollege != null) {
            programComboBox.setModel(new DefaultComboBoxModel<>(programsByCollege.get(selectedCollege)));
        }
    }

    private void updateProgramDetails() {
        String selectedProgram = (String) programComboBox.getSelectedItem();
        if (selectedProgram != null) {
            courseDetailsLabel.setText("<html><p>" + programDetails.get(selectedProgram) + "</p></html>");
        } else {
            courseDetailsLabel.setText("Please select a program to see details.");
        }
    }
}
