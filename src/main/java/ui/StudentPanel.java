package ui;

import dao.StudentDAO;
import model.Student;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * A panel for managing students, including adding, updating, deleting, and searching for students.
 */
public class StudentPanel extends JPanel {

    private final StudentDAO studentDAO;
    private final JTable studentTable;
    private final DefaultTableModel tableModel;
    private final TableRowSorter<DefaultTableModel> sorter;

    private final JTextField searchField;
    private final JTextField studentIdField;
    private final JTextField firstNameField;
    private final JTextField lastNameField;
    private final JTextField contactNumberField;
    private final JTextField emailField;
    private final JComboBox<String> genderComboBox;
    private final JTextField addressField;
    private final JTextField courseField;
    private final JTextField yearLevelField;
    private final JTextField blockField;

    /**
     * Constructs a new StudentPanel.
     */
    public StudentPanel() {
        studentDAO = new StudentDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(30);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        // Student form panel
        JPanel formPanel = new JPanel(new GridLayout(12, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));

        formPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        formPanel.add(studentIdField);

        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("Contact Number:"));
        contactNumberField = new JTextField();
        formPanel.add(contactNumberField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Gender:"));
        genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});
        formPanel.add(genderComboBox);

        formPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        formPanel.add(addressField);

        formPanel.add(new JLabel("Course:"));
        courseField = new JTextField();
        formPanel.add(courseField);

        formPanel.add(new JLabel("Year Level:"));
        yearLevelField = new JTextField();
        formPanel.add(yearLevelField);

        formPanel.add(new JLabel("Block:"));
        blockField = new JTextField();
        formPanel.add(blockField);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        formPanel.add(new JLabel()); // placeholder
        formPanel.add(buttonPanel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Student table
        tableModel = new DefaultTableModel(new String[]{"ID", "Student ID", "First Name", "Last Name", "Contact", "Email", "Gender", "Address", "Course", "Year", "Block"}, 0);
        studentTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(sorter);

        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // Add listeners
        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearButton.addActionListener(e -> clearFields());

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && studentTable.getSelectedRow() != -1) {
                populateFieldsFromSelectedRow();
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterStudents();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterStudents();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterStudents();
            }
        });

        loadStudents();
    }

    private void loadStudents() {
        try {
            List<Student> students = studentDAO.getAll();
            tableModel.setRowCount(0);
            for (Student student : students) {
                tableModel.addRow(new Object[]{
                        student.getId(),
                        student.getStudentId(),
                        student.getFirstName(),
                        student.getLastName(),
                        student.getContactNumber(),
                        student.getEmail(),
                        student.getGender(),
                        student.getAddress(),
                        student.getCourse(),
                        student.getYearLevel(),
                        student.getBlock()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addStudent() {
        try {
            Student student = new Student(
                    studentIdField.getText(),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    contactNumberField.getText(),
                    emailField.getText(),
                    (String) genderComboBox.getSelectedItem(),
                    addressField.getText(),
                    courseField.getText(),
                    Integer.parseInt(yearLevelField.getText()),
                    blockField.getText()
            );
            studentDAO.add(student);
            loadStudents();
            clearFields();
            JOptionPane.showMessageDialog(this, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid year level. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding student: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Student student = new Student(
                    studentIdField.getText(),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    contactNumberField.getText(),
                    emailField.getText(),
                    (String) genderComboBox.getSelectedItem(),
                    addressField.getText(),
                    courseField.getText(),
                    Integer.parseInt(yearLevelField.getText()),
                    blockField.getText()
            );
            student.setId((int) tableModel.getValueAt(studentTable.convertRowIndexToModel(selectedRow), 0));
            studentDAO.update(student);
            loadStudents();
            clearFields();
            JOptionPane.showMessageDialog(this, "Student updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid year level. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating student: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(studentTable.convertRowIndexToModel(selectedRow), 0);
                studentDAO.delete(id);
                loadStudents();
                clearFields();
                JOptionPane.showMessageDialog(this, "Student deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting student: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        studentIdField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        contactNumberField.setText("");
        emailField.setText("");
        genderComboBox.setSelectedIndex(0);
        addressField.setText("");
        courseField.setText("");
        yearLevelField.setText("");
        blockField.setText("");
        studentTable.clearSelection();
    }

    private void populateFieldsFromSelectedRow() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow != -1) {
            studentIdField.setText((String) tableModel.getValueAt(studentTable.convertRowIndexToModel(selectedRow), 1));
            firstNameField.setText((String) tableModel.getValueAt(studentTable.convertRowIndexToModel(selectedRow), 2));
            lastNameField.setText((String) tableModel.getValueAt(studentTable.convertRowIndexToModel(selectedRow), 3));
            contactNumberField.setText((String) tableModel.getValueAt(studentTable.convertRowIndexToModel(selectedRow), 4));
            emailField.setText((String) tableModel.getValueAt(studentTable.convertRowIndexToModel(selectedRow), 5));
            genderComboBox.setSelectedItem(tableModel.getValueAt(studentTable.convertRowIndexToModel(selectedRow), 6));
            addressField.setText((String) tableModel.getValueAt(studentTable.convertRowIndexToModel(selectedRow), 7));
            courseField.setText((String) tableModel.getValueAt(studentTable.convertRowIndexToModel(selectedRow), 8));
            yearLevelField.setText(String.valueOf(tableModel.getValueAt(studentTable.convertRowIndexToModel(selectedRow), 9)));
            blockField.setText((String) tableModel.getValueAt(studentTable.convertRowIndexToModel(selectedRow), 10));
        }
    }

    private void filterStudents() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
}
