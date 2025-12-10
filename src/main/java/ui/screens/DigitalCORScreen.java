
package ui.screens;

import model.Student;
import ui.MobileFrame;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class DigitalCORScreen extends JPanel {

    public DigitalCORScreen() {
        // Fetch data from SessionManager
        SessionManager session = SessionManager.getInstance();
        Student student = session.getCurrentStudent();
        List<String> enrolledSubjects = session.getEnrolledSubjects();
        Map<String, String> assessedFees = session.getAssessedFees();

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // --- Header ---
        JLabel headerLabel = new JLabel("OFFICIALLY ENROLLED", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(new Color(0, 100, 0)); // Dark Green
        add(headerLabel, BorderLayout.NORTH);

        // --- Main Content Area ---
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBorder(new LineBorder(Color.GRAY));

        // --- Student Info Section ---
        mainContent.add(createSectionTitle("Student Information"));
        mainContent.add(createDetailRow("Student No:", student != null ? student.getStudentId() : "N/A"));
        mainContent.add(createDetailRow("Student Name:", student != null ? student.getFirstName() + " " + student.getLastName() : "N/A"));
        mainContent.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- Enrolled Subjects Section ---
        mainContent.add(createSectionTitle("Enrolled Subjects"));
        if (enrolledSubjects != null && !enrolledSubjects.isEmpty()) {
            for (String subject : enrolledSubjects) {
                mainContent.add(createDetailRow(subject.split(" - ")[0] + ":", subject.split(" - ")[1]));
            }
        } else {
            mainContent.add(new JLabel("  No subjects enrolled."));
        }
        mainContent.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- Assessed Fees Section ---
        mainContent.add(createSectionTitle("Assessed Fees"));
        if (assessedFees != null && !assessedFees.isEmpty()) {
            for (Map.Entry<String, String> fee : assessedFees.entrySet()) {
                 if (fee.getKey().equals("TOTAL DUE")) continue; // Skip total, we'll add it last
                mainContent.add(createDetailRow(fee.getKey() + ":", fee.getValue()));
            }
            mainContent.add(Box.createRigidArea(new Dimension(0, 5)));
            // Add a separator and the total
            JSeparator separator = new JSeparator();
            separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            mainContent.add(separator);
            mainContent.add(Box.createRigidArea(new Dimension(0, 5)));
            mainContent.add(createDetailRow( "TOTAL DUE:", assessedFees.getOrDefault("TOTAL DUE", "0.00"), true));
        }

        add(new JScrollPane(mainContent), BorderLayout.CENTER);

        // --- Finish Button ---
        JButton finishButton = new JButton("Finish");
        add(finishButton, BorderLayout.SOUTH);

        finishButton.addActionListener(e -> {
            // Clear the session
            SessionManager.getInstance().clearSession();

            // Go back to the portal gateway
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.showScreen("PortalGateway");
            }
        });
    }

    private JPanel createDetailRow(String label, String value) {
        return createDetailRow(label, value, false);
    }

    private JPanel createDetailRow(String label, String value, boolean isBold) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBorder(new EmptyBorder(2, 10, 2, 10)); // Padding
        row.setOpaque(false);

        JLabel labelComponent = new JLabel(label);
        JLabel valueComponent = new JLabel(value);

        if (isBold) {
            labelComponent.setFont(labelComponent.getFont().deriveFont(Font.BOLD));
            valueComponent.setFont(valueComponent.getFont().deriveFont(Font.BOLD));
        }

        row.add(labelComponent, BorderLayout.WEST);
        row.add(valueComponent, BorderLayout.EAST);
        return row;
    }

    private JLabel createSectionTitle(String title) {
        JLabel titleLabel = new JLabel("  " + title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(10, 0, 5, 0));
        return titleLabel;
    }
}

