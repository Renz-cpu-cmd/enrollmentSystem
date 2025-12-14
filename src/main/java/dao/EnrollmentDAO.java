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
        return addWithConnection(enrollment, null);
    }

    public boolean addWithConnection(Enrollment enrollment, Connection connection) {
        String sql = "INSERT INTO enrollments (student_id, course_id, academic_year, term, status) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = connection != null ? connection : DatabaseManager.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, enrollment.getStudentId());
                pstmt.setInt(2, enrollment.getCourseId());
                pstmt.setString(3, enrollment.getAcademicYear());
                pstmt.setString(4, enrollment.getTerm());
                pstmt.setString(5, enrollment.getStatus());
                return pstmt.executeUpdate() > 0;
            } finally {
                if (connection == null) {
                    conn.close();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error adding enrollment for student id: " + enrollment.getStudentId(), e);
            return false;
        }
    }

    @Override
    public boolean update(Enrollment enrollment) {
        return updateWithConnection(enrollment, null);
    }

    public boolean updateWithConnection(Enrollment enrollment, Connection connection) {
        String sql = "UPDATE enrollments SET student_id = ?, course_id = ?, academic_year = ?, term = ?, status = ? WHERE id = ?";
        try {
            Connection conn = connection != null ? connection : DatabaseManager.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, enrollment.getStudentId());
                pstmt.setInt(2, enrollment.getCourseId());
                pstmt.setString(3, enrollment.getAcademicYear());
                pstmt.setString(4, enrollment.getTerm());
                pstmt.setString(5, enrollment.getStatus());
                pstmt.setInt(6, enrollment.getId());
                return pstmt.executeUpdate() > 0;
            } finally {
                if (connection == null) {
                    conn.close();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating enrollment with id: " + enrollment.getId(), e);
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {
        return deleteWithConnection(id, null);
    }

    public boolean deleteWithConnection(Integer id, Connection connection) {
        String sql = "DELETE FROM enrollments WHERE id = ?";
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
            LOGGER.error("Error deleting enrollment with id: " + id, e);
            return false;
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
        return getAllPaged(200, 0);
    }

    public List<Enrollment> getAllPaged(int limit, int offset) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT id, student_id, course_id, academic_year, term, status, created_at " +
                "FROM enrollments ORDER BY id LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(extractEnrollmentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving enrollments (paged)", e);
        }
        return enrollments;
    }

    public int countAll() {
        String sql = "SELECT COUNT(1) FROM enrollments";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Error counting enrollments", e);
        }
        return 0;
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
