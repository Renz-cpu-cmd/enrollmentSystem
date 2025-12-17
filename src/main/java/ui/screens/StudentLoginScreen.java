package ui.screens;

/**
 * Login screen for students.
 *
 * <p>Demonstrates inheritance by extending `JPanel` and polymorphism by
 * implementing `ScreenView`, allowing the navigation system to treat all
 * screens uniformly.</p>
 */

import com.formdev.flatlaf.FlatClientProperties;
import dao.StudentDAO;
import model.Student;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.theme.Theme;
import util.Navigation;
import util.SessionManager;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class StudentLoginScreen extends JPanel implements ScreenView {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentLoginScreen.class);

    private JTextField studentIdField;
    private JPasswordField passwordField;

    public StudentLoginScreen() {
        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(buildSplit(), gbc);
    }

    private JComponent buildSplit() {
        JPanel split = new JPanel(new GridBagLayout());
        split.setOpaque(true);
        split.setBackground(new Color(0x0F172A));

        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.weightx = 0.4;
        leftGbc.weighty = 1;
        leftGbc.fill = GridBagConstraints.BOTH;

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 1;
        rightGbc.gridy = 0;
        rightGbc.weightx = 0.6;
        rightGbc.weighty = 1;
        rightGbc.fill = GridBagConstraints.BOTH;

        split.add(buildBrandPanel(), leftGbc);
        split.add(buildFormPanel(), rightGbc);
        return split;
    }

    private JComponent buildBrandPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint paint = new GradientPaint(0, 0, new Color(0x1E3A8A), 0, getHeight(), new Color(0x172554));
                g2d.setPaint(paint);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        panel.setOpaque(true);
        panel.setBackground(new Color(0x0F172A));

        // Center stack
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("🏛");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Enrolly Student Portal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Manage your academic journey with ease.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(0xCBD5E1));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(logo);
        center.add(Box.createVerticalStrut(12));
        center.add(title);
        center.add(Box.createVerticalStrut(6));
        center.add(subtitle);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(center);

        JLabel footer = new JLabel("© 2025 University Systems");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setForeground(new Color(0x94A3B8));
        footer.setBorder(new EmptyBorder(0, 0, 16, 16));

        panel.add(centerWrapper, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private JComponent buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(32, 48, 32, 48));

        JLabel header = new JLabel("Welcome Back");
        header.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setForeground(new Color(0x0F172A));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Please enter your details to sign in.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(0x64748B));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(header);
        form.add(Box.createVerticalStrut(6));
        form.add(sub);
        form.add(Box.createVerticalStrut(24));

        studentIdField = new JTextField();
        passwordField = new JPasswordField();
        form.add(createInputWithIcon("Student ID", "👤", studentIdField));
        form.add(Box.createVerticalStrut(12));
        form.add(createInputWithIcon("Password", "🔒", passwordField));

        form.add(Box.createVerticalStrut(10));
        JLabel forgot = new JLabel("Forgot Password?");
        forgot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgot.setForeground(new Color(0x0C5CB1));
        forgot.setAlignmentX(Component.RIGHT_ALIGNMENT);
        form.add(forgot);

        form.add(Box.createVerticalStrut(14));
        JButton loginButton = new JButton("Sign In");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(0x0C5CB1));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setFocusPainted(false);
        loginButton.putClientProperty(FlatClientProperties.STYLE, "arc:12; borderWidth:0; focusWidth:0; innerFocusWidth:0; padding:10,16,10,16;");
        loginButton.setPreferredSize(new Dimension(320, 45));
        loginButton.addActionListener(e -> handleLogin());
        form.add(loginButton);

        panel.add(form, gbc);
        return panel;
    }

    private JComponent createInputWithIcon(String labelText, String icon, JComponent field) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(0x0F172A));
        wrapper.add(label);
        wrapper.add(Box.createVerticalStrut(6));

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(320, 45));

        JLabel iconLabel = new JLabel(icon + "  ");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        iconLabel.setForeground(new Color(0x64748B));
        iconLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.putClientProperty(FlatClientProperties.STYLE,
            "arc:10; borderWidth:1; borderColor:#CBD5E1; focusWidth:2; innerFocusWidth:1; padding:10,10,10,10;");
        if (field instanceof JTextField tf) {
            tf.putClientProperty("JTextField.placeholderText", labelText.equals("Student ID") ? "2023-12345" : "");
        }
        if (field instanceof JPasswordField pf) {
            pf.putClientProperty("JTextField.placeholderText", "••••••••");
        }

        row.add(iconLabel, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        wrapper.add(row);
        return wrapper;
    }

    private void handleLogin() {
        String username = studentIdField.getText() != null ? studentIdField.getText().trim() : "";
        String password = new String(passwordField.getPassword());

        if (username.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please enter credentials", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StudentDAO dao = new StudentDAO();
        Student student = dao.getStudentByStudentId(username);
        boolean authenticated = false;
        boolean seededMatch = false;
        if (student != null && student.getPassword() != null) {
            try {
                authenticated = BCrypt.checkpw(password, student.getPassword());
                seededMatch = BCrypt.checkpw("password123", student.getPassword());
            } catch (IllegalArgumentException ex) {
                authenticated = false;
            }
        }
        LOGGER.info("Login attempt studentId={} found={} auth={} pwdHashPrefix={} wd={} inputLen={} seededCheck={}",
            username,
            student != null,
            authenticated,
            student != null && student.getPassword() != null ? student.getPassword().substring(0, Math.min(8, student.getPassword().length())) : "none",
            System.getProperty("user.dir"),
            password.length(),
            seededMatch);
        if (authenticated) {
            SessionManager.getInstance().setCurrentStudent(student);
            SessionManager.getInstance().touch();
            Navigation.to(StudentLoginScreen.this, Screen.DASHBOARD);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid ID or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onEnter(NavigationContext context) {
        if (studentIdField != null) studentIdField.setText("");
        if (passwordField != null) passwordField.setText("");
    }

    @Override
    public void onLeave() {
        // No cleanup necessary yet.
    }
}
