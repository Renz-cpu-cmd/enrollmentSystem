package ui;

import dao.CourseDAO;
import dao.StudentDAO;

import javax.swing.*;
import java.awt.*;

/**
 * A dashboard panel that displays key statistics of the enrollment system.
 */
public class DashboardPanel extends JPanel {

    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;

    /**
     * Constructs a new DashboardPanel.
     */
    public DashboardPanel() {
        this.studentDAO = new StudentDAO();
        this.courseDAO = new CourseDAO();
        setLayout(new GridLayout(1, 2, 20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        int studentCount = studentDAO.getAll().size();
        int courseCount = courseDAO.getAll().size();

        add(createStatPanel("Total Students", String.valueOf(studentCount)));
        add(createStatPanel("Total Courses", String.valueOf(courseCount)));
    }

    private JPanel createStatPanel(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }
}
