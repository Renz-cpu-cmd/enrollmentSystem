package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays the current set of irregular subjects plus unit tracking.
 */
public class SubjectCartPanel extends JPanel {

    public enum AddResult {
        SUCCESS,
        DUPLICATE,
        UNIT_LIMIT
    }

    public static final class Subject {
        private final String code;
        private final String description;
        private final int units;
        private final String time;
        private final String room;

        public Subject(String code, String description, int units, String time, String room) {
            this.code = code;
            this.description = description;
            this.units = units;
            this.time = time;
            this.room = room;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public int getUnits() {
            return units;
        }

        public String getTime() {
            return time;
        }

        public String getRoom() {
            return room;
        }
    }

    private final JLabel headerLabel;
    private final JPanel listContainer;
    private final Map<String, Subject> cart = new LinkedHashMap<>();
    private final Map<String, JPanel> renderedRows = new LinkedHashMap<>();
    private final int maxUnits;
    private int totalUnits;

    public SubjectCartPanel() {
        this(26);
    }

    public SubjectCartPanel(int maxUnits) {
        this.maxUnits = maxUnits;
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);

        headerLabel = new JLabel();
        headerLabel.setFont(Theme.SUBHEADER_FONT);
        headerLabel.setForeground(Theme.TEXT_HEADER);
        add(headerLabel, BorderLayout.NORTH);

        listContainer = new JPanel();
        listContainer.setOpaque(false);
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBorder(new EmptyBorder(0, 0, 12, 0));

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollPane, BorderLayout.CENTER);

        updateHeader();
    }

    public AddResult addSubject(Subject subject) {
        if (cart.containsKey(subject.getCode())) {
            return AddResult.DUPLICATE;
        }
        if (totalUnits + subject.getUnits() > maxUnits) {
            return AddResult.UNIT_LIMIT;
        }

        cart.put(subject.getCode(), subject);
        totalUnits += subject.getUnits();
        addRow(subject);
        updateHeader();
        return AddResult.SUCCESS;
    }

    public boolean isEmpty() {
        return cart.isEmpty();
    }

    public List<Subject> getSubjects() {
        return new ArrayList<>(cart.values());
    }

    public int getTotalUnits() {
        return totalUnits;
    }

    private void addRow(Subject subject) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(Theme.CARD_BG);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
            new EmptyBorder(12, 16, 12, 16)));

        JPanel textGroup = new JPanel();
        textGroup.setOpaque(false);
        textGroup.setLayout(new BoxLayout(textGroup, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(subject.getCode() + " • " + subject.getDescription());
        title.setFont(Theme.BODY_FONT.deriveFont(Font.BOLD));
        title.setForeground(Theme.TEXT_HEADER);
        textGroup.add(title);

        JLabel meta = new JLabel(subject.getTime() + " • " + subject.getRoom() + " • " + subject.getUnits() + " Units");
        meta.setFont(Theme.LABEL_FONT);
        meta.setForeground(Theme.TEXT_BODY);
        textGroup.add(meta);

        JButton removeButton = new JButton("X");
        removeButton.setFont(Theme.BOLD_BODY_FONT);
        removeButton.setForeground(Theme.DANGER_COLOR);
        removeButton.setBackground(Color.WHITE);
        removeButton.setFocusPainted(false);
        removeButton.setBorder(BorderFactory.createLineBorder(Theme.DANGER_COLOR));
        removeButton.addActionListener(e -> removeSubject(subject.getCode()));

        row.add(textGroup, BorderLayout.CENTER);
        row.add(removeButton, BorderLayout.EAST);

        renderedRows.put(subject.getCode(), row);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
        listContainer.add(row);
        listContainer.add(Box.createVerticalStrut(8));
        listContainer.revalidate();
        listContainer.repaint();
    }

    private void removeSubject(String code) {
        Subject removed = cart.remove(code);
        if (removed != null) {
            totalUnits -= removed.getUnits();
            JPanel row = renderedRows.remove(code);
            if (row != null) {
                int index = -1;
                Component[] comps = listContainer.getComponents();
                for (int i = 0; i < comps.length; i++) {
                    if (comps[i] == row) {
                        index = i;
                        break;
                    }
                }
                if (index >= 0) {
                    listContainer.remove(index); // remove row
                    if (index < listContainer.getComponentCount()) {
                        Component next = listContainer.getComponent(index);
                        if (next instanceof Box.Filler) {
                            listContainer.remove(next);
                        }
                    }
                }
            }
            listContainer.revalidate();
            listContainer.repaint();
            updateHeader();
        }
    }

    private void updateHeader() {
        headerLabel.setText("Your Schedule (" + totalUnits + "/" + maxUnits + " Units)");
    }
}
