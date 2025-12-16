package ui.screens;

import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import dao.AssessmentDAO;
import dao.EnrollmentDAO;
import model.Assessment;
import model.AssessmentFee;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.components.ModernTable;
import ui.theme.Theme;
import util.Navigation;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssessmentScreen extends JPanel implements ScreenView {

    public static Screen returnScreen = Screen.BLOCK_SECTIONING;

    private static final Color PRIMARY = new Color(0x0C5CB1);
    private static final Color PRIMARY_DARK = new Color(0x0A2C63);
    private static final Color SLATE = new Color(0x334155);
    private static final Color PAPER = Color.WHITE;
    private static final Color CARD_BORDER = new Color(0xE2E8F0);
    private static final Color SUCCESS = new Color(0x16A34A);
    private static final Color WARNING = new Color(0xEAB308);
    private static final Font MONO = new Font("Consolas", Font.PLAIN, 14);
    private static final Font MONO_BOLD = new Font("Consolas", Font.BOLD, 18);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd");

    private final AssessmentDAO assessmentDAO;
    private final EnrollmentDAO enrollmentDAO;

    private Assessment currentAssessment;
    private List<PaymentPlan> paymentPlans = new ArrayList<>();
    private PaymentPlan selectedPlan;

    private final DefaultTableModel feeModel;
    private final DefaultTableModel scheduleModel;
    private final JLabel subtotalLabel;
    private final JLabel totalUnitsLabel;
    private final JLabel totalAssessmentLabel;
    private final JLabel refLabel;
    private final JLabel verifiedBadge;
    private final JPanel planCardsPanel;
    private final JPanel schedulePanel;
    private final ButtonGroup planGroup = new ButtonGroup();
    private final JButton payButton;
    private final JButton printButton;
    private final JButton downloadButton;
    private final DistributionBar distributionBar;

    public AssessmentScreen(EnrollmentDAO enrollmentDAO, AssessmentDAO assessmentDAO) {
        this.enrollmentDAO = enrollmentDAO;
        this.assessmentDAO = assessmentDAO;

        setLayout(new BorderLayout(16, 16));
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        feeModel = new DefaultTableModel(new Object[]{"Description", "Amount"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        scheduleModel = new DefaultTableModel(new Object[]{"Due", "Amount"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        subtotalLabel = new JLabel("Subtotal: --");
        subtotalLabel.setFont(MONO);
        subtotalLabel.setForeground(SLATE);

        totalUnitsLabel = new JLabel("Total Units: --");
        totalUnitsLabel.setFont(MONO);
        totalUnitsLabel.setForeground(SLATE);

        totalAssessmentLabel = new JLabel("Total Assessment: --");
        totalAssessmentLabel.setFont(MONO_BOLD);
        totalAssessmentLabel.setForeground(PRIMARY);

        refLabel = new JLabel("Ref #: --");
        refLabel.setFont(Theme.BODY_FONT);
        refLabel.setForeground(SLATE);

        verifiedBadge = new JLabel("✔ Officially Assessed");
        verifiedBadge.setFont(Theme.LABEL_FONT.deriveFont(Font.BOLD));
        verifiedBadge.setOpaque(true);
        verifiedBadge.setForeground(new Color(0x166534));
        verifiedBadge.setBackground(new Color(0xECFDF3));
        verifiedBadge.setBorder(new LineBorder(new Color(0x22C55E), 1, true));
        verifiedBadge.setBorder(new CompoundBorder(new LineBorder(new Color(0x22C55E), 1, true), new EmptyBorder(4, 8, 4, 8)));

        planCardsPanel = new JPanel();
        planCardsPanel.setOpaque(false);
        planCardsPanel.setLayout(new BoxLayout(planCardsPanel, BoxLayout.Y_AXIS));

        schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.setOpaque(false);

        payButton = new JButton("Proceed to Payment");
        payButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        payButton.setForeground(Color.WHITE);
        payButton.setBackground(PRIMARY);
        payButton.setOpaque(true);
        payButton.setBorder(new EmptyBorder(16, 16, 16, 16));
        payButton.addActionListener(e -> handlePay());

        printButton = createIconButton("⎙", "Print Statement");
        downloadButton = createIconButton("⇩", "Download PDF");

        distributionBar = new DistributionBar();
        distributionBar.setPreferredSize(new Dimension(10, 8));

        add(buildContent(), BorderLayout.CENTER);

        loadAssessmentAsync();
    }

    private JComponent buildContent() {
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            buildInvoicePanel(),
            buildPaymentPanel()
        );
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(12);
        splitPane.setOpaque(false);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        return splitPane;
    }

    private JComponent buildInvoicePanel() {
        JPanel card = createPaperCard();
        card.setLayout(new BorderLayout(0, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Statement of Account");
        title.setFont(Theme.SUBHEADER_FONT.deriveFont(Font.BOLD, 22f));
        title.setForeground(PRIMARY);

        String studentName = Optional.ofNullable(SessionManager.getInstance().getCurrentStudent())
            .map(s -> s.getFirstName() + " " + s.getLastName())
            .orElse("Student");
        JLabel studentLabel = new JLabel(studentName);
        studentLabel.setFont(Theme.BODY_FONT);
        studentLabel.setForeground(SLATE);

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(studentLabel);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        toolbar.setOpaque(false);
        toolbar.add(printButton);
        toolbar.add(downloadButton);

        JPanel refRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        refRow.setOpaque(false);
        refRow.add(refLabel);
        refRow.add(verifiedBadge);

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.add(toolbar, BorderLayout.NORTH);
        right.add(refRow, BorderLayout.SOUTH);

        header.add(titleBox, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        ModernTable feeTable = new ModernTable(feeModel);
        feeTable.setShowHorizontalLines(true);
        feeTable.setShowVerticalLines(false);
        feeTable.setRowHeight(32);
        feeTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);
        rightAlign.setFont(MONO);
        feeTable.getColumnModel().getColumn(1).setCellRenderer(rightAlign);

        JScrollPane scroll = new JScrollPane(feeTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(scroll, BorderLayout.CENTER);
        center.add(buildDistributionRow(), BorderLayout.SOUTH);

        card.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new GridLayout(3, 1, 0, 6));
        footer.setOpaque(false);
        JPanel subtotalRow = new JPanel(new BorderLayout());
        subtotalRow.setOpaque(false);
        subtotalRow.add(subtotalLabel, BorderLayout.EAST);

        JPanel unitsRow = new JPanel(new BorderLayout());
        unitsRow.setOpaque(false);
        unitsRow.add(totalUnitsLabel, BorderLayout.EAST);

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.add(totalAssessmentLabel, BorderLayout.EAST);

        footer.add(subtotalRow);
        footer.add(unitsRow);
        footer.add(totalRow);

        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private JComponent buildDistributionRow() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(distributionBar, BorderLayout.CENTER);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        legend.setOpaque(false);
        legend.add(buildLegendDot("Tuition", PRIMARY));
        legend.add(buildLegendDot("Misc", new Color(0xF97316)));
        legend.add(buildLegendDot("Lab", new Color(0x22C55E)));
        wrapper.add(legend, BorderLayout.SOUTH);
        return wrapper;
    }

    private JLabel buildLegendDot(String label, Color color) {
        JLabel dot = new JLabel(label);
        dot.setOpaque(true);
        dot.setBackground(color);
        dot.setForeground(Color.WHITE);
        dot.setFont(Theme.LABEL_FONT);
        dot.setBorder(new EmptyBorder(3, 8, 3, 8));
        return dot;
    }

    private JButton createIconButton(String symbol, String tooltip) {
        JButton btn = new JButton(symbol);
        btn.setPreferredSize(new Dimension(32, 32));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(SLATE);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setToolTipText(tooltip);
        btn.setBorder(new LineBorder(CARD_BORDER, 1, true));
        return btn;
    }

    private JComponent buildPaymentPanel() {
        JPanel card = createPaperCard();
        card.setLayout(new BorderLayout(0, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Choose Payment Terms");
        title.setFont(Theme.SUBHEADER_FONT.deriveFont(Font.BOLD, 20f));
        title.setForeground(new Color(15, 23, 42));
        header.add(title, BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);

        JScrollPane plansScroll = new JScrollPane(planCardsPanel);
        plansScroll.setBorder(BorderFactory.createEmptyBorder());
        plansScroll.getVerticalScrollBar().setUnitIncrement(16);
        card.add(plansScroll, BorderLayout.CENTER);

        JPanel scheduleWrapper = new JPanel(new BorderLayout(0, 8));
        scheduleWrapper.setOpaque(false);
        JLabel scheduleTitle = new JLabel("Installment Schedule");
        scheduleTitle.setFont(Theme.BODY_FONT.deriveFont(Font.BOLD, 14f));
        scheduleTitle.setForeground(SLATE);
        scheduleWrapper.add(scheduleTitle, BorderLayout.NORTH);

        ModernTable scheduleTable = new ModernTable(scheduleModel);
        scheduleTable.setShowHorizontalLines(true);
        scheduleTable.setShowVerticalLines(false);
        scheduleTable.setRowHeight(28);
        scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(140);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        right.setFont(MONO);
        scheduleTable.getColumnModel().getColumn(1).setCellRenderer(right);

        JScrollPane scheduleScroll = new JScrollPane(scheduleTable);
        scheduleScroll.setBorder(BorderFactory.createEmptyBorder());
        scheduleWrapper.add(scheduleScroll, BorderLayout.CENTER);

        schedulePanel.add(scheduleWrapper, BorderLayout.CENTER);
        schedulePanel.setVisible(false);
        card.add(schedulePanel, BorderLayout.SOUTH);

        JPanel payBar = new JPanel(new BorderLayout());
        payBar.setOpaque(false);
        payBar.add(payButton, BorderLayout.CENTER);
        payBar.setBorder(new EmptyBorder(0, 0, 0, 0));

        card.add(payBar, BorderLayout.PAGE_END);

        return card;
    }

    private JPanel createPaperCard() {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(PAPER);
        card.setBorder(new CompoundBorder(new FlatDropShadowBorder(),
            new CompoundBorder(new LineBorder(CARD_BORDER, 1, true), new EmptyBorder(18, 18, 18, 18))));
        return card;
    }

    private void loadAssessmentAsync() {
        feeModel.setRowCount(0);
        scheduleModel.setRowCount(0);
        subtotalLabel.setText("Subtotal: --");
        totalUnitsLabel.setText("Total Units: --");
        totalAssessmentLabel.setText("Total Assessment: --");
        refLabel.setText("Ref #: --");
        planCardsPanel.removeAll();
        planGroup.clearSelection();
        payButton.setText("Proceed to Payment");
        distributionBar.setSegments(0.8, 0.15, 0.05);

        SwingWorker<AssessmentData, Void> worker = new SwingWorker<>() {
            @Override
            protected AssessmentData doInBackground() {
                var student = SessionManager.getInstance().getCurrentStudent();
                if (student == null || student.getId() <= 0) {
                    return null;
                }
                Optional<Assessment> pending = assessmentDAO.findPendingForStudent(student.getId());
                if (pending.isEmpty()) {
                    return null;
                }
                Assessment assessment = pending.get();
                List<AssessmentFee> fees = assessmentDAO.findFeesByAssessment(assessment.getId());
                return new AssessmentData(assessment, fees);
            }

            @Override
            protected void done() {
                try {
                    AssessmentData data = get();
                    if (data == null) {
                        feeModel.addRow(new Object[]{"No pending assessment", ""});
                        refreshPlans(BigDecimal.ZERO);
                        return;
                    }
                    currentAssessment = data.assessment();
                    refLabel.setText("Ref #: ENR-" + currentAssessment.getEnrollmentId());
                    populateFees(data.fees());
                    refreshPlans(currentAssessment.getTotalDue());
                } catch (Exception ex) {
                    feeModel.addRow(new Object[]{"Failed to load assessment", ex.getMessage()});
                    refreshPlans(BigDecimal.ZERO);
                }
            }
        };
        worker.execute();
    }

    private void populateFees(List<AssessmentFee> fees) {
        feeModel.setRowCount(0);
        BigDecimal subtotal = BigDecimal.ZERO;
        double units = currentAssessment != null ? currentAssessment.getTotalUnits() : 0.0;
        for (AssessmentFee fee : fees) {
            BigDecimal amount = fee.getAmount() != null ? fee.getAmount() : BigDecimal.ZERO;
            subtotal = subtotal.add(amount);
            feeModel.addRow(new Object[]{fee.getDescription(), formatCurrency(amount.doubleValue())});
        }
        subtotalLabel.setText("Subtotal: " + formatCurrency(subtotal.doubleValue()));
        totalUnitsLabel.setText("Total Units: " + new DecimalFormat("#0.0").format(units));
        totalAssessmentLabel.setText("Total Assessment: " + formatCurrency(subtotal.doubleValue()));
        totalAssessmentLabel.setFont(MONO_BOLD);

        FeeSplit split = computeFeeSplit(fees);
        distributionBar.setSegments(split.tuitionPct(), split.miscPct(), split.labPct());
    }

    private void refreshPlans(BigDecimal baseTotal) {
        double total = baseTotal != null ? baseTotal.doubleValue() : 0.0;
        paymentPlans = buildPaymentPlans(total);
        planCardsPanel.removeAll();
        planGroup.clearSelection();
        scheduleModel.setRowCount(0);
        schedulePanel.setVisible(false);
        selectedPlan = null;

        for (PaymentPlan plan : paymentPlans) {
            planCardsPanel.add(createPlanCard(plan));
            planCardsPanel.add(Box.createVerticalStrut(12));
        }
        if (!paymentPlans.isEmpty()) {
            selectPlan(paymentPlans.get(0));
        }
        planCardsPanel.revalidate();
        planCardsPanel.repaint();
    }

    private JComponent createPlanCard(PaymentPlan plan) {
        PlanCard card = new PlanCard(plan);
        card.addActionListener(e -> selectPlan(plan));
        planGroup.add(card);
        return card;
    }

    private void selectPlan(PaymentPlan plan) {
        this.selectedPlan = plan;
        payButton.setText("Pay " + formatCurrency(plan.dueNow()) + " to Enroll");
        scheduleModel.setRowCount(0);

        if (plan.schedule().isEmpty()) {
            schedulePanel.setVisible(false);
        } else {
            for (PaymentSchedule sched : plan.schedule()) {
                scheduleModel.addRow(new Object[]{sched.label(), formatCurrency(sched.amount())});
            }
            schedulePanel.setVisible(true);
        }

        for (Component c : planCardsPanel.getComponents()) {
            if (c instanceof PlanCard card) {
                boolean sel = card.getPlan().equals(plan);
                card.setSelected(sel);
                card.refresh(sel);
            }
        }
    }

    private List<PaymentPlan> buildPaymentPlans(double baseTotal) {
        List<PaymentPlan> plans = new ArrayList<>();

        // Cash with discount
        double discount = 1500.0;
        double cashTotal = Math.max(0, baseTotal - discount);
        plans.add(new PaymentPlan(
            "Cash / Full Payment",
            "Save ₱1,500",
            SUCCESS,
            "Immediate settlement with discount applied.",
            cashTotal,
            cashTotal,
            List.of()
        ));

        // Installment: low downpayment, rest split into three
        double down = Math.min(5000.0, baseTotal * 0.4);
        double remaining = Math.max(0, baseTotal - down);
        double per = remaining / 3.0;
        LocalDate now = LocalDate.now();
        List<PaymentSchedule> sched = List.of(
            new PaymentSchedule("Downpayment (Due Now)", down),
            new PaymentSchedule("Prelim (Due " + now.plusMonths(1).withDayOfMonth(15).format(DATE_FMT) + ")", per),
            new PaymentSchedule("Midterm (Due " + now.plusMonths(2).withDayOfMonth(15).format(DATE_FMT) + ")", per),
            new PaymentSchedule("Finals (Due " + now.plusMonths(3).withDayOfMonth(15).format(DATE_FMT) + ")", per)
        );

        plans.add(new PaymentPlan(
            "Installment Plan",
            "Low Downpayment",
            WARNING,
            "Spread balance across the term with low upfront cost.",
            baseTotal,
            down,
            sched
        ));

        return plans;
    }

    private void handlePay() {
        if (selectedPlan == null) {
            JOptionPane.showMessageDialog(this, "Please choose a payment plan to continue.",
                "Payment Plan Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
            "Assessment recorded. Proceed to payment to finalize.",
            "Assessment", JOptionPane.INFORMATION_MESSAGE);
        Navigation.to(this, Screen.PAYMENT);
    }

    private String formatCurrency(double value) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return "₱ " + df.format(value);
    }

    private FeeSplit computeFeeSplit(List<AssessmentFee> fees) {
        double tuition = 0;
        double misc = 0;
        double lab = 0;

        for (AssessmentFee fee : fees) {
            double amount = fee.getAmount() != null ? fee.getAmount().doubleValue() : 0.0;
            String desc = fee.getDescription() != null ? fee.getDescription().toLowerCase() : "";
            if (desc.contains("tuition")) {
                tuition += amount;
            } else if (desc.contains("lab")) {
                lab += amount;
            } else if (desc.contains("misc")) {
                misc += amount;
            } else {
                misc += amount;
            }
        }

        double total = tuition + misc + lab;
        if (total <= 0) {
            return new FeeSplit(0.8, 0.15, 0.05);
        }
        return new FeeSplit(tuition / total, misc / total, lab / total);
    }

    @Override
    public void onEnter(NavigationContext context) {
        loadAssessmentAsync();
    }

    @Override
    public void onLeave() {
        // Nothing to cleanup yet.
    }

    private record PaymentSchedule(String label, double amount) {}
    private record PaymentPlan(String name, String badge, Color badgeColor, String highlight, double totalDue, double dueNow, List<PaymentSchedule> schedule) {}
    private record AssessmentData(Assessment assessment, List<AssessmentFee> fees) {}
    private record FeeSplit(double tuitionPct, double miscPct, double labPct) {}

    private class PlanCard extends JToggleButton {
        private final PaymentPlan plan;
        private final JLabel nameLabel;
        private final JLabel badgeLabel;
        private final JLabel descriptionLabel;
        private final JLabel dueLabel;

        PlanCard(PaymentPlan plan) {
            this.plan = plan;
            setLayout(new BorderLayout(12, 10));
            setContentAreaFilled(true);
            setFocusPainted(false);
            setOpaque(true);
            setBorder(new CompoundBorder(new LineBorder(CARD_BORDER, 1, true), new EmptyBorder(14, 14, 14, 14)));

            nameLabel = new JLabel(plan.name());
            nameLabel.setFont(Theme.SUBHEADER_FONT.deriveFont(Font.BOLD, 16f));
            nameLabel.setForeground(new Color(15, 23, 42));

            badgeLabel = new JLabel(plan.badge());
            badgeLabel.setFont(Theme.LABEL_FONT.deriveFont(Font.BOLD));
            badgeLabel.setOpaque(true);
            badgeLabel.setBackground(plan.badgeColor());
            badgeLabel.setForeground(Color.WHITE);
            badgeLabel.setBorder(new EmptyBorder(4, 10, 4, 10));

            descriptionLabel = new JLabel("<html>" + plan.highlight() + "</html>");
            descriptionLabel.setFont(Theme.BODY_FONT);
            descriptionLabel.setForeground(SLATE);

            dueLabel = new JLabel("Due Now: " + formatCurrency(plan.dueNow()));
            dueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            dueLabel.setForeground(PRIMARY);

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(nameLabel, BorderLayout.WEST);
            top.add(badgeLabel, BorderLayout.EAST);

            JPanel center = new JPanel(new BorderLayout());
            center.setOpaque(false);
            center.add(descriptionLabel, BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            bottom.setOpaque(false);
            bottom.add(dueLabel);

            add(top, BorderLayout.NORTH);
            add(center, BorderLayout.CENTER);
            add(bottom, BorderLayout.SOUTH);

            refresh(false);
        }

        PaymentPlan getPlan() {
            return plan;
        }

        void refresh(boolean selected) {
            Color bg = selected ? PRIMARY_DARK : Color.WHITE;
            Color fg = selected ? Color.WHITE : SLATE;
            setBackground(bg);
            nameLabel.setForeground(selected ? Color.WHITE : new Color(15, 23, 42));
            descriptionLabel.setForeground(fg);
            dueLabel.setForeground(selected ? Color.WHITE : PRIMARY);
            badgeLabel.setBackground(selected ? PRIMARY : plan.badgeColor());
            badgeLabel.setForeground(Color.WHITE);
            setBorder(new CompoundBorder(new LineBorder(selected ? PRIMARY : CARD_BORDER, selected ? 2 : 1, true), new EmptyBorder(14, 14, 14, 14)));
        }
    }

    private static class DistributionBar extends JComponent {
        private double tuitionPct = 0.8;
        private double miscPct = 0.15;
        private double labPct = 0.05;

        void setSegments(double tuitionPct, double miscPct, double labPct) {
            double total = tuitionPct + miscPct + labPct;
            if (total <= 0) {
                this.tuitionPct = 0.8;
                this.miscPct = 0.15;
                this.labPct = 0.05;
            } else {
                this.tuitionPct = tuitionPct / total;
                this.miscPct = miscPct / total;
                this.labPct = labPct / total;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int radius = h;

            g2.setColor(new Color(0xE5E7EB));
            g2.fillRoundRect(0, 0, w, h, radius, radius);

            int tuitionW = (int) Math.round(w * tuitionPct);
            int miscW = (int) Math.round(w * miscPct);
            int labW = w - tuitionW - miscW;

            int x = 0;
            g2.setColor(PRIMARY);
            g2.fillRoundRect(x, 0, tuitionW, h, radius, radius);
            x += tuitionW;

            g2.setColor(new Color(0xF97316));
            g2.fillRect(x, 0, miscW, h);
            x += miscW;

            g2.setColor(new Color(0x22C55E));
            g2.fillRoundRect(x, 0, labW, h, radius, radius);

            g2.dispose();
        }
    }
}