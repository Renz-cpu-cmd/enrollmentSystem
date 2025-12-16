package ui.screens;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import service.EnrollmentService;
import service.EnrollmentService.EnrollStudentCommand;
import service.EnrollmentService.EnrollmentResult;
import service.EnrollmentService.ServiceResult;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.components.WizardHeader;
import ui.theme.Theme;
import util.Navigation;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class BioDataScreen extends JPanel implements ScreenView {

    private static final Color UNI_BLUE = new Color(0x0C5CB1);
    private static final Color SLATE = new Color(0x64748B);
    private static final Color GOLD = new Color(0xDAA520);
    private static final Color FIELD_BORDER = new Color(0xCBD5E1);

    private final EnrollmentService enrollmentService;
    private final List<ValidatorEntry> requiredFields = new ArrayList<>();

    private JTextField lastNameField;
    private JTextField firstNameField;
    private JTextField middleNameField;
    private JComboBox<String> extensionCombo;
    private JTextField placeOfBirthField;
    private JTextField citizenshipField;
    private JTextField religionField;
    private JComboBox<String> civilStatusCombo;
    private JComboBox<String> genderCombo;
    private JSpinner birthDateSpinner;

    private JTextField mobileField;
    private JTextField emailField;
    private JTextField facebookField;

    private JTextField streetField;
    private JTextField barangayField;
    private JTextField cityField;
    private JTextField provinceField;

    private JTextField guardianNameField;
    private JTextField guardianRelationshipField;
    private JTextField guardianContactField;
    private JTextField guardianOccupationField;
    private JTextField guardianCompanyField;
    private JTextField guardianOfficePhoneField;

    private JCheckBox guardianEmergencyCheckbox;
    private JPanel emergencyPanel;
    private JTextField emergencyNameField;
    private JTextField emergencyContactField;
    private JTextField emergencyRelationshipField;

    private JLabel toastLabel;

    public BioDataScreen(EnrollmentService enrollmentService) {
        this.enrollmentService = Objects.requireNonNull(enrollmentService, "EnrollmentService is required");
        setLayout(new BorderLayout());
        setBackground(new Color(244, 247, 254));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(new WizardHeader(1), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setMaximumSize(new Dimension(900, Integer.MAX_VALUE));
        content.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(Box.createVerticalStrut(24));
        content.add(centerWrap(createSectionCard("Section A: Personal Information", buildPersonalInformationSection())));
        content.add(Box.createVerticalStrut(16));
        content.add(centerWrap(createSectionCard("Section B: Contact Details", buildContactSection())));
        content.add(Box.createVerticalStrut(16));
        content.add(centerWrap(createSectionCard("Section C: Permanent Address", buildAddressSection())));
        content.add(Box.createVerticalStrut(16));
        content.add(centerWrap(createSectionCard("Section D: Guardian Information", buildGuardianSection())));
        content.add(Box.createVerticalStrut(8));

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        add(scrollPane, BorderLayout.CENTER);

        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JComponent centerWrap(JComponent comp) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
        wrapper.add(Box.createHorizontalGlue());
        wrapper.add(comp);
        wrapper.add(Box.createHorizontalGlue());
        return wrapper;
    }


    private JComponent createSectionCard(String title, JComponent bodyContent) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new FlatDropShadowBorder(),
            new CompoundBorder(
                new MatteBorder(4, 0, 0, 0, GOLD),
                new CompoundBorder(new FlatRoundBorder(), new EmptyBorder(30, 30, 30, 30))
            )
        ));

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        heading.setForeground(UNI_BLUE);
        card.add(heading, BorderLayout.NORTH);
        card.add(bodyContent, BorderLayout.CENTER);

        wrapper.add(card, BorderLayout.CENTER);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        return wrapper;
    }

    private JComponent buildPersonalInformationSection() {
        JPanel body = createSectionBody();

        lastNameField = createTextField(true, "e.g. Dela Cruz");
        firstNameField = createTextField(true, "e.g. Juan");
        middleNameField = createTextField(false, "Optional");
        extensionCombo = createExtensionCombo();
        body.add(createFieldRow(
            createLabeledComponent("Last Name", lastNameField, true),
            createLabeledComponent("First Name", firstNameField, true),
            createLabeledComponent("Middle Name", middleNameField, false),
            createLabeledComponent("Extension", extensionCombo, false)
        ));

        placeOfBirthField = createTextField(true, "City, Country");
        citizenshipField = createTextField(true, "e.g. Filipino");
        body.add(Box.createVerticalStrut(12));
        body.add(createFieldRow(
            createLabeledComponent("Date of Birth", createBirthDatePicker(), true),
            createLabeledComponent("Place of Birth", placeOfBirthField, true),
            createLabeledComponent("Citizenship", citizenshipField, true)
        ));

        religionField = createTextField(true, "e.g. Catholic");
        genderCombo = createComboBox(new String[]{"", "Male", "Female", "Prefer not to say"}, true);
        civilStatusCombo = createComboBox(new String[]{"", "Single", "Married", "Widowed", "Separated"}, true);
        body.add(Box.createVerticalStrut(12));
        body.add(createFieldRow(
            createLabeledComponent("Gender", genderCombo, true),
            createLabeledComponent("Civil Status", civilStatusCombo, true),
            createLabeledComponent("Religion", religionField, true)
        ));

        return body;
    }

    private JComponent buildContactSection() {
        JPanel body = createSectionBody();

        mobileField = createTextField(true, "0912 345 6789");
        emailField = createTextField(true, "name@example.com");
        body.add(createFieldRow(
            createLabeledComponent("Mobile Number", mobileField, true),
            createLabeledComponent("Email Address", emailField, true)
        ));

        facebookField = createTextField(false, "facebook.com/username");
        body.add(Box.createVerticalStrut(12));
        body.add(createFieldRow(
            createLabeledComponent("Facebook Profile", facebookField, false)
        ));

        return body;
    }

    private JComponent buildAddressSection() {
        JPanel body = createSectionBody();

        streetField = createTextField(true, "123 Mabini St.");
        barangayField = createTextField(true, "Barangay 123");
        body.add(createFieldRow(
            createLabeledComponent("Street / House No.", streetField, true),
            createLabeledComponent("Barangay", barangayField, true)
        ));

        cityField = createTextField(true, "Quezon City");
        provinceField = createTextField(true, "Rizal");
        body.add(Box.createVerticalStrut(12));
        body.add(createFieldRow(
            createLabeledComponent("City / Municipality", cityField, true),
            createLabeledComponent("Province", provinceField, true)
        ));

        return body;
    }

    private JComponent buildGuardianSection() {
        JPanel body = createSectionBody();

        guardianNameField = createTextField(true, "Full name");
        guardianRelationshipField = createTextField(true, "Mother");
        guardianContactField = createTextField(true, "0917 123 4567");
        guardianOccupationField = createTextField(true, "Engineer");
        guardianCompanyField = createTextField(true, "ABC Corp");
        guardianOfficePhoneField = createTextField(true, "02 1234 5678");

        body.add(createFieldRow(
            createLabeledComponent("Guardian Name", guardianNameField, true),
            createLabeledComponent("Relationship", guardianRelationshipField, true),
            createLabeledComponent("Contact Number", guardianContactField, true)
        ));

        body.add(Box.createVerticalStrut(12));
        body.add(createFieldRow(
            createLabeledComponent("Occupation", guardianOccupationField, true),
            createLabeledComponent("Company/Employer", guardianCompanyField, true),
            createLabeledComponent("Office Phone", guardianOfficePhoneField, true)
        ));

        body.add(Box.createVerticalStrut(12));
        guardianEmergencyCheckbox = new JCheckBox("Is this the primary contact for emergencies?");
        guardianEmergencyCheckbox.setOpaque(false);
        guardianEmergencyCheckbox.setSelected(true);
        guardianEmergencyCheckbox.setFont(Theme.BODY_FONT);
        guardianEmergencyCheckbox.setForeground(Theme.TEXT_HEADER);
        guardianEmergencyCheckbox.addActionListener(e -> toggleEmergencyPanel());
        body.add(guardianEmergencyCheckbox);

        body.add(Box.createVerticalStrut(12));
        emergencyPanel = buildEmergencyPanel();
        body.add(emergencyPanel);
        toggleEmergencyPanel();

        return body;
    }

    private JPanel buildEmergencyPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(0, 16, 0, 16));

        JLabel label = new JLabel("Emergency Contact (if different)");
        label.setFont(Theme.SUBHEADER_FONT);
        label.setForeground(Theme.TEXT_SECONDARY_COLOR);
        panel.add(label);
        panel.add(Box.createVerticalStrut(12));

        emergencyNameField = createTextField(false, "Contact Person");
        emergencyContactField = createTextField(false, "0917 987 6543");
        emergencyRelationshipField = createTextField(false, "Colleague");

        registerConditionalField(emergencyNameField, this::isEmergencyPanelRequired);
        registerConditionalField(emergencyContactField, this::isEmergencyPanelRequired);
        registerConditionalField(emergencyRelationshipField, this::isEmergencyPanelRequired);

        panel.add(createFieldRow(
            createLabeledComponent("Name", emergencyNameField, true),
            createLabeledComponent("Contact Number", emergencyContactField, true),
            createLabeledComponent("Relationship", emergencyRelationshipField, true)
        ));

        return panel;
    }

    private void toggleEmergencyPanel() {
        boolean show = isEmergencyPanelRequired();
        emergencyPanel.setVisible(show);
        if (!show) {
            setErrorOutline(emergencyNameField, false);
            setErrorOutline(emergencyContactField, false);
            setErrorOutline(emergencyRelationshipField, false);
        }
        revalidate();
        repaint();
    }

    private boolean isEmergencyPanelRequired() {
        return guardianEmergencyCheckbox != null && !guardianEmergencyCheckbox.isSelected();
    }

    private JPanel createSectionBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        return body;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 0, 0, 0));

        toastLabel = new JLabel(" ");
        toastLabel.setFont(Theme.BODY_FONT);
        toastLabel.setForeground(new Color(196, 53, 53));
        footer.add(toastLabel, BorderLayout.WEST);

        JButton nextButton = new JButton("Next: Documents");
        nextButton.setFont(Theme.SUBHEADER_FONT);
        nextButton.setForeground(Color.WHITE);
        nextButton.setBorder(new EmptyBorder(12, 32, 12, 32));
        nextButton.putClientProperty(FlatClientProperties.STYLE,
            "arc:16; background:#0C5CB1; foreground:#FFFFFF;" +
                "hoverBackground:#0f6ed8; pressedBackground:#0a4f8d;" +
                "shadowColor:#0C5CB1; shadowWidth:8; shadowOpacity:30;" +
                "focusWidth:2; innerFocusWidth:1;");
        nextButton.addActionListener(e -> handleEnrollmentSubmission());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(nextButton);
        footer.add(buttonPanel, BorderLayout.EAST);

        return footer;
    }

    private void handleEnrollmentSubmission() {
        if (!validateRequiredFields()) {
            toastLabel.setText("Please fill in all required fields.");
            return;
        }
        toastLabel.setText(" ");

        EnrollStudentCommand command = EnrollStudentCommand.builder()
            .firstName(textOf(firstNameField))
            .lastName(textOf(lastNameField))
            .email(textOf(emailField))
            .mobileNumber(textOf(mobileField))
            .rawPassword(null)
            .build();

        ServiceResult<EnrollmentResult> result = enrollmentService.registerNewStudent(command);

        if (result.isSuccess()) {
            EnrollmentResult data = result.getData();
            if (data != null && data.getStudent() != null) {
                SessionManager.getInstance().setCurrentStudent(data.getStudent());
            }
            StringBuilder message = new StringBuilder("Student registered successfully.");
            if (data != null && data.getStudent() != null && data.getStudent().getStudentId() != null) {
                message.append("\nStudent ID: ").append(data.getStudent().getStudentId());
            }
            if (data != null && data.hasGeneratedPassword()) {
                message.append("\nGenerated password: ").append(data.getGeneratedPassword());
            }
            JOptionPane.showMessageDialog(
                this,
                message.toString(),
                "Enrollment Successful",
                JOptionPane.INFORMATION_MESSAGE
            );
            SessionManager.getInstance().setShsStrand("HUMSS");
            Navigation.to(this, Screen.DOCUMENTS);
        } else {
            JOptionPane.showMessageDialog(
                this,
                result.getMessage(),
                "Enrollment Failed",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private boolean validateRequiredFields() {
        boolean allValid = true;
        for (ValidatorEntry entry : requiredFields) {
            boolean valid = entry.validator().get();
            setErrorOutline(entry.component(), !valid);
            if (!valid) {
                allValid = false;
            }
        }
        return allValid;
    }

    private void setErrorOutline(JComponent component, boolean error) {
        component.putClientProperty("JComponent.outline", error ? "error" : null);
        if (component instanceof JSpinner spinner) {
            JComponent editor = spinner.getEditor();
            if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
                defaultEditor.getTextField().putClientProperty("JComponent.outline", error ? "error" : null);
            }
        }
    }

    private String textOf(JTextComponent component) {
        return component == null ? null : component.getText().trim();
    }

    private JTextField createTextField(boolean required, String placeholder) {
        JTextField field = new JTextField();
        field.setFont(Theme.BODY_FONT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.putClientProperty(FlatClientProperties.STYLE,
            "arc:10; focusWidth:1; borderColor:#CBD5E1; focusColor:#0C5CB1");
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        if (required) {
            registerRequiredField(field);
        }
        return field;
    }

    private JComboBox<String> createComboBox(String[] options, boolean required) {
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setFont(Theme.BODY_FONT);
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboBox.putClientProperty(FlatClientProperties.STYLE,
            "buttonType:roundRect; arc:10; focusWidth:1; borderColor:#CBD5E1; focusColor:#0C5CB1");
        if (required) {
            registerRequiredCombo(comboBox);
        }
        return comboBox;
    }

    private JComboBox<String> createExtensionCombo() {
        String[] options = {"", "Jr.", "Sr.", "II", "III", "IV", "V"};
        JComboBox<String> comboBox = createComboBox(options, false);
        comboBox.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Suffix");
        return comboBox;
    }

    private JComponent createBirthDatePicker() {
        birthDateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, new Date(), Calendar.DAY_OF_MONTH));
        birthDateSpinner.setFont(Theme.BODY_FONT);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(birthDateSpinner, "MMMM d, yyyy");
        editor.getTextField().setEditable(false);
        editor.getTextField().setBorder(new EmptyBorder(0, 8, 0, 8));
        editor.getTextField().putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Select date");
        editor.getTextField().putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON, CalendarHintIcon.get());
        birthDateSpinner.setEditor(editor);
        birthDateSpinner.putClientProperty(FlatClientProperties.STYLE,
            "arc:10; focusWidth:1; borderColor:#CBD5E1; focusColor:#0C5CB1");
        registerRequiredSpinner(birthDateSpinner);
        return birthDateSpinner;
    }

    private JPanel createFieldRow(JComponent... columns) {
        JPanel row = new JPanel(new GridLayout(1, columns.length, 16, 0));
        row.setOpaque(false);
        for (JComponent column : columns) {
            row.add(column);
        }
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        return row;
    }

    private JPanel createLabeledComponent(String labelText, JComponent component, boolean required) {
        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(required ? labelText + " *" : labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(SLATE);

        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(label);
        container.add(Box.createVerticalStrut(4));
        container.add(component);
        return container;
    }

    private void registerRequiredField(JTextComponent component) {
        requiredFields.add(new ValidatorEntry(component, () -> !textOf(component).isEmpty()));
    }

    private void registerRequiredCombo(JComboBox<?> comboBox) {
        requiredFields.add(new ValidatorEntry(comboBox, () -> {
            Object selected = comboBox.getSelectedItem();
            return selected != null && !selected.toString().trim().isEmpty();
        }));
    }

    private void registerRequiredSpinner(JSpinner spinner) {
        requiredFields.add(new ValidatorEntry(spinner, () -> spinner.getValue() != null));
    }

    private void registerConditionalField(JTextComponent component, Supplier<Boolean> condition) {
        requiredFields.add(new ValidatorEntry(component, () -> {
            if (!condition.get()) {
                return true;
            }
            return !textOf(component).isEmpty();
        }));
    }

    private static final class CalendarHintIcon implements Icon {
        private static final int SIZE = 14;
        private static final Icon INSTANCE = new CalendarHintIcon();

        static Icon get() {
            return INSTANCE;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);

            g2.setColor(FIELD_BORDER);
            g2.drawRoundRect(0, 1, SIZE - 1, SIZE - 2, 3, 3);

            g2.setColor(GOLD);
            g2.fillRoundRect(1, 2, SIZE - 3, 4, 2, 2);

            g2.setColor(UNI_BLUE);
            g2.fillRect(3, 6, 1, 1);
            g2.fillRect(6, 6, 1, 1);
            g2.fillRect(9, 6, 1, 1);

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }
    }

    private record ValidatorEntry(JComponent component, Supplier<Boolean> validator) {
    }


    @Override
    public void onEnter(NavigationContext context) {
        toastLabel.setText(" ");
    }

    @Override
    public void onLeave() {
        // Future: persist draft data.
    }
}

