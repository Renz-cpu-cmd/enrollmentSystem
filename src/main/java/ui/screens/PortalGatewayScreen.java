package ui.screens;

import ui.MobileFrame;
import ui.Screen;
import ui.theme.CardPanel;
import ui.theme.IconCreator;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

        CardPanel newStudentButton = createGatewayButton("New/Continuing Student", "Start or continue your application.", IconCreator.PERSON_ADD_ICON, e -> {
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.showScreen(Screen.DATA_PRIVACY, true); // Flow A
            }
        });

        CardPanel studentPortalButton = createGatewayButton("Returning Student (Portal)", "Access your student account.", IconCreator.LOGIN_ICON, e -> {
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.showScreen(Screen.STUDENT_LOGIN, true); // Flow C
            }
        });

        gbc.gridy = 0;
        buttonPanel.add(newStudentButton, gbc);

        gbc.gridy = 1;
        buttonPanel.add(studentPortalButton, gbc);

        add(buttonPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("School Academic Year 2025-2026", SwingConstants.CENTER);
        footerLabel.setFont(Theme.LABEL_FONT);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(footerLabel, BorderLayout.SOUTH);
    }

    private CardPanel createGatewayButton(String title, String description, ImageIcon icon, java.awt.event.ActionListener actionListener) {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(10, 0));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel textContainer = new JLabel("<html><b>" + title + "</b><br><p style='width:200px;'>" + description + "</p></html>");
        textContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textContainer, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                actionListener.actionPerformed(new java.awt.event.ActionEvent(evt.getSource(), java.awt.event.ActionEvent.ACTION_PERFORMED, null));
            }
        });

        return card;
    }
}