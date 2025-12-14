package ui.screens;

import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.components.SubjectCartPanel;
import ui.theme.Theme;
import util.Navigation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class IrregularPathScreen extends JPanel implements ScreenView {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
    private static final List<String> FAILED_SUBJECTS = List.of("IT 101");

    private record Subject(
        String code,
        String name,
        String schedule,
        String prerequisite,
        boolean backSubject,
        int units,
        String room
    ) {
    }

    private record ScheduleSlot(DayOfWeek day, LocalTime start, LocalTime end, String raw) {
    }

    private final List<Subject> backSubjects = List.of(
        new Subject("IT 101", "Programming 1", "Mon 7:00-9:00", null, true, 3, "CL-1"),
        new Subject("MATH 121", "Discrete Structures", "Tue 1:00-4:00", null, true, 3, "NB-202")
    );

    private final List<Subject> onSemSubjects = List.of(
        new Subject("IT 215", "Networking 2", "Wed 8:00-11:00", "IT 101", false, 3, "CL-LAB3"),
        new Subject("IT 218", "Mobile Development", "Thu 9:00-12:00", "IT 212", false, 3, "NB-305"),
        new Subject("GEC 6", "Science, Tech & Society", "Fri 1:00-4:00", null, false, 3, "AVR-A")
    );

    private final Map<String, Subject> selectedSubjects = new LinkedHashMap<>();
    private final SubjectCartPanel cartPanel;

    public IrregularPathScreen() {
        setLayout(new BorderLayout(16, 16));
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        cartPanel = new SubjectCartPanel();

        add(buildSplitPane(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JSplitPane buildSplitPane() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(wrapPanel(cartPanel));
        splitPane.setBottomComponent(buildSubjectMenu());
        splitPane.setResizeWeight(0.4);
        splitPane.setBorder(null);
        return splitPane;
    }

    private JPanel wrapPanel(JComponent component) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(component, BorderLayout.CENTER);
        return wrapper;
    }

    private JTabbedPane buildSubjectMenu() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Back Subjects (Priority)", createSubjectListPanel(backSubjects, true));
        tabs.addTab("On-Sem Subjects", createSubjectListPanel(onSemSubjects, false));
        return tabs;
    }

    private JComponent createSubjectListPanel(List<Subject> subjects, boolean highlightFailed) {
        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        for (Subject subject : subjects) {
            container.add(createSubjectCard(subject, highlightFailed));
            container.add(Box.createVerticalStrut(12));
        }

        JScrollPane pane = new JScrollPane(container);
        pane.setBorder(BorderFactory.createEmptyBorder());
        return pane;
    }

    private JPanel createSubjectCard(Subject subject, boolean highlightFailed) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Theme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
            new EmptyBorder(16, 18, 16, 18)));

        JPanel textGroup = new JPanel();
        textGroup.setOpaque(false);
        textGroup.setLayout(new BoxLayout(textGroup, BoxLayout.Y_AXIS));

        boolean emphasize = highlightFailed && FAILED_SUBJECTS.stream()
            .anyMatch(code -> code.equalsIgnoreCase(subject.code));

        JLabel title = new JLabel(subject.code + " - " + subject.name);
        title.setFont(emphasize
            ? Theme.SUBHEADER_FONT.deriveFont(Font.BOLD)
            : Theme.SUBHEADER_FONT);
        title.setForeground(emphasize ? Theme.DANGER : Theme.TEXT_HEADER);

        JPanel titleRow = new JPanel();
        titleRow.setOpaque(false);
        titleRow.setLayout(new BoxLayout(titleRow, BoxLayout.X_AXIS));
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleRow.add(title);

        if (emphasize) {
            titleRow.add(Box.createHorizontalStrut(8));
            JLabel requiredTag = new JLabel("REQUIRED");
            requiredTag.setFont(Theme.LABEL_FONT.deriveFont(Font.BOLD, 11f));
            requiredTag.setForeground(Color.WHITE);
            requiredTag.setOpaque(true);
            requiredTag.setBackground(Theme.DANGER);
            requiredTag.setBorder(new EmptyBorder(2, 8, 2, 8));
            titleRow.add(requiredTag);
        }

        textGroup.add(titleRow);

        JLabel meta = new JLabel(subject.schedule + " • " + subject.room + " • " + subject.units + " Units");
        meta.setFont(Theme.BODY_FONT);
        meta.setForeground(Theme.TEXT_BODY);
        textGroup.add(meta);

        JButton addButton = new JButton("Add");
        addButton.setFont(Theme.BODY_FONT);
        addButton.setBackground(Theme.PRIMARY);
        addButton.setForeground(Color.WHITE);
        addButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> handleAddSubject(subject));

        card.add(textGroup, BorderLayout.CENTER);
        card.add(addButton, BorderLayout.EAST);
        return card;
    }

    private void handleAddSubject(Subject subject) {
        syncSelectedSubjects();
        if (hasTimeConflict(subject)) {
            return;
        }
        if (hasPrereqConflict(subject, FAILED_SUBJECTS)) {
            return;
        }

        SubjectCartPanel.Subject viewModel = new SubjectCartPanel.Subject(
            subject.code,
            subject.name,
            subject.units,
            subject.schedule,
            subject.room
        );

        SubjectCartPanel.AddResult result = cartPanel.addSubject(viewModel);
        if (result == SubjectCartPanel.AddResult.SUCCESS) {
            selectedSubjects.put(subject.code, subject);
            JOptionPane.showMessageDialog(this,
                subject.code + " added to your schedule.",
                "Subject Added",
                JOptionPane.INFORMATION_MESSAGE);
        } else if (result == SubjectCartPanel.AddResult.DUPLICATE) {
            JOptionPane.showMessageDialog(this,
                "You already added " + subject.code + ".",
                "Duplicate Subject",
                JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Adding this subject exceeds the 26-unit cap.",
                "Unit Limit Reached",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void syncSelectedSubjects() {
        Set<String> currentCodes = new HashSet<>();
        for (SubjectCartPanel.Subject subject : cartPanel.getSubjects()) {
            currentCodes.add(subject.getCode());
        }
        selectedSubjects.keySet().removeIf(code -> !currentCodes.contains(code));
    }

    private boolean hasTimeConflict(Subject newSubject) {
        for (Subject existing : selectedSubjects.values()) {
            String conflictSlot = findConflictSlot(existing.schedule, newSubject.schedule);
            if (conflictSlot != null) {
                JOptionPane.showMessageDialog(this,
                    "Error: This conflicts with " + existing.name + " at " + conflictSlot,
                    "Schedule Conflict",
                    JOptionPane.ERROR_MESSAGE);
                return true;
            }
        }
        return false;
    }

    private String findConflictSlot(String existingSchedule, String newSchedule) {
        List<ScheduleSlot> existingSlots = parseSlots(existingSchedule);
        List<ScheduleSlot> newSlots = parseSlots(newSchedule);
        for (ScheduleSlot existing : existingSlots) {
            for (ScheduleSlot candidate : newSlots) {
                if (existing.day != null && candidate.day != null) {
                    if (existing.day == candidate.day && timesOverlap(existing, candidate)) {
                        return existing.raw;
                    }
                } else if (existing.raw.equalsIgnoreCase(candidate.raw)) {
                    return existing.raw;
                }
            }
        }
        return null;
    }

    private List<ScheduleSlot> parseSlots(String schedule) {
        List<ScheduleSlot> slots = new ArrayList<>();
        for (String chunk : schedule.split(",")) {
            slots.add(parseSlot(chunk.trim()));
        }
        return slots;
    }

    private ScheduleSlot parseSlot(String chunk) {
        try {
            String[] parts = chunk.split("\\s+", 2);
            DayOfWeek day = parseDay(parts[0]);
            if (parts.length < 2) {
                return new ScheduleSlot(day, null, null, chunk);
            }
            String[] times = parts[1].split("-");
            LocalTime start = LocalTime.parse(times[0].trim(), TIME_FORMATTER);
            LocalTime end = LocalTime.parse(times[1].trim(), TIME_FORMATTER);
            return new ScheduleSlot(day, start, end, chunk);
        } catch (Exception ex) {
            return new ScheduleSlot(null, null, null, chunk);
        }
    }

    private DayOfWeek parseDay(String token) {
        String normalized = token.trim().toLowerCase(Locale.ENGLISH);
        return switch (normalized) {
            case "m", "mon" -> DayOfWeek.MONDAY;
            case "t", "tue", "tues" -> DayOfWeek.TUESDAY;
            case "w", "wed" -> DayOfWeek.WEDNESDAY;
            case "th", "thu", "thur", "thurs" -> DayOfWeek.THURSDAY;
            case "f", "fri" -> DayOfWeek.FRIDAY;
            case "sat", "s" -> DayOfWeek.SATURDAY;
            case "sun" -> DayOfWeek.SUNDAY;
            default -> null;
        };
    }

    private boolean timesOverlap(ScheduleSlot existing, ScheduleSlot candidate) {
        if (existing.start == null || existing.end == null || candidate.start == null || candidate.end == null) {
            return existing.raw.equalsIgnoreCase(candidate.raw);
        }
        return existing.start.isBefore(candidate.end) && candidate.start.isBefore(existing.end);
    }

    private boolean hasPrereqConflict(Subject subject, List<String> failedSubjects) {
        if (subject.prerequisite == null || subject.prerequisite.isBlank()) {
            return false;
        }
        boolean failed = failedSubjects.stream()
            .anyMatch(code -> code.equalsIgnoreCase(subject.prerequisite));
        if (failed) {
            JOptionPane.showMessageDialog(this,
                "Error: You must pass " + subject.prerequisite + " before taking " + subject.name,
                "Prerequisite Not Met",
                JOptionPane.ERROR_MESSAGE);
        }
        return failed;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JButton validateButton = new JButton("Validate & Proceed");
        validateButton.setFont(Theme.SUBHEADER_FONT);
        validateButton.setBackground(Theme.PRIMARY);
        validateButton.setForeground(Color.WHITE);
        validateButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        validateButton.setFocusPainted(false);
        validateButton.addActionListener(e -> {
            if (cartPanel.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Add at least one subject before validation.",
                    "Schedule Incomplete",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            Navigation.to(this, Screen.ASSESSMENT);
        });

        footer.add(validateButton, BorderLayout.EAST);
        return footer;
    }

    @Override
    public void onEnter(NavigationContext context) {
        // Future: load previously saved irregular selections.
    }

    @Override
    public void onLeave() {
        // No teardown required yet.
    }
}