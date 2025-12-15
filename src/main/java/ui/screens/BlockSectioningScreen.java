package ui.screens;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import service.EnrollmentService;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.components.ModernTable;
import ui.components.WizardHeader;
import ui.theme.Theme;
import util.Navigation;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BlockSectioningScreen extends JPanel implements ScreenView {

    private static final String[] PREVIEW_COLUMNS = {
        "Code", "Subject", "Time", "Day", "Room", "Instructor", "Units"
    };

    private final EnrollmentService enrollmentService;
    private final DefaultListModel<Block> blockListModel = new DefaultListModel<>();
    private final JList<Block> blockList;
    private final DefaultTableModel scheduleModel;
    private final ModernTable scheduleTable;
    private final JLabel previewTitle;
    private final JLabel totalUnitsLabel;
    private final JLabel estimatedFeesLabel;
    private final JButton confirmButton;
    private final CardLayout previewCardLayout = new CardLayout();
    private final JPanel previewCardPanel = new JPanel(previewCardLayout);

    public BlockSectioningScreen(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
        setLayout(new BorderLayout(24, 24));
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(new WizardHeader(4), BorderLayout.NORTH);

        blockList = new JList<>(blockListModel);
        blockList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        blockList.setCellRenderer(new BlockCardRenderer());
        blockList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePreview(blockList.getSelectedValue());
            }
        });

        scheduleModel = new DefaultTableModel(PREVIEW_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scheduleTable = new ModernTable(scheduleModel);
        scheduleTable.setFillsViewportHeight(true);

        previewTitle = new JLabel("Select a block to preview schedule");
        previewTitle.setFont(Theme.SUBHEADER_FONT);
        previewTitle.setForeground(new Color(31, 41, 55));

        totalUnitsLabel = new JLabel("Total Units: —");
        totalUnitsLabel.setFont(Theme.BODY_FONT);
        totalUnitsLabel.setForeground(new Color(79, 89, 107));

        estimatedFeesLabel = new JLabel("Est. Fees: —");
        estimatedFeesLabel.setFont(Theme.BOLD_BODY_FONT);
        estimatedFeesLabel.setForeground(new Color(12, 92, 177));

        confirmButton = new JButton("Confirm Enrollment");
        confirmButton.setEnabled(false);
        confirmButton.putClientProperty(FlatClientProperties.STYLE,
            "arc:18; background:#0C5CB1; foreground:#FFFFFF; font:+1;" +
                "hoverBackground:#0f6ed8; pressedBackground:#0a4f8d; focusWidth:2; innerFocusWidth:1;");
        confirmButton.setBorder(new EmptyBorder(12, 32, 12, 32));
        confirmButton.addActionListener(e -> confirmSelection());

        add(buildSplitPane(), BorderLayout.CENTER);
        loadMockBlocks();
    }

    private JSplitPane buildSplitPane() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildBlockListPanel(),
            buildPreviewPanel());
        splitPane.setResizeWeight(0.3);
        splitPane.setDividerSize(2);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        return splitPane;
    }

    private JComponent buildBlockListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);
        panel.setBorder(new CompoundBorder(new FlatRoundBorder(), new EmptyBorder(16, 16, 16, 16)));

        JLabel heading = new JLabel("Available Blocks");
        heading.setFont(Theme.SUBHEADER_FONT);
        heading.setForeground(new Color(31, 41, 55));
        panel.add(heading, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(blockList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildPreviewPanel() {
        previewCardPanel.setOpaque(false);
        previewCardPanel.add(buildPreviewPlaceholder(), "empty");
        previewCardPanel.add(buildPreviewDetail(), "detail");
        previewCardLayout.show(previewCardPanel, "empty");
        return previewCardPanel;
    }

    private JComponent buildPreviewPlaceholder() {
        JPanel placeholderCard = createCardPanel();
        JLabel label = new JLabel("<html><div style='text-align:center;width:320px;'>Select a block on the left to preview its schedule and tuition breakdown.</div></html>");
        label.setFont(Theme.BODY_FONT);
        label.setForeground(new Color(107, 114, 128));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        placeholderCard.add(label, BorderLayout.CENTER);
        return placeholderCard;
    }

    private JComponent buildPreviewDetail() {
        JPanel detailCard = createCardPanel();
        detailCard.add(previewTitle, BorderLayout.NORTH);

        JScrollPane tableScroll = new JScrollPane(scheduleTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        ModernTable.applySmartScrolling(tableScroll);
        detailCard.add(tableScroll, BorderLayout.CENTER);

        detailCard.add(buildSummaryBar(), BorderLayout.SOUTH);
        return detailCard;
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel(new BorderLayout(16, 16));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(new FlatDropShadowBorder(), new EmptyBorder(24, 24, 24, 24)));
        return card;
    }

    private JPanel buildSummaryBar() {
        JPanel summary = new JPanel(new BorderLayout(16, 0));
        summary.setOpaque(false);

        JPanel stats = new JPanel();
        stats.setOpaque(false);
        stats.setLayout(new BoxLayout(stats, BoxLayout.Y_AXIS));
        stats.add(totalUnitsLabel);
        stats.add(Box.createVerticalStrut(6));
        stats.add(estimatedFeesLabel);

        summary.add(stats, BorderLayout.WEST);
        summary.add(confirmButton, BorderLayout.EAST);
        return summary;
    }

    private void loadMockBlocks() {
        blockListModel.clear();
        for (Block block : createMockBlocks()) {
            blockListModel.addElement(block);
        }
        if (!blockListModel.isEmpty()) {
            blockList.setSelectedIndex(0);
        } else {
            previewCardLayout.show(previewCardPanel, "empty");
        }
    }

    private List<Block> createMockBlocks() {
        String programName = SessionManager.getInstance().getSelectedProgramName();
        if (programName == null || programName.isBlank()) {
            programName = "BSIT";
        }

        List<Block> blocks = new ArrayList<>();
        blocks.add(new Block(programName + " 1-A", "Morning Session", 32, 40,
            List.of(
                new ScheduleEntry("IT 101", "Programming Fundamentals", "08:00 - 10:00", "Mon/Wed", "CL-201", "Prof. Serrano", 3.0),
                new ScheduleEntry("IT 102", "Discrete Mathematics", "10:00 - 11:30", "Mon/Wed", "NB-105", "Dr. Luna", 3.0),
                new ScheduleEntry("GEC 104", "Purposive Communication", "13:00 - 15:00", "Tue/Thu", "AVR-A", "Ms. Valdez", 3.0),
                new ScheduleEntry("IT 103", "Web Technologies", "08:00 - 11:00", "Tue", "CL-202", "Prof. Reyes", 3.0),
                new ScheduleEntry("PE 102", "Fitness & Wellness", "07:00 - 08:00", "Fri", "Gym", "Coach Ocampo", 2.0),
                new ScheduleEntry("NSTP 2", "CWTS 2", "09:00 - 12:00", "Sat", "Field", "Mr. Lazo", 3.0)
            ),
            23.0, 24500));

        blocks.add(new Block(programName + " 1-B", "Afternoon Session", 40, 40,
            List.of(
                new ScheduleEntry("IT 101", "Programming Fundamentals", "13:00 - 15:00", "Mon/Wed", "CL-203", "Prof. Serrano", 3.0),
                new ScheduleEntry("IT 104", "Data Structures", "15:00 - 17:00", "Mon/Wed", "CL-204", "Prof. Diaz", 3.0),
                new ScheduleEntry("GEC 102", "Understanding the Self", "10:00 - 12:00", "Tue/Thu", "NB-210", "Ms. Gomez", 3.0),
                new ScheduleEntry("IT 105", "Computer Systems Servicing", "13:00 - 16:00", "Tue", "CL-205", "Engr. De Vera", 3.0),
                new ScheduleEntry("PE 102", "Fitness & Wellness", "16:00 - 17:00", "Fri", "Gym", "Coach Ocampo", 2.0),
                new ScheduleEntry("NSTP 2", "CWTS 2", "09:00 - 12:00", "Sat", "Field", "Mr. Lazo", 3.0)
            ),
            23.0, 24850));

        blocks.add(new Block(programName + " 1-C", "Morning Session", 28, 35,
            List.of(
                new ScheduleEntry("IT 107", "Database Systems", "08:30 - 10:30", "Mon/Wed", "CL-101", "Prof. Santos", 3.0),
                new ScheduleEntry("IT 108", "Networks 1", "10:30 - 12:00", "Mon/Wed", "CL-102", "Engr. Ramos", 3.0),
                new ScheduleEntry("GEC 108", "Ethics", "13:00 - 15:00", "Tue/Thu", "NB-204", "Ms. Flores", 3.0),
                new ScheduleEntry("IT 109", "Human Computer Interaction", "08:00 - 11:00", "Fri", "CL-103", "Prof. Bautista", 3.0),
                new ScheduleEntry("PE 103", "Individual Sports", "07:00 - 08:00", "Tue", "Gym", "Coach Cruz", 2.0),
                new ScheduleEntry("PROJ 101", "Innovation Studio", "09:00 - 12:00", "Thu", "Makerspace", "Ms. Go", 4.0)
            ),
            24.0, 25790));

        return blocks;
    }

    private void updatePreview(Block block) {
        if (block == null) {
            previewCardLayout.show(previewCardPanel, "empty");
            confirmButton.setEnabled(false);
            return;
        }

        previewTitle.setText("Schedule Preview for " + block.name());
        scheduleModel.setRowCount(0);
        for (ScheduleEntry entry : block.schedule()) {
            scheduleModel.addRow(new Object[] {
                entry.code(),
                entry.subject(),
                entry.time(),
                entry.day(),
                entry.room(),
                entry.instructor(),
                entry.units()
            });
        }
        totalUnitsLabel.setText(String.format("Total Units: %.1f", block.totalUnits()));
        estimatedFeesLabel.setText("Est. Fees: " + formatCurrency(block.estimatedFees()));

        boolean hasRoom = block.enrolled() < block.capacity();
        confirmButton.setEnabled(hasRoom);
        confirmButton.setText(hasRoom ? "Confirm Enrollment" : "Block is Full");
        previewCardLayout.show(previewCardPanel, "detail");
    }

    private void confirmSelection() {
        Block block = blockList.getSelectedValue();
        if (block == null) {
            JOptionPane.showMessageDialog(this, "Please choose a block before confirming.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (block.enrolled() >= block.capacity()) {
            JOptionPane.showMessageDialog(this, "Selected block is already full. Pick another block.", "Block Full", JOptionPane.WARNING_MESSAGE);
            return;
        }

        EnrollmentService.ServiceResult<Void> result = enrollmentService.processEnrollment(block.name());
        if (result.isSuccess()) {
            AssessmentScreen.returnScreen = Screen.BLOCK_SECTIONING;
            JOptionPane.showMessageDialog(this,
                result.getMessage().isBlank() ? "Enrollment confirmed." : result.getMessage(),
                "Enrollment", JOptionPane.INFORMATION_MESSAGE);
            Navigation.to(this, Screen.ASSESSMENT);
        } else {
            JOptionPane.showMessageDialog(this,
                result.getMessage().isBlank() ? "Unable to process enrollment." : result.getMessage(),
                "Enrollment Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatCurrency(double value) {
        return "₱ " + new DecimalFormat("#,##0.00").format(value);
    }

    @Override
    public void onEnter(NavigationContext context) {
        // Future: Load real blocks from backend.
    }

    @Override
    public void onLeave() {
        // No teardown required.
    }

    private static class BlockCardRenderer extends JPanel implements ListCellRenderer<Block> {
        BlockCardRenderer() {
            setLayout(new BorderLayout());
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Block> list, Block value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            removeAll();
            JPanel card = new JPanel(new BorderLayout(12, 8));
            card.setBorder(buildCardBorder(isSelected));
            card.setBackground(Color.WHITE);

            JLabel blockName = new JLabel(value.name());
            blockName.setFont(Theme.BOLD_BODY_FONT);
            blockName.setForeground(new Color(15, 23, 42));

            JLabel shiftBadge = new JLabel(value.shift());
            shiftBadge.setOpaque(true);
            shiftBadge.setForeground(Color.WHITE);
            shiftBadge.setFont(Theme.LABEL_FONT);
            shiftBadge.setBorder(new EmptyBorder(4, 12, 4, 12));
            if ("Morning Session".equalsIgnoreCase(value.shift())) {
                shiftBadge.setBackground(new Color(245, 158, 11));
            } else {
                shiftBadge.setBackground(new Color(59, 130, 246));
            }

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.add(blockName, BorderLayout.WEST);
            header.add(shiftBadge, BorderLayout.EAST);

            JProgressBar capacityBar = new JProgressBar(0, value.capacity());
            capacityBar.setValue(value.enrolled());
            capacityBar.setString(String.format("%d/%d Enrolled", value.enrolled(), value.capacity()));
            capacityBar.setStringPainted(true);

            JLabel status = new JLabel(value.enrolled() >= value.capacity() ? "Full" : "Slots Available");
            status.setFont(Theme.LABEL_FONT);
            status.setForeground(value.enrolled() >= value.capacity() ? new Color(239, 68, 68) : new Color(16, 185, 129));

            card.add(header, BorderLayout.NORTH);
            card.add(capacityBar, BorderLayout.CENTER);
            card.add(status, BorderLayout.SOUTH);

            boolean full = value.enrolled() >= value.capacity();
            if (full) {
                card.setBackground(new Color(248, 250, 252));
                card.setForeground(new Color(148, 163, 184));
            }

            setOpaque(false);
            add(card, BorderLayout.CENTER);
            return this;
        }

        private Border buildCardBorder(boolean isSelected) {
            Border inner = new CompoundBorder(new FlatRoundBorder(), new EmptyBorder(12, 12, 12, 12));
            if (isSelected) {
                inner = new CompoundBorder(new LineBorder(new Color(14, 116, 144), 2, true), new EmptyBorder(12, 12, 12, 12));
            }
            return new CompoundBorder(new FlatDropShadowBorder(), inner);
        }
    }

    private record Block(String name, String shift, int enrolled, int capacity,
                         List<ScheduleEntry> schedule, double totalUnits, double estimatedFees) {
    }

    private record ScheduleEntry(String code, String subject, String time,
                                 String day, String room, String instructor, double units) {
    }
}