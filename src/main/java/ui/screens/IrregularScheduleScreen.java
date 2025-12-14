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

public class IrregularScheduleScreen extends JPanel implements ScreenView {

    private record Subject(String code, String title, double units, String schedule) {}

    private final JTextField searchField;
    private final JPanel availableListPanel;
    private final JPanel selectedListPanel;
    private final JLabel totalUnitsLabel;
    private final List<Subject> catalog;
    private final List<Subject> selectedSubjects = new ArrayList<>();
    private String searchQuery = "";

    public IrregularScheduleScreen() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(Theme.PADDING_BORDER);

        catalog = buildCatalog();

        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search Subject Code...");
        searchField.setFont(Theme.BODY_FONT);
        searchField.setBorder(Theme.LINE_BORDER);
        searchField.addActionListener(e -> performSearch());

        availableListPanel = createListPanel();
        selectedListPanel = createListPanel();

        totalUnitsLabel = new JLabel();
        totalUnitsLabel.setFont(Theme.SUBHEADING_FONT);
        totalUnitsLabel.setForeground(Theme.TEXT_PRIMARY_COLOR);

        add(createHeader(), BorderLayout.NORTH);
        add(createTabs(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        renderAvailableSubjects();
        renderSelectedSubjects();
        updateTotalUnits();
    }

    private JComponent createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("Custom Schedule (Irregular)");
        title.setFont(Theme.HEADING_FONT.deriveFont(Font.BOLD, 24f));
        title.setForeground(Theme.TEXT_PRIMARY_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(12));
        header.add(createSearchPanel());
        return header;
    }

    private JComponent createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);

        RoundedButton findButton = RoundedButton.primary("Find");
        findButton.addActionListener(e -> performSearch());

        panel.add(searchField, BorderLayout.CENTER);
        panel.add(findButton, BorderLayout.EAST);
        return panel;
    }

    private JComponent createTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.BODY_FONT);

        tabs.addTab("Available Subjects", createScrollPane(availableListPanel));
        tabs.addTab("My Schedule", createScrollPane(selectedListPanel));
        return tabs;
    }

    private JScrollPane createScrollPane(JPanel content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(16, 0, 16, 0));
        return panel;
    }

    private JComponent createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 0, 0, 0));

        RoundedButton proceedButton = RoundedButton.primary("Proceed to Assessment");
        proceedButton.setMargin(new Insets(16, 32, 16, 32));
        proceedButton.addActionListener(e -> {
            AssessmentScreen.returnScreen = Screen.IRREGULAR_SCHEDULE;
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(IrregularScheduleScreen.this);
            if (frame != null) {
                frame.showScreen(Screen.ASSESSMENT, true);
            }
        });

        footer.add(totalUnitsLabel, BorderLayout.WEST);
        footer.add(proceedButton, BorderLayout.EAST);
        return footer;
    }

    private void performSearch() {
        searchQuery = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        renderAvailableSubjects();
    }

    private void renderAvailableSubjects() {
        availableListPanel.removeAll();
        catalog.stream()
            .filter(subject -> !selectedSubjects.contains(subject))
            .filter(subject -> searchQuery.isBlank() || subject.code().toLowerCase().contains(searchQuery))
            .forEach(subject -> availableListPanel.add(createSubjectRow(subject, true)));
        availableListPanel.revalidate();
        availableListPanel.repaint();
    }

    private void renderSelectedSubjects() {
        selectedListPanel.removeAll();
        if (selectedSubjects.isEmpty()) {
            JLabel emptyLabel = new JLabel("No subjects added yet.");
            emptyLabel.setFont(Theme.BODY_FONT);
            emptyLabel.setForeground(Theme.TEXT_SECONDARY_COLOR);
            selectedListPanel.add(emptyLabel);
        } else {
            selectedSubjects.forEach(subject -> selectedListPanel.add(createSubjectRow(subject, false)));
        }
        selectedListPanel.revalidate();
        selectedListPanel.repaint();
    }

    private JComponent createSubjectRow(Subject subject, boolean isAddRow) {
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
                g2.setColor(new Color(0, 0, 0, 15));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
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

        RoundedButton actionButton = isAddRow
            ? new RoundedButton("Add", Theme.SUCCESS_COLOR, Color.WHITE)
            : new RoundedButton("Remove", Theme.DANGER_COLOR, Color.WHITE);
        actionButton.setMargin(new Insets(8, 18, 8, 18));
        actionButton.addActionListener(e -> {
            if (isAddRow) {
                addSubject(subject);
            } else {
                removeSubject(subject);
            }
        });

        row.add(textColumn, BorderLayout.CENTER);
        row.add(actionButton, BorderLayout.EAST);
        return row;
    }

    private void addSubject(Subject subject) {
        if (!selectedSubjects.contains(subject)) {
            selectedSubjects.add(subject);
            renderAvailableSubjects();
            renderSelectedSubjects();
            updateTotalUnits();
        }
    }

    private void removeSubject(Subject subject) {
        if (selectedSubjects.remove(subject)) {
            renderAvailableSubjects();
            renderSelectedSubjects();
            updateTotalUnits();
        }
    }

    private void updateTotalUnits() {
        double total = selectedSubjects.stream().mapToDouble(Subject::units).sum();
        totalUnitsLabel.setText(String.format("Total Units: %.1f / 26.0", total));
    }

    private List<Subject> buildCatalog() {
        Map<String, Subject> map = new LinkedHashMap<>();
        map.put("IT 211", new Subject("IT 211", "Data Structures & Algorithms", 3.0, "MWF • 08:00 - 09:00"));
        map.put("IT 212", new Subject("IT 212", "Advanced OOP", 3.0, "MWF • 09:00 - 10:00"));
        map.put("IT 213", new Subject("IT 213", "Computer Networks 1", 3.0, "TTh • 10:30 - 12:00"));
        map.put("IT 214", new Subject("IT 214", "Database Systems 2", 3.0, "TTh • 08:00 - 09:30"));
        map.put("GE 04", new Subject("GE 04", "Ethics", 3.0, "TTh • 13:00 - 14:30"));
        map.put("GE 05", new Subject("GE 05", "Life & Works of Rizal", 3.0, "Wed • 13:00 - 16:00"));
        map.put("IT ELEC", new Subject("IT ELEC", "Mobile Development", 3.0, "Fri • 10:00 - 13:00"));
        map.put("PE 04", new Subject("PE 04", "Team Sports", 2.0, "Sat • 07:00 - 09:00"));
        map.put("NSTP 2", new Subject("NSTP 2", "CWTS 2", 3.0, "Sat • 09:00 - 12:00"));
        return new ArrayList<>(map.values());
    }

    private void navigate(Screen target) {
        MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.showScreen(target, true);
        }
    }

    @Override
    public void onEnter(NavigationContext context) {
        // Placeholder for future data fetching.
    }

    @Override
    public void onLeave() {
        // No cleanup necessary yet.
    }
}
