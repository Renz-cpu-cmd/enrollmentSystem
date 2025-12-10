package ui;

import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import dao.CourseDAO;
import model.Course;
import util.GeminiClient;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class CoursePanel extends JPanel {

    private final CourseDAO courseDAO;
    private final JTable courseTable;
    private final DefaultTableModel tableModel;
    private final TableRowSorter<DefaultTableModel> sorter;

    private final JTextField searchField;
    private final JTextField codeField;
    private final JTextField nameField;
    private final JTextArea descriptionField; // Changed to JTextArea
    private final JTextField unitsField;

    private final GenerativeModel generativeModel;

    public CoursePanel() {
        courseDAO = new CourseDAO();
        generativeModel = GeminiClient.getModel(); // Use the centralized client
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(30);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        // Course form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Course Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Code:"), gbc);

        gbc.gridx = 1;
        codeField = new JTextField();
        formPanel.add(codeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField();
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        descriptionField = new JTextArea(3, 20); // Changed to JTextArea
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionField);
        formPanel.add(descriptionScrollPane, gbc);

        gbc.gridx = 2;
        JButton generateButton = new JButton("Generate");
        formPanel.add(generateButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Units:"), gbc);

        gbc.gridx = 1;
        unitsField = new JTextField();
        formPanel.add(unitsField, gbc);

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

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        formPanel.add(buttonPanel, gbc);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Course table
        tableModel = new DefaultTableModel(new String[]{"ID", "Code", "Name", "Description", "Units"}, 0);
        courseTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        courseTable.setRowSorter(sorter);

        add(new JScrollPane(courseTable), BorderLayout.CENTER);

        // Add listeners
        addButton.addActionListener(e -> addCourse());
        updateButton.addActionListener(e -> updateCourse());
        deleteButton.addActionListener(e -> deleteCourse());
        clearButton.addActionListener(e -> clearFields());
        generateButton.addActionListener(e -> generateCourseDescription());

        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && courseTable.getSelectedRow() != -1) {
                populateFieldsFromSelectedRow();
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterCourses();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterCourses();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterCourses();
            }
        });

        loadCourses();
    }

    private void generateCourseDescription() {
        if (generativeModel == null) {
            JOptionPane.showMessageDialog(this, "AI model is not available. Check environment variables.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String courseName = nameField.getText();
        if (courseName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a course name first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String prompt = "Write a course description for \"" + courseName + "\".";
            GenerateContentResponse response = generativeModel.generateContent(prompt);
            String description = response.getCandidates(0).getContent().getParts(0).getText();
            descriptionField.setText(description);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating description: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCourses() {
        List<Course> courses = courseDAO.getAll();
        tableModel.setRowCount(0);
        for (Course course : courses) {
            tableModel.addRow(new Object[]{
                    course.getId(),
                    course.getCode(),
                    course.getName(),
                    course.getDescription(),
                    course.getUnits()
            });
        }
    }

    private void addCourse() {
        try {
            Course course = new Course(
                    codeField.getText(),
                    nameField.getText(),
                    descriptionField.getText(),
                    Integer.parseInt(unitsField.getText())
            );
            if (courseDAO.add(course)) {
                loadCourses();
                clearFields();
                JOptionPane.showMessageDialog(this, "Course added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error adding course.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid units. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Course course = new Course(
                    codeField.getText(),
                    nameField.getText(),
                    descriptionField.getText(),
                    Integer.parseInt(unitsField.getText())
            );
            course.setId((int) tableModel.getValueAt(courseTable.convertRowIndexToModel(selectedRow), 0));
            if (courseDAO.update(course)) {
                loadCourses();
                clearFields();
                JOptionPane.showMessageDialog(this, "Course updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error updating course.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid units. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this course?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(courseTable.convertRowIndexToModel(selectedRow), 0);
            if (courseDAO.delete(id)) {
                loadCourses();
                clearFields();
                JOptionPane.showMessageDialog(this, "Course deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting course.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        codeField.setText("");
        nameField.setText("");
        descriptionField.setText("");
        unitsField.setText("");
        courseTable.clearSelection();
    }

    private void populateFieldsFromSelectedRow() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow != -1) {
            codeField.setText((String) tableModel.getValueAt(courseTable.convertRowIndexToModel(selectedRow), 1));
            nameField.setText((String) tableModel.getValueAt(courseTable.convertRowIndexToModel(selectedRow), 2));
            descriptionField.setText((String) tableModel.getValueAt(courseTable.convertRowIndexToModel(selectedRow), 3));
            unitsField.setText(String.valueOf(tableModel.getValueAt(courseTable.convertRowIndexToModel(selectedRow), 4)));
        }
    }

    private void filterCourses() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
}
