
package ui.screens;

import ui.MobileFrame;
import util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RegularPathScreen extends JPanel {

    // Store data as a field to access it in the action listener
    private final Object[][] data = {
            {"IT-311", "Networking 1"},
            {"IT-312", "Web Development"},
            {"IT-313", "Software Engineering"},
            {"GE-105", "Purposive Communication"},
            {"PE-3", "Physical Education 3"}
    };

    public RegularPathScreen() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Info Label
        JLabel infoLabel = new JLabel("Based on your Regular status, you are assigned to Block 3-A.");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(infoLabel, BorderLayout.NORTH);

        // Subjects Table
        String[] columnNames = {"Course Code", "Description"};
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells not editable
            }
        };

        JTable subjectsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(subjectsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Proceed Button
        JButton proceedButton = new JButton("Proceed to Assessment");
        add(proceedButton, BorderLayout.SOUTH);

        proceedButton.addActionListener(e -> {
            // Create a list of subjects from the table data
            List<String> enrolledSubjects = new ArrayList<>();
            for (Object[] row : data) {
                enrolledSubjects.add(row[0] + " - " + row[1]);
            }

            // Save the list to the SessionManager
            SessionManager.getInstance().setEnrolledSubjects(enrolledSubjects);

            // Navigate to the next screen
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.showScreen("AssessmentOfFees");
            }
        });
    }
}
