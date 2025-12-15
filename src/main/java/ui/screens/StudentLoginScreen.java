package ui.screens;

import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.theme.RoundedButton;
import ui.theme.Theme;
import util.Navigation;

import javax.swing.*;
import java.awt.*;

public class StudentLoginScreen extends JPanel implements ScreenView {

    private JTextField studentIdField;
    private JPasswordField passwordField;

    public StudentLoginScreen() {
        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(Theme.PADDING_BORDER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(createLoginCard(), gbc);
    }

    private JComponent createLoginCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 25), 1, true),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)));

        JLabel logo = new JLabel(UIManager.getIcon("OptionPane.informationIcon"));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(logo);

        card.add(Box.createVerticalStrut(12));
        JLabel title = new JLabel("Student Portal", SwingConstants.CENTER);
        title.setFont(Theme.SUBHEADING_FONT.deriveFont(Font.BOLD, 20f));
        title.setForeground(Theme.TEXT_PRIMARY_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);

        card.add(Box.createVerticalStrut(16));
        card.add(createField("Student ID", studentIdField = new JTextField()));
        card.add(Box.createVerticalStrut(12));
        card.add(createField("Password", passwordField = new JPasswordField()));

        card.add(Box.createVerticalStrut(20));
        RoundedButton loginButton = RoundedButton.primary("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> Navigation.to(StudentLoginScreen.this, Screen.DASHBOARD));
        card.add(loginButton);

        card.add(Box.createVerticalStrut(10));
        JLabel forgot = new JLabel("Forgot Password?", SwingConstants.CENTER);
        forgot.setFont(Theme.LABEL_FONT);
        forgot.setForeground(Theme.PRIMARY_COLOR);
        forgot.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(forgot);

        return card;
    }

    private JComponent createField(String labelText, JComponent input) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);
        label.setFont(Theme.LABEL_FONT);
        label.setForeground(Theme.TEXT_SECONDARY_COLOR);
        panel.add(label);

        input.setFont(Theme.BODY_FONT);
        input.setBorder(Theme.LINE_BORDER);
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        if (input instanceof JTextField field) {
            field.putClientProperty("JTextField.placeholderText",
                "Student ID".equals(labelText) ? "2023-12345" : "");
        }
        if (input instanceof JPasswordField passwordField) {
            passwordField.putClientProperty("JTextField.placeholderText", "••••••••");
        }
        panel.add(Box.createVerticalStrut(6));
        panel.add(input);
        return panel;
    }

    @Override
    public void onEnter(NavigationContext context) {
        studentIdField.setText("");
        passwordField.setText("");
    }

    @Override
    public void onLeave() {
        // No cleanup necessary yet.
    }
}
