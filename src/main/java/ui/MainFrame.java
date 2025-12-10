package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    public MainFrame() {
        super("Enrollment System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar for navigation
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebar.setBackground(new Color(240, 240, 240));

        addNavButton("Dashboard", "dashboard", sidebar);
        addNavButton("Students", "students", sidebar);
        addNavButton("Courses", "courses", sidebar);
        addNavButton("Enrollment", "enrollment", sidebar);
        addNavButton("AI Assistant", "ai_assistant", sidebar);

        // Main content panel
        contentPanel.add(new DashboardPanel(), "dashboard");
        contentPanel.add(new StudentPanel(), "students");
        contentPanel.add(new CoursePanel(), "courses");
        contentPanel.add(new EnrollmentPanel(), "enrollment");
        contentPanel.add(new AiAssistantPanel(), "ai_assistant");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, contentPanel);
        splitPane.setDividerLocation(150);

        add(splitPane, BorderLayout.CENTER);

        // Menu bar
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        file.add(exit);
        mb.add(file);
        setJMenuBar(mb);
    }

    private void addNavButton(String text, String cardName, JPanel panel) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getMinimumSize().height));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
    }
}
