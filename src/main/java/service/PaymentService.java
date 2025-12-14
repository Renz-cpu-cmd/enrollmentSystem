package service;

import dao.PaymentDAO;
import model.Payment;
import util.TransactionManager;

import java.math.BigDecimal;
import java.util.List;

public class PaymentService {

    private final PaymentDAO paymentDAO;

    public PaymentService(PaymentDAO paymentDAO) {
        this.paymentDAO = paymentDAO;
    }

    public Payment recordPayment(Payment payment) {
        validatePayment(payment);
        boolean saved = TransactionManager.executeInTransaction(conn -> paymentDAO.addWithConnection(payment, conn));
        if (!saved) {
            throw new IllegalStateException("Could not save payment.");
        }
        return payment;
    }

    public boolean updatePayment(Payment payment) {
        validatePayment(payment);
        return TransactionManager.executeInTransaction(conn -> paymentDAO.updateWithConnection(payment, conn));
    }

    public boolean deletePayment(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Payment id must be positive.");
        }
        return TransactionManager.executeInTransaction(conn -> paymentDAO.deleteWithConnection(id, conn));
    }

    public List<Payment> listPayments(int limit, int offset) {
        return paymentDAO.getAllPaged(limit, offset);
    }

    public int countPayments() {
        return paymentDAO.countAll();
    }

    private void validatePayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null.");
        }
        if (payment.getEnrollmentId() <= 0) {
            throw new IllegalArgumentException("Enrollment id must be positive.");
        }
        BigDecimal amount = payment.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }
        if (isBlank(payment.getPaymentMethod())) {
            throw new IllegalArgumentException("Payment method is required.");
        }
        if (isBlank(payment.getStatus())) {
            throw new IllegalArgumentException("Payment status is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
