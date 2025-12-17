package ui.screens;

/**
 * Payment entry and review screen.
 *
 * <p>Extends `JPanel` and implements `ScreenView` so it can be displayed by
 * the app shell consistently with other screens.</p>
 */

import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import dao.AssessmentDAO;
import model.Assessment;
import service.PaymentService;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.theme.Theme;
import util.Navigation;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

public class PaymentScreen extends JPanel implements ScreenView {

    private static final Color BLUE_ACTIVE = new Color(0x1E40AF);
    private static final Color GREEN_ACTIVE = new Color(0x10B981);
    private static final Color CASH_ACTIVE = new Color(0xCBD5E1);
    private static final Color SLATE = new Color(0x334155);
    private static final Color PAPER = Color.WHITE;
    private static final Font MONO_LG = new Font("Consolas", Font.BOLD, 24);
    private static final Font MONO_MD = new Font("Consolas", Font.PLAIN, 16);

    private final PaymentService paymentService;
    private final AssessmentDAO assessmentDAO;

    private PaymentMode paymentMode = PaymentMode.CARD;
    private Assessment currentAssessment;
    private BigDecimal outstanding = BigDecimal.ZERO;

    private JLabel outstandingLabel;
    private JLabel statusBadge;
    private JLabel studentLabel;
    private JLabel idLabel;
    private JLabel refLabel;
    private JLabel totalDueLabel;
    private JLabel amountSummaryLabel;
    private JTextField amountField;
    private JToggleButton cardTab;
    private JToggleButton walletTab;
    private JToggleButton cashTab;
    private JPanel methodCards;
    private CardLayout methodLayout;
    private JButton payButton;

    private JTextField cardNumberField;
    private JTextField cardNameField;
    private JTextField cardExpiryField;
    private JPasswordField cardCvvField;
    private CardPreview cardPreview;

    private JLabel walletStatusLabel;
    private JLabel cashRefLabel;

    public PaymentScreen(PaymentService paymentService, AssessmentDAO assessmentDAO) {
        this.paymentService = paymentService;
        this.assessmentDAO = assessmentDAO;
        buildUI();
        loadAssessmentAsync();
    }

    private void buildUI() {
        setLayout(new BorderLayout(16, 16));
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildLeftPanel(), buildSummaryPanel());
        split.setResizeWeight(0.55);
        split.setDividerSize(12);
        split.setBorder(BorderFactory.createEmptyBorder());
        add(split, BorderLayout.CENTER);
    }

    private JComponent buildLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setOpaque(false);

        JPanel tabs = new JPanel(new GridLayout(1, 3, 8, 0));
        tabs.setOpaque(false);
        cardTab = createTabButton("💳 Card", BLUE_ACTIVE, PaymentMode.CARD);
        walletTab = createTabButton("📱 E-Wallet", GREEN_ACTIVE, PaymentMode.WALLET);
        cashTab = createTabButton("💵 Cash/OTC", CASH_ACTIVE, PaymentMode.CASH);
        tabs.add(cardTab);
        tabs.add(walletTab);
        tabs.add(cashTab);

        methodLayout = new CardLayout();
        methodCards = new JPanel(methodLayout);
        methodCards.setOpaque(false);
        methodCards.add(buildCardForm(), PaymentMode.CARD.name());
        methodCards.add(buildWalletForm(), PaymentMode.WALLET.name());
        methodCards.add(buildCashForm(), PaymentMode.CASH.name());

        panel.add(tabs, BorderLayout.NORTH);
        panel.add(methodCards, BorderLayout.CENTER);
        panel.add(buildAmountRow(), BorderLayout.SOUTH);
        selectMode(PaymentMode.CARD);
        return panel;
    }

    private JToggleButton createTabButton(String text, Color activeColor, PaymentMode mode) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setBorder(new CompoundBorder(new FlatDropShadowBorder(), new FlatRoundBorder()));
        btn.setBackground(new Color(0xF8FAFC));
        btn.setForeground(SLATE);
        btn.setFont(Theme.BOLD_BODY_FONT);
        btn.addActionListener(e -> selectMode(mode));
        btn.putClientProperty("activeColor", activeColor);
        return btn;
    }

    private JPanel buildCardForm() {
        JPanel card = createPaperCard();
        card.setLayout(new BorderLayout(12, 12));

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setOpaque(false);
        cardNumberField = createField("Card Number", MONO_MD);
        cardNumberField.setDocument(new LimitedDocument(19));
        cardNumberField.getDocument().addDocumentListener(previewUpdater());
        cardNumberField.setToolTipText("#### #### #### ####");
        cardNumberField.setFont(new Font("Consolas", Font.PLAIN, 18));

        cardNameField = createField("Name on Card", Theme.BOLD_BODY_FONT);
        cardNameField.getDocument().addDocumentListener(previewUpdater());

        cardExpiryField = createField("MM/YY", MONO_MD);
        cardExpiryField.setDocument(new LimitedDocument(5));
        cardExpiryField.getDocument().addDocumentListener(previewUpdater());

        cardCvvField = new JPasswordField();
        styleInput(cardCvvField);
        cardCvvField.setDocument(new LimitedDocument(4));

        form.add(labeled("Card Number", cardNumberField));
        form.add(labeled("Name on Card", cardNameField));
        form.add(labeled("Expiry", cardExpiryField));
        form.add(labeled("CVV", cardCvvField));

        cardPreview = new CardPreview();
        cardPreview.setPreferredSize(new Dimension(320, 180));

        card.add(form, BorderLayout.NORTH);
        card.add(cardPreview, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildWalletForm() {
        JPanel panel = createPaperCard();
        panel.setLayout(new BorderLayout(12, 12));

        JPanel logos = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        logos.setOpaque(false);
        logos.add(coloredBadge("GCash", GREEN_ACTIVE));
        logos.add(coloredBadge("PayMaya", new Color(0x0EA5E9)));

        JPanel qrBox = new JPanel(new BorderLayout());
        qrBox.setOpaque(false);
        qrBox.setBorder(new EmptyBorder(12, 0, 0, 0));
        qrBox.add(new QRPlaceholder(), BorderLayout.CENTER);

        walletStatusLabel = new JLabel("Scan the QR with your wallet to continue.");
        walletStatusLabel.setFont(Theme.BODY_FONT);
        walletStatusLabel.setForeground(SLATE);
        qrBox.add(walletStatusLabel, BorderLayout.SOUTH);

        panel.add(logos, BorderLayout.NORTH);
        panel.add(qrBox, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCashForm() {
        JPanel panel = createPaperCard();
        panel.setLayout(new BorderLayout(10, 8));
        cashRefLabel = new JLabel("Ref #: --");
        cashRefLabel.setFont(Theme.SUBHEADER_FONT.deriveFont(Font.BOLD, 18f));
        cashRefLabel.setForeground(SLATE);

        JLabel instructions = new JLabel("Present this reference to the cashier. Payment posts instantly.");
        instructions.setFont(Theme.BODY_FONT);
        instructions.setForeground(SLATE);

        panel.add(cashRefLabel, BorderLayout.NORTH);
        panel.add(instructions, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAmountRow() {
        JPanel row = createPaperCard();
        row.setLayout(new BorderLayout(8, 8));
        JLabel label = new JLabel("Amount to Pay (partial allowed)");
        label.setFont(Theme.BOLD_BODY_FONT);
        label.setForeground(SLATE);
        amountField = new JTextField();
        amountField.setFont(new Font("Consolas", Font.PLAIN, 24));
        styleInput(amountField);
        amountField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { syncAmount(); }
            @Override public void removeUpdate(DocumentEvent e) { syncAmount(); }
            @Override public void changedUpdate(DocumentEvent e) { syncAmount(); }
        });
        row.add(label, BorderLayout.NORTH);
        row.add(amountField, BorderLayout.CENTER);
        return row;
    }

    private JComponent buildSummaryPanel() {
        JPanel card = new JPanel(new BorderLayout(12, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PAPER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new CompoundBorder(new FlatDropShadowBorder(), new FlatRoundBorder()));
        card.setBackground(PAPER);
        card.setPreferredSize(new Dimension(320, 400));

        JLabel header = new JLabel("Receipt Summary");
        header.setFont(Theme.SUBHEADER_FONT.deriveFont(Font.BOLD, 20f));
        header.setForeground(SLATE);

        outstandingLabel = new JLabel("₱ --");
        outstandingLabel.setFont(new Font("Consolas", Font.BOLD, 32));
        outstandingLabel.setForeground(new Color(0xDAA520));

        statusBadge = new JLabel("Status: --");
        statusBadge.setOpaque(true);
        statusBadge.setBackground(new Color(0x1E3A8A));
        statusBadge.setForeground(Color.WHITE);
        statusBadge.setBorder(new EmptyBorder(6, 10, 6, 10));
        statusBadge.setFont(Theme.LABEL_FONT);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new GridLayout(0, 1, 0, 8));
        studentLabel = infoLabel("Student", "--");
        idLabel = infoLabel("Student ID", "--");
        refLabel = infoLabel("Assessment Ref", "--");
        totalDueLabel = infoLabel("Total Due", "₱ --");
        amountSummaryLabel = infoLabel("Amount to Pay", "₱ --");

        JPanel headerBox = new JPanel(new BorderLayout());
        headerBox.setOpaque(false);
        headerBox.add(header, BorderLayout.WEST);

        JPanel balanceBox = new JPanel(new BorderLayout(4, 4));
        balanceBox.setOpaque(false);
        balanceBox.add(outstandingLabel, BorderLayout.NORTH);
        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusRow.setOpaque(false);
        statusRow.add(statusBadge);
        balanceBox.add(statusRow, BorderLayout.SOUTH);

        headerBox.add(balanceBox, BorderLayout.SOUTH);

        payButton = new JButton("🔒 Secure Pay");
        payButton.setBackground(GREEN_ACTIVE);
        payButton.setForeground(Color.WHITE);
        payButton.setFont(Theme.BOLD_BODY_FONT.deriveFont(Font.BOLD, 18f));
        payButton.setFocusPainted(false);
        payButton.setBorder(new CompoundBorder(new FlatRoundBorder(), new EmptyBorder(12, 16, 12, 16)));
        payButton.addActionListener(e -> handlePay());

        card.add(headerBox, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);

        JPanel payWrapper = new JPanel(new BorderLayout());
        payWrapper.setOpaque(false);
        payWrapper.add(payButton, BorderLayout.SOUTH);
        card.add(payWrapper, BorderLayout.SOUTH);
        return card;
    }

    private JLabel infoLabel(String title, String value) {
        JLabel label = new JLabel(title + ": " + value);
        label.setFont(Theme.BODY_FONT);
        label.setForeground(SLATE);
        return label;
    }

    private JLabel coloredBadge(String text, Color bg) {
        JLabel badge = new JLabel(text);
        badge.setOpaque(true);
        badge.setBackground(bg);
        badge.setForeground(Color.WHITE);
        badge.setBorder(new EmptyBorder(6, 10, 6, 10));
        badge.setFont(Theme.BOLD_BODY_FONT);
        return badge;
    }

    private JPanel labeled(String title, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(Theme.BOLD_BODY_FONT);
        label.setForeground(SLATE);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JTextField createField(String placeholder, Font font) {
        JTextField field = new JTextField();
        styleInput(field);
        field.setFont(font);
        field.setToolTipText(placeholder);
        return field;
    }

    private void styleInput(JComponent comp) {
        comp.setBorder(new CompoundBorder(new FlatRoundBorder(), new EmptyBorder(10, 12, 10, 12)));
        comp.setBackground(new Color(0xF8FAFC));
    }

    private JPanel createPaperCard() {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(PAPER);
        card.setBorder(new CompoundBorder(new FlatDropShadowBorder(), new CompoundBorder(new FlatRoundBorder(), new EmptyBorder(14, 14, 14, 14))));
        return card;
    }

    private void selectMode(PaymentMode mode) {
        this.paymentMode = mode;
        methodLayout.show(methodCards, mode.name());
        highlightTab(cardTab, mode == PaymentMode.CARD);
        highlightTab(walletTab, mode == PaymentMode.WALLET);
        highlightTab(cashTab, mode == PaymentMode.CASH);
        if (mode == PaymentMode.CASH) {
            cashRefLabel.setText("Ref #: " + generateRef());
        }
    }

    private void highlightTab(JToggleButton btn, boolean active) {
        Color activeColor = (Color) btn.getClientProperty("activeColor");
        btn.setSelected(active);
        btn.setBackground(active ? activeColor : new Color(0xF8FAFC));
        btn.setForeground(active ? Color.WHITE : SLATE);
        btn.setBorder(new CompoundBorder(new FlatDropShadowBorder(), new FlatRoundBorder()));
    }

    private void loadAssessmentAsync() {
        setEnabledRecursive(false);
        SwingWorker<Assessment, Void> worker = new SwingWorker<>() {
            @Override
            protected Assessment doInBackground() {
                var student = SessionManager.getInstance().getCurrentStudent();
                if (student == null) {
                    return null;
                }
                return assessmentDAO.findPendingForStudent(student.getId()).orElse(null);
            }

            @Override
            protected void done() {
                try {
                    currentAssessment = get();
                    if (currentAssessment == null) {
                        outstanding = BigDecimal.ZERO;
                        updateSummary("--", "--", "₱ --", "₱ --");
                        statusBadge.setText("Status: NONE");
                        return;
                    }
                    var student = SessionManager.getInstance().getCurrentStudent();
                    String name = student != null ? student.getFirstName() + " " + student.getLastName() : "--";
                    String sid = student != null ? student.getStudentId() : "--";

                    BigDecimal totalDue = currentAssessment.getTotalDue() != null ? currentAssessment.getTotalDue() : BigDecimal.ZERO;
                    BigDecimal totalPaid = paymentService.getTotalPaid(currentAssessment.getId());
                    outstanding = totalDue.subtract(totalPaid);
                    if (outstanding.compareTo(BigDecimal.ZERO) < 0) {
                        outstanding = BigDecimal.ZERO;
                    }

                    outstandingLabel.setText(formatCurrency(outstanding));
                    statusBadge.setText("Status: " + currentAssessment.getStatus());
                    amountField.setText(outstanding.toPlainString());
                    updateSummary(name, sid, formatCurrency(totalDue), formatCurrency(outstanding));
                    setEnabledRecursive(true);
                } catch (Exception ex) {
                    statusBadge.setText("Status: ERROR");
                    setEnabledRecursive(true);
                }
            }
        };
        worker.execute();
    }

    private void updateSummary(String student, String studentId, String totalDue, String toPay) {
        studentLabel.setText("Student: " + student);
        idLabel.setText("Student ID: " + studentId);
        String ref = currentAssessment != null ? "ENR-" + currentAssessment.getEnrollmentId() : "--";
        refLabel.setText("Assessment Ref: " + ref);
        totalDueLabel.setText("Total Due: " + totalDue);
        amountSummaryLabel.setText("Amount to Pay: " + toPay);
    }

    private void syncAmount() {
        amountSummaryLabel.setText("Amount to Pay: ₱ " + safeAmountText());
    }

    private String safeAmountText() {
        try {
            double val = Double.parseDouble(amountField.getText());
            return new DecimalFormat("#,##0.00").format(val);
        } catch (NumberFormatException ex) {
            return "0.00";
        }
    }

    private void setEnabledRecursive(boolean enabled) {
        setEnabled(enabled);
        payButton.setEnabled(enabled);
        amountField.setEnabled(enabled);
        cardTab.setEnabled(enabled);
        walletTab.setEnabled(enabled);
        cashTab.setEnabled(enabled);
    }

    private void handlePay() {
        if (currentAssessment == null) {
            JOptionPane.showMessageDialog(this, "No assessment to pay.", "Payment", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid amount.", "Payment", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (amount <= 0) {
            JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Payment", JOptionPane.WARNING_MESSAGE);
            return;
        }

        switch (paymentMode) {
            case CARD -> {
                if (!validateCard()) return;
                simulate("Processing card...", () -> processPayment(amount, "CARD"));
            }
            case WALLET -> simulate("Waiting for confirmation...", () -> processPayment(amount, "ONLINE"));
            case CASH -> processPayment(amount, "CASH");
        }
    }

    private boolean validateCard() {
        if (isEmpty(cardNumberField) || isEmpty(cardNameField) || isEmpty(cardExpiryField) || cardCvvField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Fill out all card details.", "Card Payment", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean isEmpty(JTextField field) {
        return field.getText() == null || field.getText().isBlank();
    }

    private void simulate(String message, Runnable after) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Payment", Dialog.ModalityType.MODELESS);
        dialog.setUndecorated(true);
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new CompoundBorder(new FlatRoundBorder(), new EmptyBorder(12, 16, 12, 16)));
        panel.add(new JLabel(message), BorderLayout.CENTER);
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        panel.add(bar, BorderLayout.SOUTH);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        Timer timer = new Timer(2000, e -> {
            dialog.dispose();
            after.run();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void processPayment(double amount, String method) {
        PaymentService.PaymentResult result = paymentService.processPayment(currentAssessment.getId(), amount, method);
        if (!result.success()) {
            JOptionPane.showMessageDialog(this, result.message(), "Payment", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Payment recorded (" + result.assessmentStatus() + ")", "Payment", JOptionPane.INFORMATION_MESSAGE);
        Navigation.to(this, Screen.COR);
    }

    private String formatCurrency(BigDecimal value) {
        return "₱ " + new DecimalFormat("#,##0.00").format(value);
    }

    private DocumentListener previewUpdater() {
        return new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { cardPreview.repaint(); }
            @Override public void removeUpdate(DocumentEvent e) { cardPreview.repaint(); }
            @Override public void changedUpdate(DocumentEvent e) { cardPreview.repaint(); }
        };
    }

    private String generateRef() {
        return "OTC-" + (100000 + new Random().nextInt(900000));
    }

    private class CardPreview extends JPanel {
        CardPreview() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, new Color(0x111827), getWidth(), getHeight(), new Color(0x1F2937));
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

            g2.setColor(Color.WHITE);
            g2.setFont(Theme.SUBHEADER_FONT);
            g2.drawString("Premium Card", 18, 32);

            g2.setFont(MONO_LG);
            String num = cardNumberField.getText().isBlank() ? "#### #### #### ####" : cardNumberField.getText();
            g2.drawString(num, 18, 80);

            g2.setFont(Theme.BODY_FONT);
            String name = cardNameField.getText().isBlank() ? "CARDHOLDER" : cardNameField.getText();
            g2.drawString(name.toUpperCase(), 18, 120);

            String exp = cardExpiryField.getText().isBlank() ? "MM/YY" : cardExpiryField.getText();
            g2.drawString("EXP " + exp, 18, 150);
            g2.dispose();
        }
    }

    private static class QRPlaceholder extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight()) - 20;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x, y, size, size, 12, 12);
            g2.setColor(new Color(0xE2E8F0));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x, y, size, size, 12, 12);
            g2.setColor(new Color(0x0F172A));
            for (int i = 0; i < 4; i++) {
                int bx = x + 12 + i * (size / 4);
                int by = y + 12 + (i % 2) * (size / 5);
                g2.fillRect(bx, by, size / 10, size / 10);
            }
            g2.dispose();
        }
    }

    private static class LimitedDocument extends javax.swing.text.PlainDocument {
        private final int max;
        LimitedDocument(int max) { this.max = max; }
        @Override
        public void insertString(int offs, String str, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
            if (str == null) return;
            if ((getLength() + str.length()) <= max) {
                super.insertString(offs, str, a);
            }
        }
    }

    private enum PaymentMode { CARD, WALLET, CASH }

    @Override
    public void onEnter(NavigationContext context) {
        loadAssessmentAsync();
    }

    @Override
    public void onLeave() {
        // no-op
    }
}