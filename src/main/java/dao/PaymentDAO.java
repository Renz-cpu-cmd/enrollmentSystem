package dao;

import model.Payment;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the Payment model.
 * Implements the DataAccessObject interface to provide standard CRUD operations.
 */
public class PaymentDAO implements DataAccessObject<Payment, Integer> {

    @Override
    public boolean add(Payment payment) {
        String sql = "INSERT INTO payments (enrollment_id, amount, payment_method, transaction_id, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payment.getEnrollmentId());
            pstmt.setBigDecimal(2, payment.getAmount());
            pstmt.setString(3, payment.getPaymentMethod());
            pstmt.setString(4, payment.getTransactionId());
            pstmt.setString(5, payment.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Payment payment) {
        String sql = "UPDATE payments SET enrollment_id = ?, amount = ?, payment_method = ?, transaction_id = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payment.getEnrollmentId());
            pstmt.setBigDecimal(2, payment.getAmount());
            pstmt.setString(3, payment.getPaymentMethod());
            pstmt.setString(4, payment.getTransactionId());
            pstmt.setString(5, payment.getStatus());
            pstmt.setInt(6, payment.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM payments WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Payment getById(Integer id) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractPaymentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Payment> getAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
