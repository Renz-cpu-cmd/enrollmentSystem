package ui.screens;

import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.theme.Theme;
import util.Navigation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AssessmentScreen extends JPanel implements ScreenView {

    public static Screen returnScreen = Screen.BLOCK_SECTIONING;

    private JComboBox<String> paymentModeCombo;

    public AssessmentScreen() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(40, 40, 40, 40));

        add(buildInvoicePanel(), BorderLayout.CENTER);
        add(buildFooterPanel(), BorderLayout.SOUTH);
    }

    private JComponent buildInvoicePanel() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
            new EmptyBorder(32, 32, 32, 32)));

        JLabel header = new JLabel("Assessment of Fees");
        header.setFont(Theme.HEADING_FONT.deriveFont(Font.BOLD, 26f));
        header.setForeground(Theme.TEXT_HEADER);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(header);
        card.add(Box.createVerticalStrut(24));

        String[][] charges = new String[][]{
            {"Tuition Fee (21 Units)", "P 15,000.00"},
            {"Miscellaneous Fees", "P 5,000.00"},
            {"Laboratory Fees", "P 3,000.00"}
        };

        for (String[] charge : charges) {
            card.add(createLineItem(charge[0], charge[1]));
            card.add(Box.createVerticalStrut(10));
        }

        card.add(Box.createVerticalStrut(12));
        card.add(createDivider());
        card.add(Box.createVerticalStrut(12));

        JLabel totalLabel = new JLabel("TOTAL");
        totalLabel.setFont(Theme.SUBHEADER_FONT);
        totalLabel.setForeground(Theme.TEXT_HEADER);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(totalLabel);

        JLabel totalValue = new JLabel("P 23,000.00");
        totalValue.setFont(Theme.HEADER_FONT.deriveFont(Font.BOLD, 30f));
        totalValue.setForeground(Theme.PRIMARY);
        totalValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(totalValue);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        center.add(card, gbc);
        return center;
    }

    private JPanel createLineItem(String label, String amount) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(Theme.BODY_FONT);
        labelComponent.setForeground(Theme.TEXT_HEADER);

        JLabel amountComponent = new JLabel(amount);
        amountComponent.setFont(Theme.BOLD_BODY_FONT);
        amountComponent.setForeground(Theme.TEXT_HEADER);

        row.add(labelComponent, BorderLayout.WEST);
        row.add(amountComponent, BorderLayout.EAST);
        return row;
    }

    private JComponent createDivider() {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Theme.BORDER_COLOR);
        separator.setBackground(Theme.BORDER_COLOR);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return separator;
    }

    private JPanel buildFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(24, 0, 0, 0));

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));

        JPanel paymentGroup = new JPanel();
        paymentGroup.setOpaque(false);
        paymentGroup.setLayout(new BoxLayout(paymentGroup, BoxLayout.Y_AXIS));

        JLabel paymentLabel = new JLabel("Payment Mode");
        paymentLabel.setFont(Theme.LABEL_FONT);
        paymentLabel.setForeground(Theme.TEXT_BODY);
        paymentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        paymentModeCombo = new JComboBox<>(new String[]{"Full Payment", "Installment"});
        paymentModeCombo.setFont(Theme.BODY_FONT);
        paymentModeCombo.setMaximumSize(new Dimension(220, Theme.INPUT_HEIGHT));
        paymentModeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        paymentGroup.add(paymentLabel);
        paymentGroup.add(Box.createVerticalStrut(6));
        paymentGroup.add(paymentModeCombo);

        controls.add(paymentGroup);
        controls.add(Box.createHorizontalGlue());

        JButton backButton = new JButton("Back");
        styleSecondaryButton(backButton);
        backButton.addActionListener(e -> Navigation.to(this, returnScreen));

        JButton finalizeButton = new JButton("Finalize Enrollment");
        finalizeButton.setFont(Theme.SUBHEADER_FONT);
        finalizeButton.setForeground(Color.WHITE);
        finalizeButton.setBackground(Theme.SUCCESS_COLOR);
        finalizeButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        finalizeButton.setFocusPainted(false);
        finalizeButton.addActionListener(e -> handleFinalize());

        controls.add(backButton);
        controls.add(Box.createHorizontalStrut(12));
        controls.add(finalizeButton);

        footer.add(controls, BorderLayout.CENTER);
        return footer;
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(Theme.BODY_FONT);
        button.setForeground(Theme.TEXT_HEADER);
        button.setBackground(Theme.CARD_BG);
        button.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setPreferredSize(new Dimension(90, 44));
    }

    private void handleFinalize() {
        String mode = paymentModeCombo != null && paymentModeCombo.getSelectedItem() != null
            ? paymentModeCombo.getSelectedItem().toString()
            : "Full Payment";
        JOptionPane.showMessageDialog(this,
            "Enrollment finalized under " + mode + ".\nDigital COR is now available.",
            "Enrollment Complete",
            JOptionPane.INFORMATION_MESSAGE);
        Navigation.to(this, Screen.DIGITAL_COR);
    }

    @Override
    public void onEnter(NavigationContext context) {
        // No dynamic state to initialize yet.
    }

    @Override
    public void onLeave() {
        // Nothing to cleanup.
    }
}
