package ui.components;

import dao.CourseDAO;
import dao.EnrollmentDAO;
import dao.StudentDAO;
import model.Course;
import model.Enrollment;
import model.Student;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A panel for managing enrollments, including adding, updating, deleting, and searching for enrollments.
 */
public class EnrollmentPanel extends JPanel {

    private final EnrollmentDAO enrollmentDAO;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final JTable enrollmentTable;
    private final DefaultTableModel tableModel;
    private final TableRowSorter<DefaultTableModel> sorter;

    private final JComboBox<Student> studentComboBox;
    private final JComboBox<Course> courseComboBox;
    private final JTextField academicYearField;
    private final JTextField termField;
    private final JComboBox<String> statusComboBox;
    private final JTextField searchField;

    private Map<Integer, Student> studentMap;
    private Map<Integer, Course> courseMap;

    /**
     * Constructs a new EnrollmentPanel.
     */
    public EnrollmentPanel() {
        enrollmentDAO = new EnrollmentDAO();
        studentDAO = new StudentDAO();
        courseDAO = new CourseDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(30);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        // Enrollment form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Enrollment Information"));

        formPanel.add(new JLabel("Student:"));
        studentComboBox = new JComboBox<>();
        formPanel.add(studentComboBox);

        formPanel.add(new JLabel("Course:"));
        courseComboBox = new JComboBox<>();
        formPanel.add(courseComboBox);

        formPanel.add(new JLabel("Academic Year:"));
        academicYearField = new JTextField();
        formPanel.add(academicYearField);

        formPanel.add(new JLabel("Term:"));
        termField = new JTextField();
        formPanel.add(termField);

        formPanel.add(new JLabel("Status:"));
        statusComboBox = new JComboBox<>(new String[]{"Enrolled", "Dropped", "Completed"});
        formPanel.add(statusComboBox);

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

        // Enrollment table
        tableModel = new DefaultTableModel(new String[]{"ID", "Student", "Course", "Academic Year", "Term", "Status"}, 0);
        enrollmentTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        enrollmentTable.setRowSorter(sorter);

        add(new JScrollPane(enrollmentTable), BorderLayout.CENTER);

        // Add listeners
        addButton.addActionListener(e -> addEnrollment());
        updateButton.addActionListener(e -> updateEnrollment());
        deleteButton.addActionListener(e -> deleteEnrollment());
        clearButton.addActionListener(e -> clearFields());

        enrollmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && enrollmentTable.getSelectedRow() != -1) {
                populateFieldsFromSelectedRow();
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterEnrollments();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterEnrollments();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterEnrollments();
            }
        });

        loadComboBoxes();
        loadEnrollments();
    }

    private void loadComboBoxes() {
        List<Student> students = studentDAO.getAllPaged(200, 0);
        studentMap = students.stream().collect(Collectors.toMap(Student::getId, student -> student));
        studentComboBox.removeAllItems();
        for (Student student : students) {
            studentComboBox.addItem(student);
        }

        List<Course> courses = courseDAO.getAllPaged(200, 0);
        courseMap = courses.stream().collect(Collectors.toMap(Course::getId, course -> course));
        courseComboBox.removeAllItems();
        for (Course course : courses) {
            courseComboBox.addItem(course);
        }
    }

    private void loadEnrollments() {
        List<Enrollment> enrollments = enrollmentDAO.getAllPaged(200, 0);
        tableModel.setRowCount(0);
        for (Enrollment enrollment : enrollments) {
            Student student = studentMap.get(enrollment.getStudentId());
            Course course = courseMap.get(enrollment.getCourseId());
            tableModel.addRow(new Object[]{
                    enrollment.getId(),
                    student != null ? student : "N/A",
                    course != null ? course : "N/A",
                    enrollment.getAcademicYear(),
                    enrollment.getTerm(),
                    enrollment.getStatus()
            });
        }
    }

    private void addEnrollment() {
        Student selectedStudent = (Student) studentComboBox.getSelectedItem();
        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        if (selectedStudent == null || selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a student and a course.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Enrollment enrollment = new Enrollment(
                selectedStudent.getId(),
                selectedCourse.getId(),
                academicYearField.getText(),
                termField.getText(),
                (String) statusComboBox.getSelectedItem()
        );
        if (enrollmentDAO.add(enrollment)) {
            loadEnrollments();
            clearFields();
            JOptionPane.showMessageDialog(this, "Enrollment added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error adding enrollment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEnrollment() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Student selectedStudent = (Student) studentComboBox.getSelectedItem();
        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        if (selectedStudent == null || selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a student and a course.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Enrollment enrollment = new Enrollment(
                selectedStudent.getId(),
                selectedCourse.getId(),
                academicYearField.getText(),
                termField.getText(),
                (String) statusComboBox.getSelectedItem()
        );
        enrollment.setId((int) tableModel.getValueAt(enrollmentTable.convertRowIndexToModel(selectedRow), 0));
        if (enrollmentDAO.update(enrollment)) {
            loadEnrollments();
            clearFields();
            JOptionPane.showMessageDialog(this, "Enrollment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error updating enrollment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEnrollment() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this enrollment?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(enrollmentTable.convertRowIndexToModel(selectedRow), 0);
            if (enrollmentDAO.delete(id)) {
                loadEnrollments();
                clearFields();
                JOptionPane.showMessageDialog(this, "Enrollment deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting enrollment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        studentComboBox.setSelectedIndex(-1);
        courseComboBox.setSelectedIndex(-1);
        academicYearField.setText("");
        termField.setText("");
        statusComboBox.setSelectedIndex(0);
        enrollmentTable.clearSelection();
    }

    private void populateFieldsFromSelectedRow() {
    int selectedRow = enrollmentTable.getSelectedRow();
    if (selectedRow != -1) {
        int modelRow = enrollmentTable.convertRowIndexToModel(selectedRow);

        Object studentObj = tableModel.getValueAt(modelRow, 1);
        if (studentObj instanceof Student) {
            Student student = (Student) studentObj;
            for (int i = 0; i < studentComboBox.getModel().getSize(); i++) {
                if (studentComboBox.getItemAt(i).getId() == student.getId()) {
                    studentComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        Object courseObj = tableModel.getValueAt(modelRow, 2);
        if (courseObj instanceof Course) {
            Course course = (Course) courseObj;
            for (int i = 0; i < courseComboBox.getModel().getSize(); i++) {
                if (courseComboBox.getItemAt(i).getId() == course.getId()) {
                    courseComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        academicYearField.setText(tableModel.getValueAt(modelRow, 3).toString());
        termField.setText(tableModel.getValueAt(modelRow, 4).toString());
        statusComboBox.setSelectedItem(tableModel.getValueAt(modelRow, 5).toString());
    }
}


    private void filterEnrollments() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
}


