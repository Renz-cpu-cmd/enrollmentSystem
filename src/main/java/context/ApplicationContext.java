package context;

import dao.AssessmentDAO;
import dao.BlockDAO;
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
    private final BlockDAO blockDAO;
    private final CourseDAO courseDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final PaymentDAO paymentDAO;
    private final AssessmentDAO assessmentDAO;

    // Services
    private final EnrollmentService enrollmentService;
    private final LoginService loginService;
    private final PaymentService paymentService;

    public ApplicationContext() {
        this.studentDAO = new StudentDAO();
        this.blockDAO = new BlockDAO();
        this.courseDAO = new CourseDAO();
        this.enrollmentDAO = new EnrollmentDAO();
        this.paymentDAO = new PaymentDAO();
        this.assessmentDAO = new AssessmentDAO();

        this.enrollmentService = new EnrollmentService(studentDAO, blockDAO, enrollmentDAO, assessmentDAO);
        this.loginService = new LoginService(studentDAO);
        this.paymentService = new PaymentService(paymentDAO, assessmentDAO, enrollmentDAO);
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

    public BlockDAO getBlockDAO() {
        return blockDAO;
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

    public AssessmentDAO getAssessmentDAO() {
        return assessmentDAO;
    }
}
