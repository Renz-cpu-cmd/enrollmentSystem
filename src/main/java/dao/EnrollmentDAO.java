package dao;

import model.Enrollment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the Enrollment model.
 * Implements the DataAccessObject interface to provide standard CRUD operations.
 */
public class EnrollmentDAO implements DataAccessObject<Enrollment, Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrollmentDAO.class);

    @Override
    public boolean add(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments (student_id, course_id, academic_year, term, status) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, enrollment.getStudentId());
                pstmt.setInt(2, enrollment.getCourseId());
                pstmt.setString(3, enrollment.getAcademicYear());
                pstmt.setString(4, enrollment.getTerm());
                pstmt.setString(5, enrollment.getStatus());
                boolean result = pstmt.executeUpdate() > 0;
                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            LOGGER.error("Error adding enrollment for student id: " + enrollment.getStudentId(), e);
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
    public boolean update(Enrollment enrollment) {
        String sql = "UPDATE enrollments SET student_id = ?, course_id = ?, academic_year = ?, term = ?, status = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, enrollment.getStudentId());
                pstmt.setInt(2, enrollment.getCourseId());
                pstmt.setString(3, enrollment.getAcademicYear());
                pstmt.setString(4, enrollment.getTerm());
                pstmt.setString(5, enrollment.getStatus());
                pstmt.setInt(6, enrollment.getId());
                boolean result = pstmt.executeUpdate() > 0;
                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating enrollment with id: " + enrollment.getId(), e);
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
        String sql = "DELETE FROM enrollments WHERE id = ?";
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
            LOGGER.error("Error deleting enrollment with id: " + id, e);
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
    public Enrollment getById(Integer id) {
        String sql = "SELECT * FROM enrollments WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractEnrollmentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving enrollment with id: " + id, e);
        }
        return null;
    }

    @Override
    public List<Enrollment> getAll() {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                enrollments.add(extractEnrollmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving all enrollments", e);
        }
        return enrollments;
    }

    private Enrollment extractEnrollmentFromResultSet(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(rs.getInt("id"));
        enrollment.setStudentId(rs.getInt("student_id"));
        enrollment.setCourseId(rs.getInt("course_id"));
        enrollment.setAcademicYear(rs.getString("academic_year"));
        enrollment.setTerm(rs.getString("term"));
        enrollment.setStatus(rs.getString("status"));
        enrollment.setCreatedAt(rs.getString("created_at"));
        return enrollment;
    }
}
