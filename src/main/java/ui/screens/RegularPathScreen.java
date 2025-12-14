package ui.screens;

import ui.MobileFrame;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.theme.RoundedButton;
import ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class RegularPathScreen extends JPanel implements ScreenView {

    private record Subject(String code, String title, double units, String schedule) {}

    private static final List<Subject> SUBJECTS = List.of(
        new Subject("IT 211", "Systems Integration & Architecture", 3.0, "Mon/Wed • 08:00 - 09:30"),
        new Subject("IT 212", "Information Assurance & Security", 3.0, "Mon/Wed • 09:30 - 11:00"),
        new Subject("IT 213", "Capstone Project 1", 3.0, "Tue/Thu • 08:00 - 09:30"),
        new Subject("IT 214", "Advanced Database Systems", 3.0, "Tue/Thu • 10:00 - 11:30"),
        new Subject("GE 04", "Ethics", 3.0, "Wed • 13:00 - 15:00"),
        new Subject("GE 05", "Life & Works of Rizal", 3.0, "Fri • 08:00 - 11:00"),
        new Subject("PE 04", "Team Sports", 2.0, "Sat • 07:00 - 09:00"),
        new Subject("NSTP 2", "CWTS 2", 3.0, "Sat • 09:00 - 12:00")
    );

    public RegularPathScreen() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);

        add(createHeader(), BorderLayout.NORTH);
        add(createScheduleScroll(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JComponent createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(24, 24, 8, 24));

        JLabel title = new JLabel("Your Advised Schedule");
        title.setFont(Theme.HEADING_FONT.deriveFont(Font.BOLD, 26f));
        title.setForeground(Theme.TEXT_PRIMARY_COLOR);

        JLabel subtitle = new JLabel("2nd Year - 2nd Semester");
        subtitle.setFont(Theme.BODY_FONT);
        subtitle.setForeground(Theme.TEXT_SECONDARY_COLOR);

        JLabel note = new JLabel("These subjects are pre-loaded for Regular students. Please review them below.");
        note.setFont(Theme.LABEL_FONT);
        note.setForeground(Theme.TEXT_SECONDARY_COLOR);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        header.add(Box.createVerticalStrut(12));
        header.add(note);
        return header;
    }

    private JComponent createScheduleScroll() {
        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setBorder(new EmptyBorder(0, 24, 24, 24));

        for (Subject subject : SUBJECTS) {
            column.add(createSubjectCard(subject));
            column.add(Box.createVerticalStrut(12));
        }

        JLabel totalUnits = new JLabel(String.format("Total Units: %.1f", getTotalUnits()));
        totalUnits.setFont(Theme.SUBHEADING_FONT);
        totalUnits.setForeground(Theme.TEXT_PRIMARY_COLOR);
        totalUnits.setAlignmentX(Component.LEFT_ALIGNMENT);
        column.add(Box.createVerticalStrut(4));
        column.add(totalUnits);

        JPanel viewportHost = new JPanel(new BorderLayout());
        viewportHost.setOpaque(false);
        viewportHost.add(column, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(viewportHost);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private double getTotalUnits() {
        return SUBJECTS.stream().mapToDouble(Subject::units).sum();
    }

    private JComponent createSubjectCard(Subject subject) {
        JPanel card = new JPanel() {
            {
                setOpaque(false);
                setBorder(new EmptyBorder(18, 20, 18, 20));
                setLayout(new BorderLayout());
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.SURFACE_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };

        JPanel textColumn = new JPanel();
        textColumn.setOpaque(false);
        textColumn.setLayout(new BoxLayout(textColumn, BoxLayout.Y_AXIS));

        JLabel code = new JLabel(subject.code() + " • " + subject.units() + " units");
        code.setFont(Theme.BOLD_BODY_FONT);
        code.setForeground(Theme.TEXT_PRIMARY_COLOR);

        JLabel title = new JLabel(subject.title());
        title.setFont(Theme.BODY_FONT);
        title.setForeground(Theme.TEXT_SECONDARY_COLOR);

        JLabel schedule = new JLabel(subject.schedule());
        schedule.setFont(Theme.LABEL_FONT);
        schedule.setForeground(Theme.TEXT_SECONDARY_COLOR);

        textColumn.add(code);
        textColumn.add(Box.createVerticalStrut(4));
        textColumn.add(title);
        textColumn.add(Box.createVerticalStrut(4));
        textColumn.add(schedule);

        JLabel lockLabel = new JLabel("Fixed");
        lockLabel.setFont(Theme.LABEL_FONT);
        lockLabel.setForeground(Theme.TEXT_SECONDARY_COLOR);
        lockLabel.setIcon(UIManager.getIcon("FileView.hardDriveIcon"));

        card.add(textColumn, BorderLayout.CENTER);
        card.add(lockLabel, BorderLayout.EAST);
        return card;
    }

    private JComponent createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(new EmptyBorder(16, 24, 24, 24));
        footer.setOpaque(false);

        RoundedButton backButton = RoundedButton.subtle("Back");
        backButton.addActionListener(e -> navigate(Screen.DASHBOARD));

        RoundedButton confirmButton = RoundedButton.primary("Confirm Schedule");
        confirmButton.setMargin(new Insets(16, 32, 16, 32));
        confirmButton.addActionListener(e -> navigate(Screen.ASSESSMENT));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(backButton);
        buttonRow.add(confirmButton);

        footer.add(buttonRow, BorderLayout.EAST);
        return footer;
    }

    private void navigate(Screen target) {
        MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.showScreen(target, true);
        }
    }

    @Override
    public void onEnter(NavigationContext context) {
        // Future: pull latest advised schedule if API is available.
    }

    @Override
    public void onLeave() {
        // Static view for now.
    }
}