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
        return addWithConnection(payment, null);
    }

    public boolean addWithConnection(Payment payment, Connection connection) {
        String sql = "INSERT INTO payments (enrollment_id, amount, payment_method, transaction_id, status) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = connection != null ? connection : DatabaseManager.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, payment.getEnrollmentId());
                pstmt.setBigDecimal(2, payment.getAmount());
                pstmt.setString(3, payment.getPaymentMethod());
                pstmt.setString(4, payment.getTransactionId());
                pstmt.setString(5, payment.getStatus());
                return pstmt.executeUpdate() > 0;
            } finally {
                if (connection == null) {
                    conn.close();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error adding payment for enrollment id: " + payment.getEnrollmentId(), e);
            return false;
        }
    }

    @Override
    public boolean update(Payment payment) {
        return updateWithConnection(payment, null);
    }

    public boolean updateWithConnection(Payment payment, Connection connection) {
        String sql = "UPDATE payments SET enrollment_id = ?, amount = ?, payment_method = ?, transaction_id = ?, status = ? WHERE id = ?";
        try {
            Connection conn = connection != null ? connection : DatabaseManager.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, payment.getEnrollmentId());
                pstmt.setBigDecimal(2, payment.getAmount());
                pstmt.setString(3, payment.getPaymentMethod());
                pstmt.setString(4, payment.getTransactionId());
                pstmt.setString(5, payment.getStatus());
                pstmt.setInt(6, payment.getId());
                return pstmt.executeUpdate() > 0;
            } finally {
                if (connection == null) {
                    conn.close();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating payment with id: " + payment.getId(), e);
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {
        return deleteWithConnection(id, null);
    }

    public boolean deleteWithConnection(Integer id, Connection connection) {
        String sql = "DELETE FROM payments WHERE id = ?";
        try {
            Connection conn = connection != null ? connection : DatabaseManager.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                return pstmt.executeUpdate() > 0;
            } finally {
                if (connection == null) {
                    conn.close();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error deleting payment with id: " + id, e);
            return false;
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
        return getAllPaged(200, 0);
    }

    public List<Payment> getAllPaged(int limit, int offset) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT id, enrollment_id, amount, payment_method, transaction_id, status, created_at " +
                "FROM payments ORDER BY id LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(extractPaymentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving payments (paged)", e);
        }
        return payments;
    }

    public int countAll() {
        String sql = "SELECT COUNT(1) FROM payments";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Error counting payments", e);
        }
        return 0;
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
