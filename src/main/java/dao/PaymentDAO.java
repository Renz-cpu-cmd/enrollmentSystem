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
public class PaymentDAO implements DataAccessObject<Payment> {

    /**
     * Adds a new payment to the database.
     *
     * @param payment The payment to add.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void add(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (enrollment_id, amount, payment_method, transaction_id, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payment.getEnrollmentId());
            pstmt.setBigDecimal(2, payment.getAmount());
            pstmt.setString(3, payment.getPaymentMethod());
            pstmt.setString(4, payment.getTransactionId());
            pstmt.setString(5, payment.getStatus());
            pstmt.executeUpdate();
        }
    }

    /**
     * Updates an existing payment in the database.
     *
     * @param payment The payment to update.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void update(Payment payment) throws SQLException {
        String sql = "UPDATE payments SET enrollment_id = ?, amount = ?, payment_method = ?, transaction_id = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payment.getEnrollmentId());
            pstmt.setBigDecimal(2, payment.getAmount());
            pstmt.setString(3, payment.getPaymentMethod());
            pstmt.setString(4, payment.getTransactionId());
            pstmt.setString(5, payment.getStatus());
            pstmt.setInt(6, payment.getId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a payment from the database by its ID.
     *
     * @param id The ID of the payment to delete.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM payments WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves a payment from the database by its ID.
     *
     * @param id The ID of the payment to retrieve.
     * @return The payment with the specified ID, or null if not found.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public Payment getById(int id) throws SQLException {
        String sql = "SELECT * FROM payments WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
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
        }
        return null;
    }

    /**
     * Retrieves all payments from the database.
     *
     * @return A list of all payments.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<Payment> getAll() throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setEnrollmentId(rs.getInt("enrollment_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setPaymentMethod(rs.getString("payment_method"));
                payment.setTransactionId(rs.getString("transaction_id"));
                payment.setStatus(rs.getString("status"));
                payment.setCreatedAt(rs.getString("created_at"));
                payments.add(payment);
            }
        }
        return payments;
    }
}
