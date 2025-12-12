package service;

import dao.StudentDAO;
import model.Student;
import org.mindrot.jbcrypt.BCrypt;
import util.SessionManager;

public class LoginService {

    private final StudentDAO studentDAO;

    public LoginService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    /**
     * Authenticates a student based on their student ID and password.
     *
     * @param studentId The student's ID.
     * @param password  The student's raw password.
     * @return true if authentication is successful, false otherwise.
     */
    public boolean login(String studentId, String password) {
        Student student = studentDAO.getStudentByStudentId(studentId);

        if (student != null && BCrypt.checkpw(password, student.getPassword())) {
            SessionManager.getInstance().setCurrentStudent(student);
            return true;
        }
        return false;
    }
}
