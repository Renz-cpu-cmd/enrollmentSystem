package ui.screens;

/**
 * Block sectioning screen used in the enrollment flow.
 *
 * <p>Extends `JPanel` and implements `ScreenView` so it can be routed and
 * styled consistently with other screens.</p>
 */

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import dao.BlockDAO;
import model.Block;
import model.Schedule;
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
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockSectioningScreen extends JPanel implements ScreenView {

    // --- THEME COLORS ---
    private static final Color UNI_BLUE = new Color(0x0C5CB1);
    private static final Color GOLD = new Color(0xDAA520);
    private static final Color SLATE = new Color(0x64748B);
    private static final Color LIGHT_BG = new Color(0xF8FAFC);

    private static final String[] PREVIEW_COLUMNS = {
        "Code", "Subject", "Time", "Day", "Room", "Instructor", "Units"
    };

    private final EnrollmentService enrollmentService;
    private final BlockDAO blockDAO;

    private final DefaultListModel<BlockView> blockListModel = new DefaultListModel<>();
    private final JList<BlockView> blockList;
    private final DefaultTableModel scheduleModel;
    private final ModernTable scheduleTable;
    private final JLabel previewTitle;
    private final JLabel totalUnitsLabel;
    private final JLabel estimatedFeesLabel;
    
    // Assessment Box Labels
    private final JLabel tuitionLabel;
    private final JLabel miscLabel;
    private final JLabel labLabel;
    private final JLabel totalLabel;
    
    private final JButton confirmButton;
    private final CardLayout previewCardLayout = new CardLayout();
    private final JPanel previewCardPanel = new JPanel(previewCardLayout);

    private List<BlockView> allBlocks = new ArrayList<>();
    private String activeFilter = "All";

    public BlockSectioningScreen(EnrollmentService enrollmentService) {
        this(enrollmentService, null);
    }

    public BlockSectioningScreen(EnrollmentService enrollmentService, BlockDAO blockDAO) {
        this.enrollmentService = Objects.requireNonNull(enrollmentService, "EnrollmentService required");
        this.blockDAO = blockDAO;

        // --- LEFT LIST SETUP ---
        blockList = new JList<>(blockListModel);
        blockList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        blockList.setCellRenderer(new BlockCardRenderer());
        blockList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePreview(blockList.getSelectedValue());
            }
        });

        // --- RIGHT TABLE SETUP ---
        scheduleModel = new DefaultTableModel(PREVIEW_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scheduleTable = new ModernTable(scheduleModel);
        scheduleTable.setFillsViewportHeight(true);

        // --- LABELS & BUTTONS ---
        previewTitle = new JLabel("Select a block to preview schedule");
        previewTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        previewTitle.setForeground(new Color(23, 37, 84));

        totalUnitsLabel = new JLabel("Total Units: 0.0");
        totalUnitsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        totalUnitsLabel.setForeground(SLATE);

        estimatedFeesLabel = new JLabel("Est. Fees: 0.00");
        estimatedFeesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        estimatedFeesLabel.setForeground(UNI_BLUE);

        tuitionLabel = new JLabel("Tuition: 0.00");
        tuitionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        miscLabel = new JLabel("Misc: 0.00");
        miscLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        labLabel = new JLabel("Lab Fees: 0.00");
        labLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        totalLabel = new JLabel("TOTAL: 0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(GOLD.darker());

        confirmButton = new JButton("Confirm Enrollment");
        confirmButton.setEnabled(false);
        confirmButton.putClientProperty(FlatClientProperties.STYLE,
            "arc:18; background:#0C5CB1; foreground:#FFFFFF; font:+1;" +
            "hoverBackground:#0f6ed8; pressedBackground:#0a4f8d; focusWidth:2; innerFocusWidth:1;");
        confirmButton.setBorder(new EmptyBorder(12, 32, 12, 32));
        confirmButton.addActionListener(e -> confirmSelection());

        // --- MAIN LAYOUT ---
        setLayout(new BorderLayout(24, 24));
        setBackground(new Color(244, 247, 254));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(new WizardHeader(4), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        loadBlocks();
    }

    private JComponent buildContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeftPane(), buildRightPane());
        splitPane.setResizeWeight(0.35); // Give the list a bit more space
        splitPane.setDividerSize(4);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        return splitPane;
    }

    private JComponent buildLeftPane() {
        JPanel left = new JPanel(new BorderLayout(0, 12));
        left.setOpaque(false);
        left.add(buildFilters(), BorderLayout.NORTH);
        left.add(buildBlockListPanel(), BorderLayout.CENTER);
        return left;
    }

    private JComponent buildFilters() {
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        String[] labels = {"All", "Morning", "Afternoon"};
        for (String label : labels) {
            JToggleButton btn = new JToggleButton(label);
            btn.putClientProperty(FlatClientProperties.STYLE,
                "arc:12; background:#FFFFFF; borderColor:#CBD5E1;" +
                    "selectedBackground:#0C5CB1; selectedForeground:#FFFFFF;" +
                    "foreground:#0F172A; borderWidth:1; focusWidth:1; margin:4,12,4,12;");
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                activeFilter = label;
                filterBlocks();
            });
            if (label.equals(activeFilter)) {
                btn.setSelected(true);
            }
            group.add(btn);
            filters.add(btn);
        }
        return filters;
    }

    private JComponent buildBlockListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JLabel heading = new JLabel("Available Blocks");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(UNI_BLUE);
        panel.add(heading, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(blockList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildRightPane() {
        JPanel right = new JPanel(new BorderLayout(0, 12));
        right.setOpaque(false);
        right.add(buildPreviewPanel(), BorderLayout.CENTER);
        return right;
    }

    private JComponent buildPreviewPanel() {
        previewCardPanel.setOpaque(false);
        previewCardPanel.add(buildPreviewPlaceholder(), "empty");
        previewCardPanel.add(buildPreviewDetail(), "detail");
        previewCardLayout.show(previewCardPanel, "empty");
        return previewCardPanel;
    }

    private JComponent buildPreviewPlaceholder() {
        JPanel placeholderCard = createPaperCard();
        JLabel icon = new JLabel("\uD83D\uDCC5", SwingConstants.CENTER); // Calendar icon
        icon.setFont(new Font("Segoe UI", Font.BOLD, 48));
        icon.setForeground(new Color(203, 213, 225));
        
        JLabel label = new JLabel("Select a block to generate your schedule.", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(SLATE);
        
        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(icon, BorderLayout.CENTER);
        center.add(label, BorderLayout.SOUTH);
        
        placeholderCard.add(center, BorderLayout.CENTER);
        return placeholderCard;
    }

    private JComponent buildPreviewDetail() {
        JPanel detailCard = createPaperCard();

        JLabel header = new JLabel("Certificate of Registration (Preview)");
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(UNI_BLUE);
        detailCard.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);

        body.add(previewTitle, BorderLayout.NORTH);

        JScrollPane tableScroll = new JScrollPane(scheduleTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        ModernTable.applySmartScrolling(tableScroll);
        body.add(tableScroll, BorderLayout.CENTER);

        body.add(buildFinancials(), BorderLayout.SOUTH);
        detailCard.add(body, BorderLayout.CENTER);
        return detailCard;
    }

    private JPanel createPaperCard() {
        JPanel card = new JPanel(new BorderLayout(16, 16));
        card.setBackground(Color.WHITE);
        // Paper effect: Subtle border + Drop Shadow
        card.setBorder(new CompoundBorder(
            new FlatDropShadowBorder(), 
            new CompoundBorder(new LineBorder(new Color(226, 232, 240), 1, true), new EmptyBorder(24, 24, 24, 24))
        ));
        return card;
    }

    private JPanel buildFinancials() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        // -- Assessment Box --
        JPanel assessBox = new JPanel();
        assessBox.setOpaque(true);
        assessBox.setBackground(new Color(248, 250, 252));
        assessBox.setBorder(new CompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true), 
            new EmptyBorder(12, 16, 12, 16))
        );
        assessBox.setLayout(new BoxLayout(assessBox, BoxLayout.Y_AXIS));
        
        assessBox.add(tuitionLabel);
        assessBox.add(Box.createVerticalStrut(4));
        assessBox.add(miscLabel);
        assessBox.add(Box.createVerticalStrut(4));
        assessBox.add(labLabel);
        assessBox.add(Box.createVerticalStrut(8));
        assessBox.add(new JSeparator());
        assessBox.add(Box.createVerticalStrut(8));
        assessBox.add(totalLabel);

        wrapper.add(assessBox, BorderLayout.EAST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(16,0,0,0));
        actions.add(confirmButton);
        
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(wrapper, BorderLayout.CENTER);
        container.add(actions, BorderLayout.SOUTH);
        
        return container;
    }

    // --- LOGIC: GENERATE MOCK BLOCKS ---
    private void loadBlocks() {
        blockListModel.clear();
        
        // 1. Get Program from Session
        String rawCode = SessionManager.getInstance().getSelectedProgramCode();
        String rawName = SessionManager.getInstance().getSelectedProgramName();
        
        // DEBUG: Check what the waiter wrote down
        System.out.println("DEBUG: BlockScreen loaded. Session Program: [" + rawCode + "] Name: [" + rawName + "]");

        // 2. Normalize (Smart Detection)
        String programCode = normalizeProgramCode(rawCode, rawName);
        System.out.println("DEBUG: Normalized to: [" + programCode + "]");

        // 3. Generate 3 Sections (A, B, C)
        allBlocks = new ArrayList<>();
        
        // Morning
        allBlocks.add(createBlockFor(programCode, "A", "Morning Session", "07:30", "08:30", "09:30", "10:30"));
        // Afternoon
        allBlocks.add(createBlockFor(programCode, "B", "Afternoon Session", "13:00", "14:00", "15:00", "16:00"));
        // Evening
        allBlocks.add(createBlockFor(programCode, "C", "Evening Session", "17:00", "18:00", "19:00", "20:00"));

        filterBlocks();
    }

    private String normalizeProgramCode(String code, String name) {
        String combined = (code + " " + name).toUpperCase();
        
        // RIGOROUS CHECKING
        if (combined.contains("BSN") || combined.contains("NURS")) return "BSN";
        if (combined.contains("BSBA") || combined.contains("BUSINESS")) return "BSBA";
        if (combined.contains("ACCY") || combined.contains("ACCOUNT")) return "BSACCY";
        if (combined.contains("BSED") || combined.contains("EDUC") || combined.contains("TEACH")) return "BSED";
        if (combined.contains("CS") || combined.contains("COMP")) return "BSCS";
        
        return "BSIT"; // Default
    }

    private BlockView createBlockFor(String code, String section, String shift, String t1, String t2, String t3, String t4) {
        List<ScheduleEntry> entries = new ArrayList<>();
        
        // --- THE BRAIN: ASSIGN SUBJECTS BASED ON PROGRAM ---
        switch (code) {
            case "BSN" -> { // NURSING
                entries.add(new ScheduleEntry("NURS 101", "Anatomy & Physiology", t1 + " - " + t2, "Mon/Wed", "NUR-LAB", "Dr. Santos", 5.0));
                entries.add(new ScheduleEntry("NCM 100", "Theoretical Foundations", t2 + " - " + t3, "Tue/Thu", "NUR-201", "Prof. Cruz", 3.0));
                entries.add(new ScheduleEntry("BIO 101", "Biochemistry", t3 + " - " + t4, "Fri", "SCI-LAB", "Dr. Reyes", 3.0));
                entries.add(new ScheduleEntry("GEC 101", "Understanding the Self", "Sat 09:00-12:00", "Sat", "MAIN-101", "Ms. Lee", 3.0));
            }
            case "BSBA" -> { // BUSINESS
                entries.add(new ScheduleEntry("MKTG 101", "Marketing Management", t1 + " - " + t2, "Mon/Wed", "BUS-101", "Prof. Go", 3.0));
                entries.add(new ScheduleEntry("ECON 101", "Microeconomics", t2 + " - " + t3, "Tue/Thu", "BUS-102", "Dr. Tan", 3.0));
                entries.add(new ScheduleEntry("MGMT 101", "Principles of Mgt", t3 + " - " + t4, "Fri", "BUS-103", "Prof. Lim", 3.0));
            }
            case "BSACCY" -> { // ACCOUNTANCY
                entries.add(new ScheduleEntry("ACCY 101", "Financial Accounting", t1 + " - " + t3, "Mon/Wed", "ACC-LAB", "CPA Dizon", 6.0));
                entries.add(new ScheduleEntry("LAW 101", "ObliCon", t3 + " - " + t4, "Tue/Thu", "ACC-101", "Atty. Yap", 3.0));
                entries.add(new ScheduleEntry("MATH 101", "Business Calculus", "Sat 08:00-11:00", "Sat", "MAIN-202", "Engr. Co", 3.0));
            }
            case "BSED" -> { // EDUCATION
                entries.add(new ScheduleEntry("EDUC 101", "Child & Adol. Dev", t1 + " - " + t2, "Mon/Wed", "ED-101", "Dr. Pineda", 3.0));
                entries.add(new ScheduleEntry("EDUC 102", "Facilitating Learning", t2 + " - " + t3, "Tue/Thu", "ED-102", "Prof. Solis", 3.0));
                entries.add(new ScheduleEntry("GEC 104", "Purposive Comm", t3 + " - " + t4, "Fri", "MAIN-301", "Ms. Abad", 3.0));
            }
            default -> { // IT / CS
                entries.add(new ScheduleEntry("CC 101", "Intro to Computing", t1 + " - " + t2, "Mon/Wed", "COM-LAB1", "Prof. Sison", 3.0));
                entries.add(new ScheduleEntry("CC 102", "Programming 1", t2 + " - " + t3, "Tue/Thu", "COM-LAB2", "Engr. David", 3.0));
                entries.add(new ScheduleEntry("GEC 108", "Ethics", t3 + " - " + t4, "Fri", "MAIN-105", "Fr. Ocampo", 3.0));
            }
        }

        double totalUnits = entries.stream().mapToDouble(ScheduleEntry::units).sum();
        double tuition = totalUnits * 1500;
        double misc = 5500;
        double lab = code.equals("BSN") || code.equals("BSIT") ? 2500 : 1000;
        
        return new BlockView(code + " 1-" + section, shift, 30, 40, entries, totalUnits, tuition, misc, lab);
    }

    private void filterBlocks() {
        blockListModel.clear();
        allBlocks.stream().filter(this::matchesFilter).forEach(blockListModel::addElement);
        if (!blockListModel.isEmpty()) {
            blockList.setSelectedIndex(0);
        } else {
            previewCardLayout.show(previewCardPanel, "empty");
            confirmButton.setEnabled(false);
        }
    }

    private boolean matchesFilter(BlockView block) {
        if (activeFilter.equals("All")) return true;
        return block.shift().contains(activeFilter);
    }

    private void updatePreview(BlockView block) {
        if (block == null) {
            previewCardLayout.show(previewCardPanel, "empty");
            confirmButton.setEnabled(false);
            return;
        }

        previewTitle.setText("Schedule Preview for " + block.name());
        scheduleModel.setRowCount(0);
        for (ScheduleEntry entry : block.schedule()) {
            scheduleModel.addRow(new Object[] {
                entry.code(), entry.subject(), entry.time(), entry.day(), entry.room(), entry.instructor(), entry.units()
            });
        }
        
        // Update Labels
        totalUnitsLabel.setText(String.format("Total Units: %.1f", block.totalUnits()));
        estimatedFeesLabel.setText("Est. Fees: " + formatCurrency(block.tuition + block.misc + block.lab));
        
        tuitionLabel.setText("Tuition: " + formatCurrency(block.tuition));
        miscLabel.setText("Misc: " + formatCurrency(block.misc));
        labLabel.setText("Lab Fees: " + formatCurrency(block.lab));
        totalLabel.setText("Total: " + formatCurrency(block.tuition + block.misc + block.lab));

        confirmButton.setEnabled(true);
        previewCardLayout.show(previewCardPanel, "detail");
    }

    private void confirmSelection() {
        BlockView block = blockList.getSelectedValue();
        if (block == null) return;

        EnrollmentService.ServiceResult<Void> result = enrollmentService.processEnrollment(block.name());
        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Enrollment confirmed.\nProceeding to Assessment.", "Success", JOptionPane.INFORMATION_MESSAGE);
            Navigation.to(this, Screen.ASSESSMENT);
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatCurrency(double value) {
        return "₱ " + new DecimalFormat("#,##0.00").format(value);
    }

    @Override
    public void onEnter(NavigationContext context) {
        loadBlocks(); // Reload every time we enter to catch Program changes
    }

    @Override
    public void onLeave() {}

    // --- UI RENDERER (The Ticket Look) ---
    private class BlockCardRenderer extends JPanel implements ListCellRenderer<BlockView> {
        BlockCardRenderer() {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(0, 95)); // FORCE HEIGHT
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends BlockView> list, BlockView value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            removeAll();
            
            // Container with padding
            JPanel card = new JPanel(new BorderLayout(12, 6));
            card.setOpaque(true);
            card.setBackground(Color.WHITE);
            
            // THE GOLD STRIP (Border)
            Border baseBorder = new CompoundBorder(new MatteBorder(0, 4, 0, 0, GOLD), new EmptyBorder(12, 12, 12, 12));
            if (isSelected) {
                card.setBackground(new Color(0xEFF6FF)); // Light Blue highlight
                baseBorder = new CompoundBorder(new MatteBorder(0, 4, 0, 0, UNI_BLUE), new EmptyBorder(12, 12, 12, 12));
            }
            card.setBorder(new CompoundBorder(new FlatDropShadowBorder(), baseBorder));

            // Content
            JLabel nameLabel = new JLabel(value.name());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            nameLabel.setForeground(UNI_BLUE);
            
            JLabel shiftLabel = new JLabel(value.shift());
            shiftLabel.setOpaque(true);
            shiftLabel.setBackground(new Color(0xF1F5F9));
            shiftLabel.setForeground(SLATE);
            shiftLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            shiftLabel.setBorder(new EmptyBorder(4, 12, 4, 12));
            // FIXED: Removed arc style to prevent crash.

            JPanel textPanel = new JPanel(new GridLayout(2, 1));
            textPanel.setOpaque(false);
            textPanel.add(nameLabel);
            textPanel.add(shiftLabel);

            JLabel statusLabel = new JLabel("Slots Available");
            statusLabel.setForeground(new Color(22, 163, 74));
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            
            JProgressBar capacityBar = new JProgressBar(0, value.capacity());
            capacityBar.setValue(value.enrolled());
            capacityBar.setStringPainted(false);
            capacityBar.setPreferredSize(new Dimension(100, 6));
            // FIXED: Removed trackWidth style to prevent crash.
            capacityBar.setForeground(new Color(0x0C5CB1));
            capacityBar.setBackground(new Color(0xE2E8F0));

            card.add(textPanel, BorderLayout.CENTER);
            
            JPanel bottom = new JPanel(new BorderLayout(0,4));
            bottom.setOpaque(false);
            bottom.add(statusLabel, BorderLayout.NORTH);
            bottom.add(capacityBar, BorderLayout.SOUTH);
            card.add(bottom, BorderLayout.SOUTH);

            // Add card to renderer
            add(card, BorderLayout.CENTER);
            setBorder(new EmptyBorder(4, 8, 4, 8)); // Gap between items
            setOpaque(false);
            
            return this;
        }
    }

    // --- RECORDS ---
    private record BlockView(String name, String shift, int enrolled, int capacity,
                             List<ScheduleEntry> schedule, double totalUnits, 
                             double tuition, double misc, double lab) {
    }

    private record ScheduleEntry(String code, String subject, String time,
                                 String day, String room, String instructor, double units) {
    }
}