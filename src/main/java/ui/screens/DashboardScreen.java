package ui.screens;

import model.Student;
import ui.MobileFrame;
import ui.Screen;
import util.SessionManager;

import javax.swing.*;
import java.awt.*;

public class DashboardScreen extends JPanel {

    private JButton enrollButton;
    private boolean isAccountingCleared = true; // Placeholder
    private boolean areGradesSubmitted = true; // Placeholder
    private boolean hasNoViolations = true;    // Placeholder

    public DashboardScreen() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Profile
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        profilePanel.add(new JLabel("User Photo")); // Placeholder
        Student loggedInStudent = SessionManager.getInstance().getCurrentStudent();
        String profileText = "Welcome";
        if (loggedInStudent != null) {
            profileText = loggedInStudent.getFirstName() + " " + loggedInStudent.getLastName() + " - 2nd Year - BSIT"; // Year/program is static for now
        }
        profilePanel.add(new JLabel(profileText));
        add(profilePanel, BorderLayout.NORTH);

        // Status Cards
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.add(createStatusCard("Accounting", isAccountingCleared ? "Cleared" : "Deficient", isAccountingCleared ? Color.GREEN : Color.RED));
        statusPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statusPanel.add(createStatusCard("Grades", areGradesSubmitted ? "All Grades Submitted" : "Incomplete", areGradesSubmitted ? Color.GREEN : Color.RED));
        statusPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statusPanel.add(createStatusCard("Violations", hasNoViolations ? "No Prefect Records" : "With Record", hasNoViolations ? Color.GREEN : Color.RED));
        add(statusPanel, BorderLayout.CENTER);

        // Enroll Now Button
        enrollButton = new JButton("Start Enrollment");
        add(enrollButton, BorderLayout.SOUTH);

        // Check statuses and update button state
        updateEnrollmentButtonState();

        enrollButton.addActionListener(e -> {
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.showScreen(Screen.STATUS_DECLARATION, true);
            }
        });
    }

    private JPanel createStatusCard(String title, String status, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel statusLabel = new JLabel(status);
        statusLabel.setForeground(color);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        card.add(titleLabel, BorderLayout.WEST);
        card.add(statusLabel, BorderLayout.EAST);
        return card;
    }

    private void updateEnrollmentButtonState() {
        if (isAccountingCleared && areGradesSubmitted && hasNoViolations) {
            enrollButton.setEnabled(true);
        } else {
            enrollButton.setEnabled(false);
            enrollButton.setText("Enrollment Disabled");
        }
    }

    // This method can be called to refresh the screen with new data
    public void refreshData() {
        // In a real app, you'd fetch the latest student and status data here
        // For now, we'll just re-validate the button state
        updateEnrollmentButtonState();
    }
}