
package util;

import model.Student;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A singleton class to manage the current user session data through the enrollment flow.
 * This is a simple implementation for demonstration purposes.
 */
public class SessionManager {

    private static SessionManager instance;
    private Student currentStudent;
    private List<String> enrolledSubjects;
    private Map<String, String> assessedFees;

    private SessionManager() {
        // Initialize with empty data to prevent null pointers
        this.enrolledSubjects = new ArrayList<>();
        this.assessedFees = new HashMap<>();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
    }

    public List<String> getEnrolledSubjects() {
        return enrolledSubjects;
    }

    public void setEnrolledSubjects(List<String> enrolledSubjects) {
        this.enrolledSubjects = enrolledSubjects;
    }

    public Map<String, String> getAssessedFees() {
        return assessedFees;
    }

    public void setAssessedFees(Map<String, String> assessedFees) {
        this.assessedFees = assessedFees;
    }

    /**
     * Clears all session data, including student info, subjects, and fees.
     * Should be called after enrollment is complete or on logout.
     */
    public void clearSession() {
        currentStudent = null;
        if (enrolledSubjects != null) {
            enrolledSubjects.clear();
        }
        if (assessedFees != null) {
            assessedFees.clear();
        }
    }
}
