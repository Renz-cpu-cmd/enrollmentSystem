package context;

import dao.CourseDAO;
import dao.EnrollmentDAO;
import dao.PaymentDAO;
import dao.StudentDAO;
import service.EnrollmentService;
import service.LoginService;

public class ApplicationContext {

    // DAOs
    private static final StudentDAO studentDAO = new StudentDAO();
    private static final CourseDAO courseDAO = new CourseDAO();
    private static final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private static final PaymentDAO paymentDAO = new PaymentDAO();

    // Services
    private static final EnrollmentService enrollmentService = new EnrollmentService(studentDAO);
    private static final LoginService loginService = new LoginService(studentDAO);

    // Private constructor to prevent instantiation
    private ApplicationContext() {
    }

    // --- Getters for Services ---

    public static EnrollmentService getEnrollmentService() {
        return enrollmentService;
    }

    public static LoginService getLoginService() {
        return loginService;
    }

    // --- Getters for DAOs (optional, but can be useful) ---

    public static StudentDAO getStudentDAO() {
        return studentDAO;
    }

    public static CourseDAO getCourseDAO() {
        return courseDAO;
    }

    public static EnrollmentDAO getEnrollmentDAO() {
        return enrollmentDAO;
    }

    public static PaymentDAO getPaymentDAO() {
        return paymentDAO;
    }
}
