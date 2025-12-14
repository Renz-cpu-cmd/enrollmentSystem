package service;

import dao.StudentDAO;
import model.Student;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SessionManager;

public class LoginService {

    private final StudentDAO studentDAO;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_WINDOW_MS = 15 * 60 * 1000; // 15 minutes window for counting failures
    private static final long LOCKOUT_DURATION_MS = 10 * 60 * 1000; // 10 minutes lockout

    private int failedAttempts = 0;
    private long firstFailureEpochMs = 0L;
    private long lockoutUntilEpochMs = 0L;

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
        long now = System.currentTimeMillis();

        if (now < lockoutUntilEpochMs) {
            LOGGER.warn("Login locked out for studentId {} until {}", studentId, lockoutUntilEpochMs);
            return false;
        }

        Student student = studentDAO.getStudentByStudentId(studentId);

        if (student != null && BCrypt.checkpw(password, student.getPassword())) {
            resetFailures();
            SessionManager.getInstance().setCurrentStudent(student);
            SessionManager.getInstance().touch();
            LOGGER.info("Login success for studentId {}", studentId);
            return true;
        }
        recordFailure(now, studentId);
        return false;
    }

    public void logout() {
        SessionManager.getInstance().clearSession();
    }

    public boolean isSessionExpired() {
        return SessionManager.getInstance().isSessionExpired();
    }

    public boolean isLockedOut() {
        return System.currentTimeMillis() < lockoutUntilEpochMs;
    }

    public long getLockoutMillisRemaining() {
        long now = System.currentTimeMillis();
        return Math.max(0, lockoutUntilEpochMs - now);
    }

    private void recordFailure(long now, String studentId) {
        if (firstFailureEpochMs == 0L || now - firstFailureEpochMs > LOCKOUT_WINDOW_MS) {
            firstFailureEpochMs = now;
            failedAttempts = 1;
        } else {
            failedAttempts++;
        }

        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            lockoutUntilEpochMs = now + LOCKOUT_DURATION_MS;
            LOGGER.warn("Locking out studentId {} for {} ms after {} failed attempts", studentId, LOCKOUT_DURATION_MS, failedAttempts);
        } else {
            LOGGER.warn("Login failed for studentId {} (attempt {} of {} in window)", studentId, failedAttempts, MAX_FAILED_ATTEMPTS);
        }
    }

    private void resetFailures() {
        failedAttempts = 0;
        firstFailureEpochMs = 0L;
        lockoutUntilEpochMs = 0L;
    }
}
