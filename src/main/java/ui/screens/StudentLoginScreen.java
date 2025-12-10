
package ui.screens;

import dao.StudentDAO;
import model.Student;
import ui.MobileFrame;
import ui.theme.CardPanel;
import ui.theme.Theme;
import ui.theme.Toast;
import util.SessionManager;

import javax.swing.*;
import java.awt.*;

public class StudentLoginScreen extends JPanel {

    private JTextField studentIdField;
    private JPasswordField passwordField;

    public StudentLoginScreen() {
        super(new GridBagLayout());
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(Theme.PADDING_BORDER);

        // Content panel that holds the form elements
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        JLabel titleLabel = new JLabel("Student Portal Login", SwingConstants.CENTER);
        titleLabel.setFont(Theme.HEADING_FONT);
        titleLabel.setForeground(Theme.TEXT_PRIMARY_COLOR);
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Card for login fields
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        CardPanel loginCard = new CardPanel(new GridBagLayout());
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.insets = new Insets(10, 15, 10, 15);
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardGbc.weightx = 1.0;

        // Student ID
        cardGbc.gridy = 0;
        loginCard.add(new JLabel("Student ID:"), cardGbc);
        studentIdField = new JTextField();
        cardGbc.gridy = 1;
        loginCard.add(studentIdField, cardGbc);

        // Password
        cardGbc.gridy = 2;
        loginCard.add(new JLabel("Password:"), cardGbc);
        passwordField = new JPasswordField();
        cardGbc.gridy = 3;
        loginCard.add(passwordField, cardGbc);

        contentPanel.add(loginCard, gbc);

        // Buttons
        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        // Secondary button style
        backButton.setBackground(Theme.SURFACE_COLOR);
        backButton.setForeground(Theme.TEXT_SECONDARY_COLOR);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.gridx = 0;
        contentPanel.add(backButton, gbc);

        gbc.gridx = 1;
        contentPanel.add(loginButton, gbc);

        add(contentPanel);

        // Action Listeners
        loginButton.addActionListener(e -> handleLogin());
        backButton.addActionListener(e -> {
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.showScreen("PortalGateway"); // Go back to the main portal screen
            }
        });
    }

    private void handleLogin() {
        String studentId = studentIdField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (studentId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Student ID and Password cannot be empty.", Toast.Type.WARNING, Toast.LENGTH_SHORT);
            return;
        }

        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.getStudentByStudentId(studentId);

        if (student != null && student.getPassword().equals(password)) {
            SessionManager.getInstance().setCurrentStudent(student);
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.showScreen("Dashboard");
            }
        } else {
            Toast.makeText(this, "Invalid Student ID or Password.", Toast.Type.ERROR, Toast.LENGTH_SHORT);
        }
    }
}
