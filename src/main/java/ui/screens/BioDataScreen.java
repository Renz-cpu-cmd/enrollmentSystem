
package ui.screens;

import com.toedter.calendar.JDateChooser;
import dao.StudentDAO;
import model.Student;
import ui.MobileFrame;
import ui.theme.AccordionPanel;
import ui.theme.ProgressStepper;
import ui.theme.Theme;
import ui.theme.Toast;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class BioDataScreen extends JPanel {

    // Form Components
    private JTextField lastNameField, firstNameField, middleNameField;
    private JComboBox<String> suffixComboBox, sexComboBox;
    private JDateChooser birthdateChooser;
    private JTextField mobileNumberField, emailField;
    private JTextArea homeAddressArea;
    private JTextField guardianNameField, guardianMobileField;
    private JTextField lastSchoolAttendedField;
    private JComboBox<String> strandComboBox;

    public BioDataScreen() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(Theme.PADDING_BORDER);

        // Header
        setupHeader();

        // Main Content (Scrollable)
        setupMainContent();

        // Footer (Button Panel)
        setupButtonPanel();
    }

    private void setupHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.BACKGROUND_COLOR);

        // Progress Stepper
        String[] steps = {"Bio-Data", "Documents", "Block", "Finish"};
        ProgressStepper stepper = new ProgressStepper(steps);
        stepper.setCurrentStep(0);
        headerPanel.add(stepper, BorderLayout.NORTH);

        // Title
        JLabel titleLabel = new JLabel("Student Biodata Form", SwingConstants.CENTER);
        titleLabel.setFont(Theme.HEADING_FONT);
        titleLabel.setForeground(Theme.TEXT_PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void setupMainContent() {
        JPanel accordionContainer = new JPanel();
        accordionContainer.setLayout(new BoxLayout(accordionContainer, BoxLayout.Y_AXIS));
        accordionContainer.setBackground(Theme.BACKGROUND_COLOR);

        // Create and Add Accordion Panels (Cards)
        createAccordions(accordionContainer);

        JScrollPane scrollPane = new JScrollPane(accordionContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createAccordions(JPanel container) {
        AccordionPanel personalInfoAccordion = new AccordionPanel("Personal Information");
        personalInfoAccordion.setContent(createPersonalInfoPanel());
        personalInfoAccordion.setExpanded(true);
        container.add(personalInfoAccordion);
        container.add(Box.createVerticalStrut(10));

        AccordionPanel contactInfoAccordion = new AccordionPanel("Contact Information");
        contactInfoAccordion.setContent(createContactInfoPanel());
        container.add(contactInfoAccordion);
        container.add(Box.createVerticalStrut(10));

        AccordionPanel familyAccordion = new AccordionPanel("Family Background");
        familyAccordion.setContent(createFamilyBackgroundPanel());
        container.add(familyAccordion);
        container.add(Box.createVerticalStrut(10));

        AccordionPanel educationAccordion = new AccordionPanel("Educational History");
        educationAccordion.setContent(createEducationalHistoryPanel());
        container.add(educationAccordion);
    }

    private void setupButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Theme.BACKGROUND_COLOR);

        JButton backButton = new JButton("Back");
        JButton clearButton = new JButton("Clear");
        JButton proceedButton = new JButton("Save and Proceed");

        // Style buttons using theme properties
        // (Secondary button styling might need a custom method if needed frequently)
        clearButton.setBackground(Theme.SURFACE_COLOR);
        clearButton.setForeground(Theme.TEXT_SECONDARY_COLOR);

        buttonPanel.add(backButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(proceedButton);
        add(buttonPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> navigateBack());
        clearButton.addActionListener(e -> clearForm());
        proceedButton.addActionListener(e -> saveStudentData());
    }

    // -- Panel Creation Methods for Accordion Content --

    private JPanel createFormSectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Theme.SURFACE_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private GridBagConstraints createGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        return gbc;
    }

    // -- Field Creation Helper Methods --

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.LABEL_FONT);
        label.setForeground(Theme.TEXT_SECONDARY_COLOR);
        return label;
    }

    private JTextField addTextField(JPanel panel, String labelText, GridBagConstraints gbc, int y) {
        gbc.gridy = y;
        gbc.gridx = 0;
        panel.add(createFieldLabel(labelText), gbc);

        gbc.gridx = 1;
        JTextField textField = new JTextField();
        panel.add(textField, gbc);
        return textField;
    }

    private JComboBox<String> addComboBox(JPanel panel, String labelText, String[] items, GridBagConstraints gbc, int y) {
        gbc.gridy = y;
        gbc.gridx = 0;
        panel.add(createFieldLabel(labelText), gbc);

        gbc.gridx = 1;
        JComboBox<String> comboBox = new JComboBox<>(items);
        panel.add(comboBox, gbc);
        return comboBox;
    }

    private JDateChooser addDateChooser(JPanel panel, String labelText, GridBagConstraints gbc, int y) {
        gbc.gridy = y;
        gbc.gridx = 0;
        panel.add(createFieldLabel(labelText), gbc);

        gbc.gridx = 1;
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        panel.add(dateChooser, gbc);
        return dateChooser;
    }

    private JTextArea addTextArea(JPanel panel, String labelText, GridBagConstraints gbc, int y) {
        gbc.gridy = y;
        gbc.gridx = 0;
        panel.add(createFieldLabel(labelText), gbc);

        gbc.gridx = 1;
        JTextArea textArea = new JTextArea(3, 20);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, gbc);
        return textArea;
    }

    // -- Form Content Panels --

    private JPanel createPersonalInfoPanel() {
        JPanel panel = createFormSectionPanel();
        GridBagConstraints gbc = createGbc();
        int y = 0;

        lastNameField = addTextField(panel, "Last Name:", gbc, y++);
        firstNameField = addTextField(panel, "First Name:", gbc, y++);
        middleNameField = addTextField(panel, "Middle Name:", gbc, y++);
        suffixComboBox = addComboBox(panel, "Suffix:", new String[]{"", "Jr.", "Sr.", "III", "IV"}, gbc, y++);
        birthdateChooser = addDateChooser(panel, "Birthdate:", gbc, y++);
        sexComboBox = addComboBox(panel, "Sex:", new String[]{"Male", "Female"}, gbc, y++);
        return panel;
    }

    private JPanel createContactInfoPanel() {
        JPanel panel = createFormSectionPanel();
        GridBagConstraints gbc = createGbc();
        int y = 0;
        mobileNumberField = addTextField(panel, "Mobile Number (+63):", gbc, y++);
        emailField = addTextField(panel, "Email Address:", gbc, y++);
        homeAddressArea = addTextArea(panel, "Home Address:", gbc, y++);
        return panel;
    }

    private JPanel createFamilyBackgroundPanel() {
        JPanel panel = createFormSectionPanel();
        GridBagConstraints gbc = createGbc();
        int y = 0;
        guardianNameField = addTextField(panel, "Guardian Name:", gbc, y++);
        guardianMobileField = addTextField(panel, "Guardian Mobile:", gbc, y++);
        return panel;
    }

    private JPanel createEducationalHistoryPanel() {
        JPanel panel = createFormSectionPanel();
        GridBagConstraints gbc = createGbc();
        int y = 0;
        lastSchoolAttendedField = addTextField(panel, "Last School Attended:", gbc, y++);
        strandComboBox = addComboBox(panel, "SHS Strand:", new String[]{"STEM", "TVL-ICT", "HUMSS", "ABM", "GAS"}, gbc, y++);
        return panel;
    }


    // -- Data Handling and Navigation --

    private void saveStudentData() {
        if (lastNameField.getText().trim().isEmpty() || firstNameField.getText().trim().isEmpty() || birthdateChooser.getDate() == null) {
            Toast.makeText(this, "Please fill in required fields (Last Name, First Name, Birthdate).", Toast.Type.WARNING, Toast.LENGTH_LONG);
            return;
        }

        Student newStudent = new Student();
        newStudent.setLastName(lastNameField.getText().trim());
        newStudent.setFirstName(firstNameField.getText().trim());
        newStudent.setMiddleName(middleNameField.getText().trim());
        newStudent.setSuffix((String) suffixComboBox.getSelectedItem());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        newStudent.setBirthDate(sdf.format(birthdateChooser.getDate()));

        newStudent.setSex((String) sexComboBox.getSelectedItem());
        newStudent.setMobileNumber(mobileNumberField.getText().trim());
        newStudent.setEmail(emailField.getText().trim());
        newStudent.setHomeAddress(homeAddressArea.getText().trim());
        newStudent.setGuardianName(guardianNameField.getText().trim());
        newStudent.setGuardianMobile(guardianMobileField.getText().trim());
        newStudent.setLastSchoolAttended(lastSchoolAttendedField.getText().trim());
        newStudent.setShsStrand((String) strandComboBox.getSelectedItem());

        if (!"STEM".equals(newStudent.getShsStrand()) && !"TVL-ICT".equals(newStudent.getShsStrand())) {
            Toast.makeText(this, "Note: Bridging subjects will be added to your curriculum.", Toast.Type.INFO, Toast.LENGTH_LONG);
        }

        String tempStudentId = "TEMP-" + System.currentTimeMillis();
        String tempPassword = "password123";
        newStudent.setStudentId(tempStudentId);
        newStudent.setPassword(tempPassword);

        StudentDAO studentDAO = new StudentDAO();
        if (studentDAO.add(newStudent)) {
            Toast.makeText(this, "Student data saved! Your ID: " + tempStudentId, Toast.Type.SUCCESS, Toast.LENGTH_LONG);
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.showScreen("ProgramSelection");
            }
        } else {
            Toast.makeText(this, "Error saving student data.", Toast.Type.ERROR, Toast.LENGTH_SHORT);
        }
    }

    private void clearForm() {
        lastNameField.setText("");
        firstNameField.setText("");
        middleNameField.setText("");
        mobileNumberField.setText("");
        emailField.setText("");
        homeAddressArea.setText("");
        guardianNameField.setText("");
        guardianMobileField.setText("");
        lastSchoolAttendedField.setText("");
        suffixComboBox.setSelectedIndex(0);
        sexComboBox.setSelectedIndex(0);
        strandComboBox.setSelectedIndex(0);
        birthdateChooser.setDate(null);
        Toast.makeText(this, "Form cleared.", Toast.Type.INFO, Toast.LENGTH_SHORT);
    }

    private void navigateBack() {
        MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.showScreen("DataPrivacy");
        }
    }
}
