package ui.screens;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.theme.Theme;
import util.Navigation;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StudentLoginScreen extends JPanel implements ScreenView {

    private JTextField studentIdField;
    private JPasswordField passwordField;

    public StudentLoginScreen() {
        setLayout(new GridBagLayout());
        setBackground(new Color(244, 247, 254));

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
        card.setBorder(new CompoundBorder(
            new FlatDropShadowBorder(),
            new CompoundBorder(new FlatRoundBorder(), new EmptyBorder(32, 36, 32, 36))
        ));
        card.setMaximumSize(new Dimension(420, Integer.MAX_VALUE));

        JLabel heading = new JLabel("Welcome Back");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setForeground(Theme.TEXT_PRIMARY_COLOR);
        heading.putClientProperty(FlatClientProperties.STYLE, "font:+4;" +
            "font: bold;" +
            "foreground:#0F172A;");

        JLabel subheading = new JLabel("Please sign in to continue");
        subheading.setAlignmentX(Component.CENTER_ALIGNMENT);
        subheading.putClientProperty(FlatClientProperties.STYLE, "foreground:#6E7582; font:+0;");

        card.add(heading);
        card.add(Box.createVerticalStrut(8));
        card.add(subheading);
        card.add(Box.createVerticalStrut(24));

        card.add(createFieldPanel("Student ID", studentIdField = new JTextField(), "2025-12345"));
        card.add(Box.createVerticalStrut(16));
        card.add(createPasswordPanel());

        card.add(Box.createVerticalStrut(24));
        card.add(createButtonRow());

        JLabel forgot = new JLabel("Forgot password?", SwingConstants.CENTER);
        forgot.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgot.setForeground(new Color(70, 105, 180));
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.add(Box.createVerticalStrut(12));
        card.add(forgot);

        return card;
    }

    private JComponent createFieldPanel(String label, JTextField field, String placeholder) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel labelComp = new JLabel(label);
        labelComp.setForeground(Theme.TEXT_PRIMARY_COLOR);
        panel.add(labelComp);

        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.putClientProperty(FlatClientProperties.STYLE, "arc:16;" +
            "focusWidth:2; innerFocusWidth:1;" +
            "background:#F6F8FB;" +
            "selectionBackground:#0C5CB1;" +
            "selectionForeground:#FFFFFF;");

        panel.add(Box.createVerticalStrut(6));
        panel.add(field);
        return panel;
    }

    private JComponent createPasswordPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Password");
        label.setForeground(Theme.TEXT_PRIMARY_COLOR);
        panel.add(label);

        passwordField = new JPasswordField();
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "••••••••");
        passwordField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        passwordField.putClientProperty(FlatClientProperties.STYLE,
            "arc:16; focusWidth:2; innerFocusWidth:1; background:#F6F8FB;" +
                "showRevealButton:true;" +
                "selectionBackground:#0C5CB1; selectionForeground:#FFFFFF;");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        panel.add(Box.createVerticalStrut(6));
        panel.add(passwordField);
        return panel;
    }

    private JComponent createButtonRow() {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        JButton backButton = new JButton("Back to Gateway");
        backButton.putClientProperty(FlatClientProperties.STYLE,
            "borderWidth:0; foreground:#0C5CB1; underline:true;" +
                "background:null; hoverBackground:null; pressedBackground:null;");
        backButton.addActionListener(e -> Navigation.to(this, Screen.PORTAL_GATEWAY));

        JButton loginButton = new JButton("Login");
        loginButton.putClientProperty(FlatClientProperties.STYLE,
            "arc:16; background:#0C5CB1; foreground:#FFFFFF;" +
                "hoverBackground:#0f6ed8; pressedBackground:#0a4f8d;" +
                "focusWidth:2; innerFocusWidth:1;");
        loginButton.setPreferredSize(new Dimension(140, 44));
        loginButton.addActionListener(e -> Navigation.to(this, Screen.DASHBOARD));

        row.add(backButton);
        row.add(Box.createHorizontalGlue());
        row.add(loginButton);
        return row;
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
