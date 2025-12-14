package ui.screens;

import ui.NavigationContext;
import ui.ScreenView;
import ui.components.MobileScrollPane;
import ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DigitalCORScreen extends JPanel implements ScreenView {

    private static final String[][] SUBJECT_ROWS = {
        {"IT 211", "Data Structures & Algo", "3", "Mon 7:00-10:00", "NB-101", "Prof. Dizon"},
        {"IT 212", "Object Oriented Prog", "3", "Tue 8:00-11:00", "CL-LAB2", "Prof. Navarro"},
        {"GEC 5", "Purposive Comm", "3", "Wed 1:00-4:00", "AVR-A", "Ms. Santos"},
        {"PE 3", "Individual Sports", "2", "Fri 8:00-10:00", "Gym", "Coach Ramos"},
        {"IT 211", "Data Structures & Algo", "3", "Thu 1:00-4:00", "NB-102", "Prof. Dizon"}
    };

    public DigitalCORScreen() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(buildScrollDocument(), BorderLayout.CENTER);
        add(buildBottomAction(), BorderLayout.SOUTH);
    }

    private JComponent buildScrollDocument() {
        JPanel document = new JPanel();
        document.setOpaque(false);
        document.setLayout(new BoxLayout(document, BoxLayout.Y_AXIS));

        JPanel card = new JPanel();
        card.setBackground(Theme.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
            new EmptyBorder(32, 32, 32, 32)));

        card.add(buildHeroSection());
        card.add(Box.createVerticalStrut(24));
        card.add(buildOfficialDetails());
        card.add(Box.createVerticalStrut(20));
        card.add(buildSubjectTableSection());
        card.add(Box.createVerticalStrut(20));
        card.add(buildFeeSummary());
        card.add(Box.createVerticalStrut(20));
        card.add(buildSecuritySection());

        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        document.add(card);

        MobileScrollPane scrollPane = new MobileScrollPane(document);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    private JComponent buildHeroSection() {
        JPanel hero = new JPanel();
        hero.setOpaque(false);
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel badge = new JLabel("OFFICIALLY ENROLLED", SwingConstants.CENTER);
        badge.setFont(Theme.HEADING_FONT.deriveFont(Font.BOLD, 28f));
        badge.setForeground(Theme.SUCCESS_COLOR);
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel term = new JLabel("A.Y. 2025-2026 • Second Semester", SwingConstants.CENTER);
        term.setFont(Theme.BODY_FONT);
        term.setForeground(Theme.TEXT_BODY);
        term.setAlignmentX(Component.CENTER_ALIGNMENT);

        hero.add(badge);
        hero.add(Box.createVerticalStrut(6));
        hero.add(term);
        return hero;
    }

    private JComponent buildOfficialDetails() {
        JPanel section = new JPanel(new GridLayout(2, 2, 16, 12));
        section.setOpaque(false);

        section.add(detailCell("Student Name", "Juan Dela Cruz"));
        section.add(detailCell("Student ID", "2025-01023"));
        section.add(detailCell("Program", "BS Information Technology"));
        section.add(detailCell("Status", "REGULAR"));
        return section;
    }

    private JPanel detailCell(String label, String value) {
        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));

        JLabel labelComp = new JLabel(label.toUpperCase());
        labelComp.setFont(Theme.LABEL_FONT);
        labelComp.setForeground(Theme.TEXT_BODY);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(Theme.SUBHEADER_FONT);
        valueComp.setForeground(Theme.TEXT_HEADER);

        cell.add(labelComp);
        cell.add(Box.createVerticalStrut(4));
        cell.add(valueComp);
        return cell;
    }

    private JComponent buildSubjectTableSection() {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BorderLayout(0, 12));

        JLabel title = new JLabel("Subject Summary");
        title.setFont(Theme.SUBHEADER_FONT);
        title.setForeground(Theme.TEXT_HEADER);

        JTable table = createSubjectTable();
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR));
        tableScroll.setPreferredSize(new Dimension(0, 180));

        section.add(title, BorderLayout.NORTH);
        section.add(tableScroll, BorderLayout.CENTER);
        return section;
    }

    private JTable createSubjectTable() {
        String[] columns = {"Course Code", "Subject Description", "Units", "Time", "Room", "Instructor"};
        DefaultTableModel model = new DefaultTableModel(SUBJECT_ROWS, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(Theme.BODY_FONT);
        table.setForeground(Theme.TEXT_HEADER);
        table.setBackground(Color.WHITE);
        table.getTableHeader().setFont(Theme.LABEL_FONT);
        table.getTableHeader().setBackground(Theme.BG_COLOR);
        table.getTableHeader().setForeground(Theme.TEXT_HEADER);
        table.setShowGrid(true);
        table.setGridColor(Theme.BORDER_COLOR);
        return table;
    }

    private JComponent buildFeeSummary() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 8));
        panel.setOpaque(false);

        panel.add(summaryRow("Total Due", "P 23,000.00", Theme.PRIMARY));
        panel.add(summaryRow("Payment Mode", "Full Payment", Theme.SUCCESS_COLOR));
        return panel;
    }

    private JPanel summaryRow(String label, String value, Color accent) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(Theme.SUBHEADER_FONT);
        labelComp.setForeground(Theme.TEXT_HEADER);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(Theme.SUBHEADER_FONT.deriveFont(Font.BOLD));
        valueComp.setForeground(accent);

        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.EAST);
        return row;
    }

    private JComponent buildSecuritySection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);

        JLabel caption = new JLabel("Security Validation", SwingConstants.LEFT);
        caption.setFont(Theme.SUBHEADER_FONT);
        caption.setForeground(Theme.TEXT_HEADER);

        JPanel qrMock = new JPanel();
        qrMock.setPreferredSize(new Dimension(140, 140));
        qrMock.setBackground(Color.BLACK);
        qrMock.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 4));

        JLabel qrLabel = new JLabel("QR CODE", SwingConstants.CENTER);
        qrLabel.setForeground(Color.WHITE);
        qrLabel.setFont(Theme.SUBHEADER_FONT);
        qrMock.setLayout(new BorderLayout());
        qrMock.add(qrLabel, BorderLayout.CENTER);

        wrapper.add(caption, BorderLayout.NORTH);
        wrapper.add(qrMock, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildBottomAction() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton action = new JButton("Save PDF / Print COR");
        action.setFont(Theme.SUBHEADER_FONT);
        action.setForeground(Color.WHITE);
        action.setBackground(Theme.PRIMARY);
        action.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        action.setFocusPainted(false);
        action.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Your digital COR has been queued for printing.",
            "Export Ready",
            JOptionPane.INFORMATION_MESSAGE));

        footer.add(action, BorderLayout.EAST);
        return footer;
    }

    @Override
    public void onEnter(NavigationContext context) {
        // Future enhancement: load real COR data via services.
    }

    @Override
    public void onLeave() {
        // No teardown necessary.
    }
}