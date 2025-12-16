package dao;

import model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
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
        String sql = "INSERT INTO payments (assessment_id, amount, payment_method, reference_no, payment_date) VALUES (?, ?, ?, ?, COALESCE(?, datetime('now')))";
        Connection conn = connection != null ? connection : getConnectionQuietly();
        boolean shouldClose = connection == null;
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, payment.getAssessmentId());
            pstmt.setBigDecimal(2, payment.getAmount());
            pstmt.setString(3, payment.getPaymentMethod());
            pstmt.setString(4, payment.getReferenceNo());
            pstmt.setString(5, payment.getPaymentDate());
            boolean inserted = pstmt.executeUpdate() > 0;
            if (inserted) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        payment.setId(rs.getInt(1));
                    }
                }
            }
            return inserted;
        } catch (SQLException e) {
            LOGGER.error("Error adding payment for assessment id: {}", payment.getAssessmentId(), e);
            return false;
        } finally {
            closeIfNeeded(conn, shouldClose);
        }
    }

    @Override
    public boolean update(Payment payment) {
        return updateWithConnection(payment, null);
    }

    public boolean updateWithConnection(Payment payment, Connection connection) {
        String sql = "UPDATE payments SET assessment_id = ?, amount = ?, payment_method = ?, reference_no = ?, payment_date = ? WHERE id = ?";
        Connection conn = connection != null ? connection : getConnectionQuietly();
        boolean shouldClose = connection == null;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payment.getAssessmentId());
            pstmt.setBigDecimal(2, payment.getAmount());
            pstmt.setString(3, payment.getPaymentMethod());
            pstmt.setString(4, payment.getReferenceNo());
            pstmt.setString(5, payment.getPaymentDate());
            pstmt.setInt(6, payment.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error updating payment with id: {}", payment.getId(), e);
            return false;
        } finally {
            closeIfNeeded(conn, shouldClose);
        }
    }

    @Override
    public boolean delete(Integer id) {
        return deleteWithConnection(id, null);
    }

    public boolean deleteWithConnection(Integer id, Connection connection) {
        String sql = "DELETE FROM payments WHERE id = ?";
        Connection conn = connection != null ? connection : getConnectionQuietly();
        boolean shouldClose = connection == null;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error deleting payment with id: {}", id, e);
            return false;
        } finally {
            closeIfNeeded(conn, shouldClose);
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
            LOGGER.error("Error retrieving payment with id: {}", id, e);
        }
        return null;
    }

    @Override
    public List<Payment> getAll() {
        return getAllPaged(200, 0);
    }

    public List<Payment> getAllPaged(int limit, int offset) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT id, assessment_id, amount, payment_method, reference_no, payment_date, created_at " +
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

    public BigDecimal sumPaymentsByAssessment(int assessmentId, Connection connection) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total_paid FROM payments WHERE assessment_id = ?";
        Connection conn = connection != null ? connection : getConnectionQuietly();
        boolean shouldClose = connection == null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assessmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total_paid");
                }
            }
            return BigDecimal.ZERO;
        } finally {
            closeIfNeeded(conn, shouldClose);
        }
    }

    public Payment findLatestForAssessment(int assessmentId) {
        String sql = "SELECT * FROM payments WHERE assessment_id = ? ORDER BY payment_date DESC, id DESC LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assessmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractPaymentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving latest payment for assessment {}", assessmentId, e);
        }
        return null;
    }

    private Payment extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setAssessmentId(rs.getInt("assessment_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setReferenceNo(rs.getString("reference_no"));
        payment.setPaymentDate(rs.getString("payment_date"));
        payment.setCreatedAt(rs.getString("created_at"));
        return payment;
    }

    private Connection getConnectionQuietly() {
        try {
            return DatabaseManager.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to acquire connection", e);
        }
    }

    private void closeIfNeeded(Connection conn, boolean shouldClose) {
        if (!shouldClose || conn == null) {
            return;
        }
        try {
            conn.close();
        } catch (SQLException e) {
            LOGGER.warn("Unable to close connection", e);
        }
    }
}
