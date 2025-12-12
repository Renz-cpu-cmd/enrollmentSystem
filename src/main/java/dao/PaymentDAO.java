package dao;

import model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the Payment model.
 * Implements the DataAccessObject interface to provide standard CRUD operations.
 */
public class PaymentDAO implements DataAccessObject<Payment, Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDAO.class);

    @Override
    public boolean add(Payment payment) {
        String sql = "INSERT INTO payments (enrollment_id, amount, payment_method, transaction_id, status) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, payment.getEnrollmentId());
                pstmt.setBigDecimal(2, payment.getAmount());
                pstmt.setString(3, payment.getPaymentMethod());
                pstmt.setString(4, payment.getTransactionId());
                pstmt.setString(5, payment.getStatus());
                boolean result = pstmt.executeUpdate() > 0;
                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            LOGGER.error("Error adding payment for enrollment id: " + payment.getEnrollmentId(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.error("Error rolling back transaction", ex);
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error("Error closing connection", e);
                }
            }
        }
    }

    @Override
    public boolean update(Payment payment) {
        String sql = "UPDATE payments SET enrollment_id = ?, amount = ?, payment_method = ?, transaction_id = ?, status = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, payment.getEnrollmentId());
                pstmt.setBigDecimal(2, payment.getAmount());
                pstmt.setString(3, payment.getPaymentMethod());
                pstmt.setString(4, payment.getTransactionId());
                pstmt.setString(5, payment.getStatus());
                pstmt.setInt(6, payment.getId());
                boolean result = pstmt.executeUpdate() > 0;
                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating payment with id: " + payment.getId(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.error("Error rolling back transaction", ex);
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error("Error closing connection", e);
                }
            }
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM payments WHERE id = ?";
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                boolean result = pstmt.executeUpdate() > 0;
                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            LOGGER.error("Error deleting payment with id: " + id, e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.error("Error rolling back transaction", ex);
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error("Error closing connection", e);
                }
            }
        }
    }

    @Override
    public Payment getById(Integer id) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractPaymentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving payment with id: " + id, e);
        }
        return null;
    }

    @Override
    public List<Payment> getAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving all payments", e);
        }
        return payments;
    }

    private Payment extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setEnrollmentId(rs.getInt("enrollment_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setTransactionId(rs.getString("transaction_id"));
        payment.setStatus(rs.getString("status"));
        payment.setCreatedAt(rs.getString("created_at"));
        return payment;
    }
}
