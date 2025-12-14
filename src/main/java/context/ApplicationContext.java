package context;

import dao.CourseDAO;
import dao.EnrollmentDAO;
import dao.PaymentDAO;
import dao.StudentDAO;
import service.EnrollmentService;
import service.LoginService;
import service.PaymentService;

public class ApplicationContext {

    // DAOs
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final PaymentDAO paymentDAO;

    // Services
    private final EnrollmentService enrollmentService;
    private final LoginService loginService;
    private final PaymentService paymentService;

    public ApplicationContext() {
        this.studentDAO = new StudentDAO();
        this.courseDAO = new CourseDAO();
        this.enrollmentDAO = new EnrollmentDAO();
        this.paymentDAO = new PaymentDAO();

        this.enrollmentService = new EnrollmentService(studentDAO);
        this.loginService = new LoginService(studentDAO);
        this.paymentService = new PaymentService(paymentDAO);
    }

    // --- Getters for Services ---

    public EnrollmentService getEnrollmentService() {
        return enrollmentService;
    }

    public LoginService getLoginService() {
        return loginService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    // --- Getters for DAOs (optional, but can be useful) ---

    public StudentDAO getStudentDAO() {
        return studentDAO;
    }

    public CourseDAO getCourseDAO() {
        return courseDAO;
    }

    public EnrollmentDAO getEnrollmentDAO() {
        return enrollmentDAO;
    }

    public PaymentDAO getPaymentDAO() {
        return paymentDAO;
    }
}
