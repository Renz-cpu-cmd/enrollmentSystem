package ui.screens;

import ui.MobileFrame;
import ui.Screen;

import javax.swing.*;
import java.awt.*;

public class StatusDeclarationScreen extends JPanel {

    public StatusDeclarationScreen() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Main content panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // --- Year Level & Semester Confirmation ---
        mainPanel.add(new JLabel("You are eligible to enroll for:"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Year Level
        mainPanel.add(new JLabel("Year Level:"));
        JComboBox<String> yearLevelComboBox = new JComboBox<>(new String[]{"2nd Year (Retained)", "3rd Year (Promoted)"});
        // In a real app, this would be auto-selected
        mainPanel.add(yearLevelComboBox);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Semester
        mainPanel.add(new JLabel("Semester:"));
        JTextField semesterField = new JTextField("1st Semester");
        semesterField.setEditable(false);
        mainPanel.add(semesterField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Status Declaration ---
        mainPanel.add(new JLabel("Declare Your Status:"));
        JRadioButton regularButton = new JRadioButton("Regular (I passed all my subjects)");
        JRadioButton irregularButton = new JRadioButton("Irregular (I have failed/dropped subjects)");
        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(regularButton);
        statusGroup.add(irregularButton);

        mainPanel.add(regularButton);
        mainPanel.add(irregularButton);

        add(mainPanel, BorderLayout.CENTER);

        // Load Subjects Button
        JButton loadSubjectsButton = new JButton("Load Subjects");
        add(loadSubjectsButton, BorderLayout.SOUTH);

        // Action Listener for the button
        loadSubjectsButton.addActionListener(e -> {
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                if (regularButton.isSelected()) {
                    frame.showScreen(Screen.REGULAR_PATH, true); // Screen C-4A
                } else if (irregularButton.isSelected()) {
                    frame.showScreen(Screen.IRREGULAR_PATH, true); // Screen C-4B
                } else {
                    JOptionPane.showMessageDialog(this, "Please declare your status.");
                }
            }
        });
    }
}