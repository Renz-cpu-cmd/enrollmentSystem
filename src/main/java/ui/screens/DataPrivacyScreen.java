package ui.screens;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import util.Navigation;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DataPrivacyScreen extends JPanel implements ScreenView {

    private final JCheckBox consentCheckBox;
    private final JButton continueButton;

    public DataPrivacyScreen() {
        setLayout(new GridBagLayout());
        setBackground(new Color(244, 247, 254));

        consentCheckBox = createConsentCheck();
        continueButton = createContinueButton();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buildLegalCard(), gbc);
    }

    private JComponent buildLegalCard() {
        JPanel card = new JPanel(new BorderLayout(0, 24));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new FlatDropShadowBorder(),
            new CompoundBorder(new FlatRoundBorder(), new EmptyBorder(28, 36, 28, 36))
        ));
        card.setPreferredSize(new Dimension(720, 520));

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildBody(), BorderLayout.CENTER);
        card.add(buildFooter(), BorderLayout.SOUTH);
        return card;
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Data Privacy Statement");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.putClientProperty(FlatClientProperties.STYLE, "font:+4; font:bold; foreground:#0F172A;");

        JLabel subtitle = new JLabel("Republic Act No. 10173 — Data Privacy Act of 2012");
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.putClientProperty(FlatClientProperties.STYLE, "foreground:#6E7582; font:+0;");

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        return header;
    }

    private JComponent buildBody() {
        JEditorPane legalPane = new JEditorPane();
        legalPane.setContentType("text/html");
        legalPane.setEditable(false);
        legalPane.setText(buildLegalHtml());
        legalPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        legalPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JScrollPane scrollPane = new JScrollPane(legalPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 234, 242), 1, true));
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.setPreferredSize(new Dimension(0, 360));
        return scrollPane;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));

        consentCheckBox.addActionListener(e -> continueButton.setEnabled(consentCheckBox.isSelected()));
        footer.add(consentCheckBox);
        footer.add(Box.createVerticalStrut(16));
        footer.add(buildActions());
        return footer;
    }

    private JPanel buildActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton declineButton = new JButton("Decline");
        declineButton.putClientProperty(FlatClientProperties.STYLE,
            "borderWidth:0; foreground:#6E7582; background:null;" +
                "hoverForeground:#0C5CB1;" +
                "underline:true;");
        declineButton.addActionListener(e -> {
            consentCheckBox.setSelected(false);
            continueButton.setEnabled(false);
        });

        actions.add(declineButton);
        actions.add(continueButton);
        return actions;
    }

    private JCheckBox createConsentCheck() {
        JCheckBox box = new JCheckBox("I have read and understand the Data Privacy Statement.");
        box.setOpaque(false);
        box.putClientProperty(FlatClientProperties.STYLE, "font:+0; foreground:#0F172A;");
        return box;
    }

    private JButton createContinueButton() {
        JButton button = new JButton("Continue");
        button.setEnabled(false);
        button.putClientProperty(FlatClientProperties.STYLE,
            "arc:16; background:#0C5CB1; foreground:#FFFFFF;" +
                "hoverBackground:#0f6ed8; pressedBackground:#0a4f8d;" +
                "focusWidth:2; innerFocusWidth:1; font:+0;");
        button.addActionListener(e -> Navigation.to(this, Screen.BIO_DATA));
        return button;
    }

    private String buildLegalHtml() {
        return "<html><body style='font-family: " + UIManager.getFont("Label.font").getFamily() +
            ", sans-serif; font-size:12px; color:#333333; line-height:1.6;'>" +
            "<p>To comply with Republic Act No. 10173 (Data Privacy Act of 2012) and " +
            "Commission on Higher Education (CHED) / Department of Education (DepEd) " +
            "guidelines, the University of Nueva Esperanza (the \"University\") provides " +
            "this Privacy Statement describing how your personal information will be " +
            "collected, used, retained, and shared as part of the enrollment process." +
            "</p>" +
            "<b>1. Data Collection</b>" +
            "<ul>" +
            "<li>Personal identifiers: full name, student ID, date of birth, citizenship, and government-issued numbers.</li>" +
            "<li>Contact information: mobile number, email address, and residential address.</li>" +
            "<li>Academic records: Form 137/138, previous school information, scholastic standing, and conduct reports.</li>" +
            "<li>Family background: parent/guardian details, emergency contacts, and financial support information.</li>" +
            "</ul>" +
            "<b>2. Purpose of Use</b>" +
            "<ul>" +
            "<li>Evaluate admission eligibility and process new or returning student enrollment.</li>" +
            "<li>Compile official class lists, curricula, and grading sheets in compliance with CHED and DepEd requirements.</li>" +
            "<li>Generate official receipts, assessment of fees, and statements of account.</li>" +
            "<li>Maintain alumni, registrar, and guidance records for long-term academic tracking.</li>" +
            "</ul>" +
            "<b>3. Data Retention</b>" +
            "<p>Your personal data will be stored in secure systems for as long as you are an active student and for at " +
            "least five (5) years thereafter, or longer when required to fulfill statutory obligations (e.g., " +
            "CHED/DepEd reporting, alumni verification). When data is no longer necessary, it will be anonymized or " +
            "securely destroyed.</p>" +
            "<b>4. Third-Party Sharing</b>" +
            "<ul>" +
            "<li>Department of Education (DepEd), Commission on Higher Education (CHED), and other regulatory bodies " +
            "for mandated submissions.</li>" +
            "<li>Accreditation and government scholarship agencies to validate enrollment and academic standing.</li>" +
            "<li>Authorized service providers (e.g., IT platforms, payment gateways) bound by confidentiality agreements.</li>" +
            "</ul>" +
            "<p>The University implements administrative, physical, and technical safeguards (including encrypted " +
            "databases, access logs, and role-based access controls) to protect your personal information. You have the " +
            "right to request access, correction, or withdrawal of consent subject to University policies and applicable " +
            "laws.</p>" +
            "<p>By selecting \"Continue\" you acknowledge that you have read and understood this Data Privacy Statement." +
            "</p>" +
            "</body></html>";
    }

    @Override
    public void onEnter(NavigationContext context) {
        consentCheckBox.setSelected(false);
        continueButton.setEnabled(false);
    }

    @Override
    public void onLeave() {
        // No cleanup required.
    }
}