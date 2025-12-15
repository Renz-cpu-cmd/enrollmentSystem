package ui.screens;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.components.ModernTable;
import ui.theme.Theme;
import util.Navigation;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AssessmentScreen extends JPanel implements ScreenView {

    public static Screen returnScreen = Screen.BLOCK_SECTIONING;

    private final DefaultTableModel feeModel;
    private final JLabel subtotalLabel;
    private final JLabel totalDueLabel;
    private final JPanel planContainer;
    private final ButtonGroup planGroup = new ButtonGroup();
    private final List<PaymentPlan> paymentPlans;
    private PaymentPlan selectedPlan;

    public AssessmentScreen() {
        setLayout(new BorderLayout(24, 24));
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        feeModel = new DefaultTableModel(new Object[]{"Description", "Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        subtotalLabel = new JLabel();
        totalDueLabel = new JLabel();
        planContainer = new JPanel();
        planContainer.setOpaque(false);
        planContainer.setLayout(new BoxLayout(planContainer, BoxLayout.Y_AXIS));

        List<FeeLine> feeLines = buildFeeLines();
        double subtotal = feeLines.stream().mapToDouble(FeeLine::amount).sum();
        feeLines.forEach(line -> feeModel.addRow(new Object[]{line.label(), formatCurrency(line.amount())}));
        subtotalLabel.setText("Subtotal: " + formatCurrency(subtotal));

        paymentPlans = buildPaymentPlans(subtotal);

        add(createHeader(), BorderLayout.NORTH);
        add(buildSplitPane(), BorderLayout.CENTER);

        if (!paymentPlans.isEmpty()) {
            selectPlan(paymentPlans.get(0));
        }
    }

    private JComponent createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(12, 92, 177));
        header.setBorder(new CompoundBorder(new FlatDropShadowBorder(), new EmptyBorder(18, 24, 18, 24)));

        JLabel title = new JLabel("Statement of Account");
        title.setFont(Theme.HEADING_FONT.deriveFont(Font.BOLD, 26f));
        title.setForeground(Color.WHITE);

        JLabel reference = new JLabel("Reference #: " + generateReference());
        reference.setFont(Theme.BODY_FONT);
        reference.setForeground(new Color(220, 231, 247));

        header.add(title, BorderLayout.WEST);
        header.add(reference, BorderLayout.EAST);
        return header;
    }

    private JSplitPane buildSplitPane() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildInvoicePanel(),
            buildPaymentPanel());
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(12);
        splitPane.setOpaque(false);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        return splitPane;
    }

    private JComponent buildInvoicePanel() {
        JPanel invoiceCard = createCardPanel();
        invoiceCard.setLayout(new BorderLayout(0, 16));

        JLabel title = new JLabel("Tuition & Fees Breakdown");
        title.setFont(Theme.SUBHEADER_FONT);
        title.setForeground(new Color(31, 41, 55));
        invoiceCard.add(title, BorderLayout.NORTH);

        ModernTable feeTable = new ModernTable(feeModel);
        feeTable.setShowHorizontalLines(true);
        feeTable.setShowVerticalLines(false);
        feeTable.setRowHeight(36);
        feeTable.getColumnModel().getColumn(0).setPreferredWidth(180);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        feeTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(feeTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        invoiceCard.add(scrollPane, BorderLayout.CENTER);

        JLabel subtotalValue = new JLabel(subtotalLabel.getText());
        subtotalValue.setFont(Theme.BOLD_BODY_FONT);
        subtotalValue.setForeground(new Color(55, 65, 81));
        JPanel subtotalRow = new JPanel(new BorderLayout());
        subtotalRow.setOpaque(false);
        subtotalRow.add(Box.createHorizontalGlue(), BorderLayout.CENTER);
        subtotalRow.add(subtotalValue, BorderLayout.EAST);
        invoiceCard.add(subtotalRow, BorderLayout.SOUTH);

        return invoiceCard;
    }

    private JComponent buildPaymentPanel() {
        JPanel paymentCard = createCardPanel();
        paymentCard.setLayout(new BorderLayout(0, 16));

        JLabel title = new JLabel("Select Payment Plan");
        title.setFont(Theme.SUBHEADER_FONT);
        title.setForeground(new Color(31, 41, 55));
        paymentCard.add(title, BorderLayout.NORTH);

        planContainer.removeAll();
        for (int i = 0; i < paymentPlans.size(); i++) {
            planContainer.add(createPlanCard(paymentPlans.get(i)));
            if (i < paymentPlans.size() - 1) {
                planContainer.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane planScroll = new JScrollPane(planContainer);
        planScroll.setBorder(BorderFactory.createEmptyBorder());
        planScroll.getVerticalScrollBar().setUnitIncrement(18);
        paymentCard.add(planScroll, BorderLayout.CENTER);

        paymentCard.add(buildTotalBar(), BorderLayout.SOUTH);
        return paymentCard;
    }

    private JPanel buildTotalBar() {
        JPanel totalBar = new JPanel(new BorderLayout(16, 0));
        totalBar.setOpaque(false);
        totalDueLabel.setFont(Theme.HEADING_FONT.deriveFont(Font.BOLD, 28f));
        totalDueLabel.setForeground(new Color(12, 92, 177));

        JButton finalizeButton = new JButton("Finalize Enrollment");
        finalizeButton.putClientProperty(FlatClientProperties.STYLE,
            "arc:18; background:#0C5CB1; foreground:#FFFFFF; font:+1;" +
                "hoverBackground:#0f6ed8; pressedBackground:#0a4f8d; focusWidth:2; innerFocusWidth:1;");
        finalizeButton.setBorder(new EmptyBorder(14, 32, 14, 32));
        finalizeButton.addActionListener(e -> handleFinalize());

        totalBar.add(totalDueLabel, BorderLayout.CENTER);
        totalBar.add(finalizeButton, BorderLayout.EAST);
        return totalBar;
    }

    private JComponent createPlanCard(PaymentPlan plan) {
        JToggleButton card = new JToggleButton();
        card.setLayout(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(buildPlanBorder(false));
        card.setFocusPainted(false);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.putClientProperty(FlatClientProperties.STYLE, "margin:12,16,12,16; font:+0;");
        card.putClientProperty("plan", plan);

        JLabel title = new JLabel(plan.name());
        title.setFont(Theme.SUBHEADER_FONT);
        title.setForeground(new Color(15, 23, 42));

        JLabel highlight = new JLabel(plan.highlight());
        highlight.setFont(Theme.LABEL_FONT);
        highlight.setForeground(new Color(71, 85, 105));

        JLabel due = new JLabel(formatCurrency(plan.totalDue()));
        due.setFont(Theme.BOLD_BODY_FONT.deriveFont(Font.BOLD, 18f));
        due.setForeground(new Color(12, 92, 177));

        JTextArea details = new JTextArea(plan.details());
        details.setWrapStyleWord(true);
        details.setLineWrap(true);
        details.setEditable(false);
        details.setOpaque(false);
        details.setFont(Theme.BODY_FONT);
        details.setForeground(new Color(100, 116, 139));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(highlight, BorderLayout.EAST);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(details);
        center.add(Box.createVerticalStrut(8));
        center.add(due);

        card.add(header, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);

        card.addActionListener(e -> selectPlan(plan));
        planGroup.add(card);
        return card;
    }

    private Border buildPlanBorder(boolean selected) {
        Color borderColor = selected ? new Color(8, 145, 178) : new Color(226, 232, 240);
        int thickness = selected ? 2 : 1;
        return new CompoundBorder(new LineBorder(borderColor, thickness, true), new EmptyBorder(16, 16, 16, 16));
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(new FlatDropShadowBorder(), new EmptyBorder(24, 24, 24, 24)));
        return card;
    }

    private void selectPlan(PaymentPlan plan) {
        this.selectedPlan = plan;
        totalDueLabel.setText("Total Due Upon Enrollment: " + formatCurrency(plan.totalDue()));

        for (Component component : planContainer.getComponents()) {
            if (component instanceof JToggleButton toggle) {
                PaymentPlan togglePlan = (PaymentPlan) toggle.getClientProperty("plan");
                boolean matches = togglePlan != null && togglePlan.equals(plan);
                toggle.setSelected(matches);
                toggle.setBorder(buildPlanBorder(matches));
            }
        }
    }

    private List<FeeLine> buildFeeLines() {
        List<FeeLine> lines = new ArrayList<>();
        String program = SessionManager.getInstance().getSelectedProgramName();
        double tuition = 21500;
        if (program != null) {
            if (program.toLowerCase().contains("nursing")) {
                tuition = 26500;
            } else if (program.toLowerCase().contains("accountancy")) {
                tuition = 22500;
            }
        }
        lines.add(new FeeLine("Tuition Fee (23 Units)", tuition));
        lines.add(new FeeLine("Miscellaneous Fees", 5200));
        lines.add(new FeeLine("Laboratory Fees", 3100));
        lines.add(new FeeLine("Student ID / Library", 850));
        lines.add(new FeeLine("Student Insurance", 420));
        return lines;
    }

    private List<PaymentPlan> buildPaymentPlans(double baseTotal) {
        List<PaymentPlan> plans = new ArrayList<>();
        plans.add(new PaymentPlan(
            "Cash / Full Payment",
            "Discount 5%",
            "Settle everything today to enjoy an immediate discount.",
            baseTotal * 0.95
        ));
        plans.add(new PaymentPlan(
            "Installment (3 Payments)",
            "Down PHP 5,000",
            "Pay PHP 5,000 now, remaining balance split over the next two months.",
            baseTotal
        ));
        plans.add(new PaymentPlan(
            "Installment (5 Payments)",
            "Low monthly",
            "Spread your balance into five equal payments for better cash flow.",
            baseTotal + 750
        ));
        return plans;
    }

    private void handleFinalize() {
        if (selectedPlan == null) {
            JOptionPane.showMessageDialog(this, "Please choose a payment plan to continue.",
                "Payment Plan Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
            "Enrollment Successful! Welcome to University.",
            "Enrollment Complete",
            JOptionPane.INFORMATION_MESSAGE);
        Navigation.to(this, Screen.DASHBOARD);
    }

    private String formatCurrency(double value) {
        return "PHP " + new DecimalFormat("#,##0.00").format(value);
    }

    private String generateReference() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        int random = 1000 + new Random().nextInt(9000);
        return year + "-" + random;
    }

    @Override
    public void onEnter(NavigationContext context) {
        // Future: hook into backend billing service.
    }

    @Override
    public void onLeave() {
        // Nothing to cleanup yet.
    }

    private record FeeLine(String label, double amount) {
    }

    private record PaymentPlan(String name, String highlight, String details, double totalDue) {
    }
}
