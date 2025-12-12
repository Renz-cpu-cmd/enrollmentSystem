package service;

import dao.StudentDAO;
import model.Student;
import org.mindrot.jbcrypt.BCrypt;

public class EnrollmentService {

    private final StudentDAO studentDAO;

    public EnrollmentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    /**
     * Registers a new student. This includes hashing the password and saving the student data.
     *
     * @param student The student object to register.
     * @return The newly created student with a generated ID and hashed password.
     * @throws IllegalArgumentException if the student data is invalid.
     */
    public Student registerNewStudent(Student student) throws IllegalArgumentException {
        // In a real application, more complex business logic and validation would go here.
        // For example, checking for uniqueness of email, generating a permanent student ID, etc.

        // Hash the password
        String tempPassword = "password123"; // This should be properly generated or passed in
        String hashedPassword = BCrypt.hashpw(tempPassword, BCrypt.gensalt());
        student.setPassword(hashedPassword);

        // Generate a temporary student ID
        String tempStudentId = "TEMP-" + System.currentTimeMillis();
        student.setStudentId(tempStudentId);

        if (studentDAO.add(student)) {
            return student;
        } else {
            throw new IllegalArgumentException("Could not save student data.");
        }
    }
}
