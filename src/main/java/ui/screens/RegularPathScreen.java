package ui.screens;

import ui.MobileFrame;
import ui.Screen;
import util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegularPathScreen extends JPanel {
    public RegularPathScreen() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Regular Enrollment Path", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Course List Panel
        JPanel courseListPanel = new JPanel();
        courseListPanel.setLayout(new BoxLayout(courseListPanel, BoxLayout.Y_AXIS));
        courseListPanel.setBorder(BorderFactory.createTitledBorder("3rd Year, 1st Semester Courses (BSIT)"));

        // In a real app, this would be dynamically fetched
        String[] courses = {
                "IT-311: Networking 1",
                "IT-312: Web Development",
                "IT-313: Software Engineering",
                "GE-105: The Contemporary World",
                "PE-3: Physical Education 3"
        };
        for (String course : courses) {
            JLabel courseLabel = new JLabel(course);
            courseLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            courseListPanel.add(courseLabel);
        }

        add(new JScrollPane(courseListPanel), BorderLayout.CENTER);

        // Confirmation Button
        JButton confirmButton = new JButton("Confirm Section & Proceed to Assessment");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 16));
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(confirmButton, BorderLayout.SOUTH);

        confirmButton.addActionListener(e -> {
            // Save the list of subjects to the session
            List<String> enrolledSubjects = new ArrayList<>(Arrays.asList(courses));
            SessionManager.getInstance().setEnrolledSubjects(enrolledSubjects);

            JOptionPane.showMessageDialog(this, "Section confirmed! Proceeding to Assessment of Fees.");
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.showScreen(Screen.ASSESSMENT_OF_FEES, true);
            }
        });
    }
}