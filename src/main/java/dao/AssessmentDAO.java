package dao;

/**
 * Data Access Object for `Assessment` and related `AssessmentFee` records.
 *
 * <p>Encapsulates SQL operations and keeps the service layer independent of
 * persistence details.</p>
 */

import model.Assessment;
import model.AssessmentFee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssessmentDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssessmentDAO.class);

    public Assessment saveAssessment(Assessment assessment, Connection connection) throws SQLException {
        String sql = "INSERT INTO assessments (enrollment_id, total_units, tuition_fee, misc_fee, lab_fee, other_fee, total_due, currency, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, assessment.getEnrollmentId());
            ps.setDouble(2, assessment.getTotalUnits());
            ps.setBigDecimal(3, nullable(assessment.getTuitionFee()));
            ps.setBigDecimal(4, nullable(assessment.getMiscFee()));
            ps.setBigDecimal(5, nullable(assessment.getLabFee()));
            ps.setBigDecimal(6, nullable(assessment.getOtherFee()));
            ps.setBigDecimal(7, nullable(assessment.getTotalDue()));
            ps.setString(8, assessment.getCurrency());
            ps.setString(9, assessment.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    assessment.setId(rs.getInt(1));
                }
            }
            return assessment;
        }
    }

    public void saveFees(List<AssessmentFee> fees, Connection connection) throws SQLException {
        if (fees == null || fees.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO assessment_fees (assessment_id, fee_code, description, amount, sort_order) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (AssessmentFee fee : fees) {
                ps.setInt(1, fee.getAssessmentId());
                ps.setString(2, fee.getFeeCode());
                ps.setString(3, fee.getDescription());
                ps.setBigDecimal(4, nullable(fee.getAmount()));
                ps.setInt(5, fee.getSortOrder());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public Optional<Assessment> findPendingForStudent(int studentId) {
        String sql = "SELECT a.* FROM assessments a " +
            "JOIN enrollments e ON e.id = a.enrollment_id " +
            "WHERE e.student_id = ? AND a.status IN ('PENDING','PARTIAL') " +
            "ORDER BY a.created_at DESC LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAssessment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error loading pending assessment for student {}", studentId, e);
        }
        return Optional.empty();
    }

    public Optional<Assessment> findLatestPaidForStudent(int studentId) {
        String sql = "SELECT a.* FROM assessments a " +
                "JOIN enrollments e ON e.id = a.enrollment_id " +
                "WHERE e.student_id = ? AND a.status = 'PAID' " +
                "ORDER BY a.created_at DESC LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAssessment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error loading paid assessment for student {}", studentId, e);
        }
        return Optional.empty();
    }

    public Optional<Assessment> findById(int assessmentId) {
        String sql = "SELECT * FROM assessments WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assessmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAssessment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error loading assessment {}", assessmentId, e);
        }
        return Optional.empty();
    }

    public Optional<Assessment> findById(int assessmentId, Connection connection) throws SQLException {
        String sql = "SELECT * FROM assessments WHERE id = ?";
        Connection conn = connection != null ? connection : DatabaseManager.getConnection();
        boolean shouldClose = connection == null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assessmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAssessment(rs));
                }
            }
            return Optional.empty();
        } finally {
            if (shouldClose) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.warn("Unable to close connection", e);
                }
            }
        }
    }

    public boolean updateStatus(int assessmentId, String status, Connection connection) throws SQLException {
        String sql = "UPDATE assessments SET status = ? WHERE id = ?";
        Connection conn = connection != null ? connection : DatabaseManager.getConnection();
        boolean shouldClose = connection == null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, assessmentId);
            return ps.executeUpdate() > 0;
        } finally {
            if (shouldClose) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.warn("Unable to close connection", e);
                }
            }
        }
    }

    public List<AssessmentFee> findFeesByAssessment(int assessmentId) {
        String sql = "SELECT * FROM assessment_fees WHERE assessment_id = ? ORDER BY sort_order";
        List<AssessmentFee> fees = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assessmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AssessmentFee fee = new AssessmentFee();
                    fee.setId(rs.getInt("id"));
                    fee.setAssessmentId(rs.getInt("assessment_id"));
                    fee.setFeeCode(rs.getString("fee_code"));
                    fee.setDescription(rs.getString("description"));
                    fee.setAmount(rs.getBigDecimal("amount"));
                    fee.setSortOrder(rs.getInt("sort_order"));
                    fees.add(fee);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error loading fees for assessment {}", assessmentId, e);
        }
        return fees;
    }

    private Assessment mapAssessment(ResultSet rs) throws SQLException {
        Assessment a = new Assessment();
        a.setId(rs.getInt("id"));
        a.setEnrollmentId(rs.getInt("enrollment_id"));
        a.setTotalUnits(rs.getDouble("total_units"));
        a.setTuitionFee(rs.getBigDecimal("tuition_fee"));
        a.setMiscFee(rs.getBigDecimal("misc_fee"));
        a.setLabFee(rs.getBigDecimal("lab_fee"));
        a.setOtherFee(rs.getBigDecimal("other_fee"));
        a.setTotalDue(rs.getBigDecimal("total_due"));
        a.setCurrency(rs.getString("currency"));
        a.setStatus(rs.getString("status"));
        a.setCreatedAt(rs.getString("created_at"));
        return a;
    }

    private BigDecimal nullable(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
