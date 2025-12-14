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
import ui.theme.Theme;
import util.Navigation;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class BioDataScreen extends JPanel implements ScreenView {

    private final EnrollmentService enrollmentService;
    private final List<ValidatorEntry> requiredFields = new ArrayList<>();

    private JTextField lastNameField;
    private JTextField firstNameField;
    private JTextField middleNameField;
    private JTextField extensionField;
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

        add(createHeroHeader(), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(Box.createVerticalStrut(24));
        content.add(createSectionCard("Section A: Personal Information", buildPersonalInformationSection()));
        content.add(Box.createVerticalStrut(16));
        content.add(createSectionCard("Section B: Contact Details", buildContactSection()));
        content.add(Box.createVerticalStrut(16));
        content.add(createSectionCard("Section C: Permanent Address", buildAddressSection()));
        content.add(Box.createVerticalStrut(16));
        content.add(createSectionCard("Section D: Guardian Information", buildGuardianSection()));
        content.add(Box.createVerticalStrut(8));

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        add(scrollPane, BorderLayout.CENTER);

        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JComponent createHeroHeader() {
        GradientHeaderPanel header = new GradientHeaderPanel();
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel textRow = new JPanel(new BorderLayout());
        textRow.setOpaque(false);

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("New Student Enrollment");
        title.putClientProperty(FlatClientProperties.STYLE, "font:+6; font:bold; foreground:#FFFFFF;");
        JLabel subtitle = new JLabel("Academic Year 2025-2026");
        subtitle.putClientProperty(FlatClientProperties.STYLE, "font:+1; foreground:rgba(255,255,255,0.85);");
        textBlock.add(title);
        textBlock.add(Box.createVerticalStrut(4));
        textBlock.add(subtitle);

        JLabel watermark = new JLabel("UNE");
        watermark.putClientProperty(FlatClientProperties.STYLE,
            "font:+12; font:bold; foreground:rgba(255,255,255,0.25);");

        textRow.add(textBlock, BorderLayout.WEST);
        textRow.add(watermark, BorderLayout.EAST);

        JPanel stepperWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        stepperWrapper.setOpaque(false);
        stepperWrapper.setBorder(new EmptyBorder(24, 0, 0, 0));
        stepperWrapper.add(buildStepper());

        header.add(textRow, BorderLayout.CENTER);
        header.add(stepperWrapper, BorderLayout.SOUTH);
        return header;
    }

    private JComponent buildStepper() {
        JPanel stepper = new JPanel();
        stepper.setOpaque(false);
        stepper.setLayout(new BoxLayout(stepper, BoxLayout.X_AXIS));

        stepper.add(createStepPill(1, "Bio-Data", true));
        stepper.add(createStepSeparator());
        stepper.add(createStepPill(2, "Documents", false));
        stepper.add(createStepSeparator());
        stepper.add(createStepPill(3, "Program", false));
        stepper.add(createStepSeparator());
        stepper.add(createStepPill(4, "Schedule", false));
        stepper.setAlignmentX(Component.LEFT_ALIGNMENT);
        return stepper;
    }

    private Component createStepSeparator() {
        JLabel separator = new JLabel("->");
        separator.setBorder(new EmptyBorder(0, 12, 0, 12));
        separator.setForeground(new Color(210, 224, 247));
        separator.setFont(Theme.BODY_FONT);
        return separator;
    }

    private JPanel createStepPill(int stepNumber, String label, boolean active) {
        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        container.setOpaque(false);

        JLabel index = new JLabel(String.format("%02d", stepNumber));
        index.putClientProperty(FlatClientProperties.STYLE,
            active
                ? "font:+2; font:bold; foreground:#FFFFFF;"
                : "font:+1; foreground:#B7C6E6;");

        JLabel text = new JLabel(label);
        text.putClientProperty(FlatClientProperties.STYLE,
            active
                ? "font:+2; font:bold; foreground:#FFFFFF;"
                : "font:+1; foreground:#CFDAF4;");

        container.add(index);
        container.add(text);
        return container;
    }

    private JComponent createSectionCard(String title, JComponent bodyContent) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new FlatDropShadowBorder(),
            new CompoundBorder(new FlatRoundBorder(), new EmptyBorder(24, 28, 24, 28))
        ));

        JLabel heading = new JLabel(title);
        heading.setFont(Theme.SUBHEADER_FONT);
        heading.setForeground(Theme.TEXT_HEADER);
        card.add(heading, BorderLayout.NORTH);
        card.add(bodyContent, BorderLayout.CENTER);

        wrapper.add(card, BorderLayout.CENTER);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        return wrapper;
    }

    private JComponent buildPersonalInformationSection() {
        JPanel body = createSectionBody();

        lastNameField = createTextField(true);
        firstNameField = createTextField(true);
        middleNameField = createTextField(false);
        extensionField = createTextField(false);
        body.add(createFieldRow(
            createLabeledComponent("Last Name", lastNameField, true),
            createLabeledComponent("First Name", firstNameField, true),
            createLabeledComponent("Middle Name", middleNameField, false),
            createLabeledComponent("Extension", extensionField, false)
        ));

        placeOfBirthField = createTextField(true);
        citizenshipField = createTextField(true);
        body.add(Box.createVerticalStrut(12));
        body.add(createFieldRow(
            createLabeledComponent("Date of Birth", createBirthDatePicker(), true),
            createLabeledComponent("Place of Birth", placeOfBirthField, true),
            createLabeledComponent("Citizenship", citizenshipField, true)
        ));

        religionField = createTextField(true);
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

        mobileField = createTextField(true);
        mobileField.setText("+63");
        emailField = createTextField(true);
        body.add(createFieldRow(
            createLabeledComponent("Mobile Number", mobileField, true),
            createLabeledComponent("Email Address", emailField, true)
        ));

        facebookField = createTextField(false);
        body.add(Box.createVerticalStrut(12));
        body.add(createFieldRow(
            createLabeledComponent("Facebook Profile", facebookField, false)
        ));

        return body;
    }

    private JComponent buildAddressSection() {
        JPanel body = createSectionBody();

        streetField = createTextField(true);
        barangayField = createTextField(true);
        body.add(createFieldRow(
            createLabeledComponent("Street / House No.", streetField, true),
            createLabeledComponent("Barangay", barangayField, true)
        ));

        cityField = createTextField(true);
        provinceField = createTextField(true);
        body.add(Box.createVerticalStrut(12));
        body.add(createFieldRow(
            createLabeledComponent("City / Municipality", cityField, true),
            createLabeledComponent("Province", provinceField, true)
        ));

        return body;
    }

    private JComponent buildGuardianSection() {
        JPanel body = createSectionBody();

        guardianNameField = createTextField(true);
        guardianRelationshipField = createTextField(true);
        guardianContactField = createTextField(true);
        guardianOccupationField = createTextField(true);
        guardianCompanyField = createTextField(true);
        guardianOfficePhoneField = createTextField(true);

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

        emergencyNameField = createTextField(false);
        emergencyContactField = createTextField(false);
        emergencyRelationshipField = createTextField(false);

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
        nextButton.setBackground(Theme.PRIMARY);
        nextButton.setForeground(Color.WHITE);
        nextButton.setBorder(new EmptyBorder(12, 32, 12, 32));
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
            StringBuilder message = new StringBuilder("Student registered successfully.");
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
            Navigation.to(this, Screen.PROGRAM_SELECTION);
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

    private JTextField createTextField(boolean required) {
        JTextField field = new JTextField();
        field.setFont(Theme.BODY_FONT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.putClientProperty(FlatClientProperties.STYLE, "arc:16; focusWidth:1");
        if (required) {
            registerRequiredField(field);
        }
        return field;
    }

    private JComboBox<String> createComboBox(String[] options, boolean required) {
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setFont(Theme.BODY_FONT);
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboBox.putClientProperty(FlatClientProperties.STYLE, "buttonType:roundRect; focusWidth:1");
        if (required) {
            registerRequiredCombo(comboBox);
        }
        return comboBox;
    }

    private JComponent createBirthDatePicker() {
        birthDateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, new Date(), Calendar.DAY_OF_MONTH));
        birthDateSpinner.setFont(Theme.BODY_FONT);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(birthDateSpinner, "yyyy-MM-dd");
        editor.getTextField().setEditable(false);
        editor.getTextField().setBorder(new EmptyBorder(0, 8, 0, 8));
        birthDateSpinner.setEditor(editor);
        birthDateSpinner.putClientProperty(FlatClientProperties.STYLE, "focusWidth:1");
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
        label.setFont(Theme.LABEL_FONT);
        label.setForeground(Theme.TEXT_SECONDARY_COLOR);

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

    private record ValidatorEntry(JComponent component, Supplier<Boolean> validator) {
    }

    private static class GradientHeaderPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint paint = new GradientPaint(
                0, 0, new Color(12, 92, 177),
                getWidth(), getHeight(), new Color(5, 32, 84)
            );
            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
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

