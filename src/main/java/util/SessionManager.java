
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
    private long lastActivityEpochMs;
    private List<String> enrolledSubjects;
    private Map<String, String> assessedFees;
    private String shsStrand;
    private static final long DEFAULT_SESSION_TIMEOUT_MS = 30 * 60 * 1000; // 30 minutes

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
        touch();
    }

    public boolean isSessionExpired() {
        long now = System.currentTimeMillis();
        return currentStudent != null && (now - lastActivityEpochMs) > DEFAULT_SESSION_TIMEOUT_MS;
    }

    public void ensureNotExpired() {
        if (isSessionExpired()) {
            clearSession();
        }
    }

    public void touch() {
        lastActivityEpochMs = System.currentTimeMillis();
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

    public String getShsStrand() {
        return shsStrand;
    }

    public void setShsStrand(String shsStrand) {
        this.shsStrand = shsStrand;
    }

    /**
     * Clears all session data, including student info, subjects, and fees.
     * Should be called after enrollment is complete or on logout.
     */
    public void clearSession() {
        currentStudent = null;
        lastActivityEpochMs = 0L;
        if (enrolledSubjects != null) {
            enrolledSubjects.clear();
        }
        if (assessedFees != null) {
            assessedFees.clear();
        }
        shsStrand = null;
    }
}
