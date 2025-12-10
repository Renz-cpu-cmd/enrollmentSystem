package ui;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import dao.UserDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField userField = new JTextField(20);
    private JPasswordField passField = new JPasswordField(20);

    public LoginFrame() {
        super("Enrollment System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.gridx = 0;
        c.gridy = 0;
        p.add(new JLabel("Username:"), c);
        c.gridx = 1;
        p.add(userField, c);
        c.gridx = 0;
        c.gridy = 1;
        p.add(new JLabel("Password:"), c);
        c.gridx = 1;
        p.add(passField, c);
        JButton loginBtn = new JButton("Login");
        c.gridx = 1;
        c.gridy = 2;
        p.add(loginBtn, c);
        loginBtn.addActionListener(e -> doLogin());
        getContentPane().add(p);
        pack();
        setLocationRelativeTo(null);
    }

    private void doLogin() {
        String u = userField.getText().trim();
        String p = new String(passField.getPassword());
        try {
            if (UserDAO.authenticate(u, p)) {
                JOptionPane.showMessageDialog(this, "Login successful");
                SwingUtilities.invokeLater(() -> {
                    new MainFrame().setVisible(true);
                });
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            System.err.println("Failed to initialize LaF");
        }

        // Create default admin if not exists
        try {
            if (!UserDAO.userExists("admin")) {
                UserDAO.createUser("admin", "admin123", "ADMIN");
                System.out.println("Created default admin/admin123 - change password immediately.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
