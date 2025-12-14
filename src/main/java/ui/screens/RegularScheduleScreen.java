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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegularScheduleScreen extends JPanel implements ScreenView {

    private record Subject(String code, String title, double units, String schedule) {}

    private final JComboBox<String> blockSelector;
    private final JPanel subjectsContainer;
    private final JLabel totalUnitsLabel;
    private final Map<String, List<Subject>> blockSubjects;
    private final List<JCheckBox> subjectCheckboxes = new ArrayList<>();

    public RegularScheduleScreen() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);

        blockSubjects = createBlockSubjects();

        blockSelector = new JComboBox<>(blockSubjects.keySet().toArray(new String[0]));
        blockSelector.addActionListener(e -> refreshSubjects());

        subjectsContainer = new JPanel();
        subjectsContainer.setOpaque(false);
        subjectsContainer.setLayout(new BoxLayout(subjectsContainer, BoxLayout.Y_AXIS));

        totalUnitsLabel = new JLabel();
        totalUnitsLabel.setFont(Theme.SUBHEADING_FONT);
        totalUnitsLabel.setForeground(Theme.TEXT_PRIMARY_COLOR);

        add(createNorthPanel(), BorderLayout.NORTH);
        add(createScrollPane(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        refreshSubjects();
    }

    private JComponent createNorthPanel() {
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.setOpaque(false);
        north.setBorder(new EmptyBorder(24, 24, 12, 24));

        JLabel title = new JLabel("Select Your Schedule");
        title.setFont(Theme.HEADING_FONT.deriveFont(Font.BOLD, 26f));
        title.setForeground(Theme.TEXT_PRIMARY_COLOR);

        JLabel subtitle = new JLabel("2nd Year - 2nd Semester");
        subtitle.setFont(Theme.BODY_FONT);
        subtitle.setForeground(Theme.TEXT_SECONDARY_COLOR);

        north.add(title);
        north.add(Box.createVerticalStrut(4));
        north.add(subtitle);
        north.add(Box.createVerticalStrut(16));
        north.add(createFilterPanel());
        return north;
    }

    private JComponent createFilterPanel() {
        JPanel filter = new JPanel(new GridBagLayout());
        filter.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 8, 0, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel label = new JLabel("Preferred Block/Section:");
        label.setFont(Theme.BOLD_BODY_FONT);
        label.setForeground(Theme.TEXT_PRIMARY_COLOR);
        filter.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        blockSelector.setMaximumRowCount(3);
        blockSelector.setBorder(Theme.LINE_BORDER);
        blockSelector.setPreferredSize(new Dimension(200, 36));
        filter.add(blockSelector, gbc);
        return filter;
    }

    private JComponent createScrollPane() {
        JPanel viewportHost = new JPanel(new BorderLayout());
        viewportHost.setOpaque(false);
        viewportHost.add(subjectsContainer, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(viewportHost);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JComponent createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 24, 24, 24));

        RoundedButton proceedButton = RoundedButton.primary("Proceed to Assessment");
        proceedButton.setMargin(new Insets(16, 32, 16, 32));
        proceedButton.addActionListener(e -> {
            AssessmentScreen.returnScreen = Screen.REGULAR_SCHEDULE;
            navigate(Screen.ASSESSMENT);
        });

        footer.add(totalUnitsLabel, BorderLayout.WEST);
        footer.add(proceedButton, BorderLayout.EAST);
        return footer;
    }

    private void refreshSubjects() {
        subjectCheckboxes.clear();
        subjectsContainer.removeAll();

        String block = (String) blockSelector.getSelectedItem();
        List<Subject> subjects = blockSubjects.getOrDefault(block, List.of());

        for (Subject subject : subjects) {
            subjectsContainer.add(createSubjectRow(subject));
            subjectsContainer.add(Box.createVerticalStrut(12));
        }

        subjectsContainer.revalidate();
        subjectsContainer.repaint();
        updateTotalUnits();
    }

    private JComponent createSubjectRow(Subject subject) {
        JPanel row = new JPanel(new BorderLayout(12, 0)) {
            {
                setOpaque(false);
                setBorder(new EmptyBorder(16, 20, 16, 20));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.SURFACE_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(0, 0, 0, 20));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };

        JCheckBox selectBox = new JCheckBox();
        selectBox.setOpaque(false);
        selectBox.setSelected(true);
        selectBox.putClientProperty("units", subject.units());
        selectBox.addActionListener(e -> updateTotalUnits());
        subjectCheckboxes.add(selectBox);

        JPanel textColumn = new JPanel();
        textColumn.setOpaque(false);
        textColumn.setLayout(new BoxLayout(textColumn, BoxLayout.Y_AXIS));

        JLabel code = new JLabel(subject.code() + " • " + subject.units() + " units");
        code.setFont(Theme.BOLD_BODY_FONT);
        code.setForeground(Theme.TEXT_PRIMARY_COLOR);

        JLabel desc = new JLabel(subject.title());
        desc.setFont(Theme.BODY_FONT);
        desc.setForeground(Theme.TEXT_SECONDARY_COLOR);

        JLabel sched = new JLabel(subject.schedule());
        sched.setFont(Theme.LABEL_FONT);
        sched.setForeground(Theme.TEXT_SECONDARY_COLOR);

        textColumn.add(code);
        textColumn.add(Box.createVerticalStrut(4));
        textColumn.add(desc);
        textColumn.add(Box.createVerticalStrut(4));
        textColumn.add(sched);

        row.add(selectBox, BorderLayout.WEST);
        row.add(textColumn, BorderLayout.CENTER);
        return row;
    }

    private void updateTotalUnits() {
        double total = 0;
        for (JCheckBox checkBox : subjectCheckboxes) {
            if (checkBox.isSelected()) {
                Object value = checkBox.getClientProperty("units");
                if (value instanceof Double units) {
                    total += units;
                }
            }
        }
        totalUnitsLabel.setText(String.format("Total Units: %.1f", total));
    }

    private Map<String, List<Subject>> createBlockSubjects() {
        Map<String, List<Subject>> map = new LinkedHashMap<>();
        map.put("BSIT 2-A", List.of(
            new Subject("IT 211", "Data Structures & Algorithms", 3.0, "Mon/Wed/Fri • 08:00 - 09:00"),
            new Subject("IT 212", "Advanced Object-Oriented Programming", 3.0, "Mon/Wed/Fri • 09:00 - 10:00"),
            new Subject("IT 213", "Computer Networks 1", 3.0, "Tue/Thu • 10:30 - 12:00"),
            new Subject("GE 04", "Ethics", 3.0, "Tue/Thu • 13:00 - 14:30"),
            new Subject("IT ELEC", "UI/UX Principles", 3.0, "Fri • 13:00 - 16:00"),
            new Subject("PE 04", "Team Sports", 2.0, "Sat • 07:00 - 09:00")
        ));
        map.put("BSIT 2-B", List.of(
            new Subject("IT 211", "Data Structures & Algorithms", 3.0, "Mon/Wed • 10:00 - 11:30"),
            new Subject("IT 214", "Database Management 2", 3.0, "Tue/Thu • 08:00 - 09:30"),
            new Subject("IT 215", "Software Engineering", 3.0, "Tue/Thu • 09:30 - 11:00"),
            new Subject("GE 05", "Life & Works of Rizal", 3.0, "Wed • 13:00 - 16:00"),
            new Subject("IT ELEC", "Mobile Application Dev", 3.0, "Fri • 10:00 - 13:00"),
            new Subject("NSTP 2", "CWTS 2", 3.0, "Sat • 09:00 - 12:00")
        ));
        map.put("BSIT 2-C", List.of(
            new Subject("IT 212", "Advanced Object-Oriented Programming", 3.0, "Mon/Wed • 13:00 - 14:30"),
            new Subject("IT 213", "Computer Networks 1", 3.0, "Mon/Wed • 14:30 - 16:00"),
            new Subject("IT 214", "Database Management 2", 3.0, "Tue/Thu • 07:30 - 09:00"),
            new Subject("GE 04", "Ethics", 3.0, "Tue/Thu • 09:00 - 10:30"),
            new Subject("IT ELEC", "Data Visualization", 3.0, "Fri • 08:00 - 11:00"),
            new Subject("PE 04", "Team Sports", 2.0, "Sat • 10:00 - 12:00")
        ));
        return map;
    }

    private void navigate(Screen target) {
        MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.showScreen(target, true);
        }
    }

    @Override
    public void onEnter(NavigationContext context) {
        // No dynamic data yet.
    }

    @Override
    public void onLeave() {
        // No-op.
    }
}
