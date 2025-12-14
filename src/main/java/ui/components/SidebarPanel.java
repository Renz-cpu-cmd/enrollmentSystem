package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SidebarPanel extends JPanel {

    private final JButton dashboardButton = createMenuButton("Dashboard");
    private final JButton enrollmentButton = createMenuButton("Enrollment");
    private final JButton scheduleButton = createMenuButton("Schedule");
    private final JButton profileButton = createMenuButton("Profile");
    private final JButton logoutButton = createMenuButton("Logout");

    public SidebarPanel() {
        setBackground(Theme.SIDEBAR_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(32, 24, 32, 24));
        setPreferredSize(new Dimension(220, 0));

        JLabel brand = new JLabel("Enrolly");
        brand.setFont(Theme.HEADER_FONT);
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(brand);

        add(Box.createVerticalStrut(30));
        add(dashboardButton);
        add(Box.createVerticalStrut(8));
        add(enrollmentButton);
        add(Box.createVerticalStrut(8));
        add(scheduleButton);
        add(Box.createVerticalStrut(8));
        add(profileButton);
        add(Box.createVerticalGlue());
        add(logoutButton);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        Theme.styleButton(button);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setForeground(Theme.SIDEBAR_TEXT);
        button.setFont(Theme.SUBHEADER_FONT);
        button.setBorder(new EmptyBorder(10, 12, 10, 12));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Theme.SIDEBAR_TEXT_ACTIVE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Theme.SIDEBAR_TEXT);
            }
        });
        return button;
    }

    public void setDashboardAction(Runnable action) {
        dashboardButton.addActionListener(e -> action.run());
    }

    public void setEnrollmentAction(Runnable action) {
        enrollmentButton.addActionListener(e -> action.run());
    }

    public void setScheduleAction(Runnable action) {
        scheduleButton.addActionListener(e -> action.run());
    }

    public void setProfileAction(Runnable action) {
        profileButton.addActionListener(e -> action.run());
    }

    public void setLogoutAction(Runnable action) {
        logoutButton.addActionListener(e -> action.run());
    }
}
