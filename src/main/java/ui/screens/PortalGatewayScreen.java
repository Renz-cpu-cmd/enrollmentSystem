
package ui.screens;

import ui.MobileFrame;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class PortalGatewayScreen extends JPanel {

    public PortalGatewayScreen() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);

        // Header (Logo and Welcome Text)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Theme.BACKGROUND_COLOR);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

        JLabel logoLabel = new JLabel("[SCHOOL LOGO]"); // Placeholder for logo
        logoLabel.setFont(Theme.HEADING_FONT);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Welcome to the Enrollment System");
        welcomeLabel.setFont(Theme.BODY_FONT);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(logoLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(welcomeLabel);

        add(headerPanel, BorderLayout.NORTH);

        // UI Components (Buttons)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Theme.BACKGROUND_COLOR);
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 50, 10, 50);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JButton freshmanButton = new JButton("Incoming Freshman");
        JButton transfereeButton = new JButton("Transferee");
        JButton studentPortalButton = new JButton("Student Portal");

        transfereeButton.setBackground(Theme.SURFACE_COLOR);
        transfereeButton.setForeground(Theme.TEXT_SECONDARY_COLOR);

        gbc.gridy = 0;
        buttonPanel.add(freshmanButton, gbc);

        gbc.gridy = 1;
        buttonPanel.add(transfereeButton, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(30, 50, 10, 50); // Add extra top margin
        buttonPanel.add(studentPortalButton, gbc);

        add(buttonPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("School Academic Year 2025-2026", SwingConstants.CENTER);
        footerLabel.setFont(Theme.LABEL_FONT);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(footerLabel, BorderLayout.SOUTH);

        // Action Listeners
        freshmanButton.addActionListener(e -> {
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.showScreen("DataPrivacy"); // Flow A
            }
        });

        transfereeButton.addActionListener(e -> {
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                // Flow B is similar to Flow A
                frame.showScreen("DataPrivacy");
            }
        });

        studentPortalButton.addActionListener(e -> {
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.showScreen("StudentLogin"); // Flow C
            }
        });
    }
}
