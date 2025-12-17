package service;

/**
 * Coordinates payment processing and lookups for enrollments.
 *
 * <p>Provides business logic over `PaymentDAO` and related data, keeping
 * payment workflows out of UI classes.</p>
 */

import dao.AssessmentDAO;
import dao.DatabaseManager;
import dao.EnrollmentDAO;
import dao.PaymentDAO;
import model.Assessment;
import model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);
    private static final Set<String> ALLOWED_METHODS = Set.of("CASH", "CARD", "ONLINE");

    private final PaymentDAO paymentDAO;
    private final AssessmentDAO assessmentDAO;
    private final EnrollmentDAO enrollmentDAO;

    public PaymentService(PaymentDAO paymentDAO, AssessmentDAO assessmentDAO, EnrollmentDAO enrollmentDAO) {
        this.paymentDAO = paymentDAO;
        this.assessmentDAO = assessmentDAO;
        this.enrollmentDAO = enrollmentDAO;
    }

    public PaymentResult processPayment(int assessmentId, double amount, String method) {
        if (assessmentId <= 0) {
            return PaymentResult.failure("Invalid assessment.");
        }
        BigDecimal paymentAmount = BigDecimal.valueOf(amount);
        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentResult.failure("Payment amount must be greater than zero.");
        }
        String normalizedMethod = normalizeMethod(method);
        if (!ALLOWED_METHODS.contains(normalizedMethod)) {
            return PaymentResult.failure("Payment method must be CASH, CARD, or ONLINE.");
        }

        try {
            return DatabaseManager.runInTransaction(conn -> {
                Optional<Assessment> assessmentOpt = assessmentDAO.findById(assessmentId, conn);
                if (assessmentOpt.isEmpty()) {
                    return PaymentResult.failure("Assessment not found.");
                }
                Assessment assessment = assessmentOpt.get();

                Payment payment = new Payment(assessmentId, paymentAmount, normalizedMethod, generateReference());
                payment.setPaymentDate(currentTimestamp());
                boolean inserted = paymentDAO.addWithConnection(payment, conn);
                if (!inserted) {
                    return PaymentResult.failure("Unable to record payment.");
                }

                BigDecimal totalPaid = paymentDAO.sumPaymentsByAssessment(assessmentId, conn);
                BigDecimal totalDue = Optional.ofNullable(assessment.getTotalDue()).orElse(BigDecimal.ZERO);
                String newStatus = totalPaid.compareTo(totalDue) >= 0 ? "PAID" : "PARTIAL";
                assessmentDAO.updateStatus(assessmentId, newStatus, conn);

                if ("PAID".equals(newStatus)) {
                    enrollmentDAO.updateStatus(assessment.getEnrollmentId(), "OFFICIALLY_ENROLLED", conn);
                }

                return PaymentResult.success(newStatus, payment.getReferenceNo(), totalPaid);
            });
        } catch (SQLException ex) {
            LOGGER.error("Payment processing failed for assessment {}", assessmentId, ex);
            return PaymentResult.failure("Unable to process payment right now.");
        }
    }

    public BigDecimal getTotalPaid(int assessmentId) {
        try {
            return paymentDAO.sumPaymentsByAssessment(assessmentId, null);
        } catch (SQLException e) {
            LOGGER.error("Failed to compute total paid for assessment {}", assessmentId, e);
            return BigDecimal.ZERO;
        }
    }

    public List<Payment> listPayments(int limit, int offset) {
        return paymentDAO.getAllPaged(limit, offset);
    }

    public int countPayments() {
        return paymentDAO.countAll();
    }

    private String normalizeMethod(String method) {
        return method == null ? "" : method.trim().toUpperCase();
    }

    private String generateReference() {
        String ts = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        return "PAY-" + ts;
    }

    private String currentTimestamp() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    public record PaymentResult(boolean success, String message, String assessmentStatus, String referenceNo, BigDecimal totalPaid) {
        public static PaymentResult success(String assessmentStatus, String referenceNo, BigDecimal totalPaid) {
            return new PaymentResult(true, "Payment recorded.", assessmentStatus, referenceNo, totalPaid);
        }

        public static PaymentResult failure(String message) {
            return new PaymentResult(false, message, null, null, BigDecimal.ZERO);
        }
    }
}
