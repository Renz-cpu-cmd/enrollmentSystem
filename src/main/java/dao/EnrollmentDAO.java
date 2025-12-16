package dao;

import model.Enrollment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentDAO implements DataAccessObject<Enrollment, Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnrollmentDAO.class);

    /**
     * Inserts an enrollment inside an existing transaction.
     */
    public Enrollment create(Enrollment enrollment, Connection connection) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, block_id, term, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, enrollment.getStudentId());
            ps.setInt(2, enrollment.getBlockId());
            ps.setString(3, enrollment.getTerm());
            ps.setString(4, enrollment.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    enrollment.setId(rs.getInt(1));
                }
            }
            return enrollment;
        }
    }

    @Override
    public boolean add(Enrollment enrollment) {
        try {
            return DatabaseManager.runInTransaction(conn -> {
                create(enrollment, conn);
                return true;
            });
        } catch (SQLException e) {
            LOGGER.error("Error adding enrollment for student id: {}", enrollment.getStudentId(), e);
            return false;
        }
    }

    public boolean addWithConnection(Enrollment enrollment, Connection connection) {
        try {
            create(enrollment, connection != null ? connection : DatabaseManager.getConnection());
            return true;
        } catch (SQLException e) {
            LOGGER.error("Error adding enrollment for student id: {}", enrollment.getStudentId(), e);
            return false;
        }
    }

    @Override
    public boolean update(Enrollment enrollment) {
        return updateWithConnection(enrollment, null);
    }

    public boolean updateWithConnection(Enrollment enrollment, Connection connection) {
        String sql = "UPDATE enrollments SET student_id = ?, block_id = ?, term = ?, status = ? WHERE id = ?";
        Connection conn = connection != null ? connection : getConnectionQuietly();
        boolean shouldClose = connection == null;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getBlockId());
            pstmt.setString(3, enrollment.getTerm());
            pstmt.setString(4, enrollment.getStatus());
            pstmt.setInt(5, enrollment.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error updating enrollment with id: {}", enrollment.getId(), e);
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
        String sql = "DELETE FROM enrollments WHERE id = ?";
        Connection conn = connection != null ? connection : getConnectionQuietly();
        boolean shouldClose = connection == null;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error deleting enrollment with id: {}", id, e);
            return false;
        } finally {
            closeIfNeeded(conn, shouldClose);
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
            LOGGER.error("Error retrieving enrollment with id: {}", id, e);
        }
        return null;
    }

    @Override
    public List<Enrollment> getAll() {
        return getAllPaged(200, 0);
    }

    public List<Enrollment> getAllPaged(int limit, int offset) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT id, student_id, block_id, term, status, created_at " +
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

    public Optional<Enrollment> findActiveByStudent(Integer studentId) {
        String sql = "SELECT * FROM enrollments WHERE student_id = ? AND status IN ('ENROLLED','OFFICIALLY_ENROLLED','PENDING') ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractEnrollmentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving active enrollment for student: {}", studentId, e);
        }
        return Optional.empty();
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

    public Enrollment getById(Integer id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE id = ?";
        Connection conn = connection != null ? connection : DatabaseManager.getConnection();
        boolean shouldClose = connection == null;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractEnrollmentFromResultSet(rs);
                }
            }
            return null;
        } finally {
            closeIfNeeded(conn, shouldClose);
        }
    }

    public boolean updateStatus(Integer enrollmentId, String status, Connection connection) throws SQLException {
        String sql = "UPDATE enrollments SET status = ? WHERE id = ?";
        Connection conn = connection != null ? connection : DatabaseManager.getConnection();
        boolean shouldClose = connection == null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, enrollmentId);
            return ps.executeUpdate() > 0;
        } finally {
            closeIfNeeded(conn, shouldClose);
        }
    }

    private Enrollment extractEnrollmentFromResultSet(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(rs.getInt("id"));
        enrollment.setStudentId(rs.getInt("student_id"));
        enrollment.setBlockId(rs.getInt("block_id"));
        enrollment.setTerm(rs.getString("term"));
        enrollment.setStatus(rs.getString("status"));
        enrollment.setCreatedAt(rs.getString("created_at"));
        return enrollment;
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
