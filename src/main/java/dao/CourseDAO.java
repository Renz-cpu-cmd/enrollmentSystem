package dao;

import model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the Course model.
 * Implements the DataAccessObject interface to provide standard CRUD operations.
 */
public class CourseDAO implements DataAccessObject<Course, Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseDAO.class);

    @Override
    public boolean add(Course course) {
        String sql = "INSERT INTO courses (code, name, description, units) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, course.getCode());
                pstmt.setString(2, course.getName());
                pstmt.setString(3, course.getDescription());
                pstmt.setInt(4, course.getUnits());
                boolean result = pstmt.executeUpdate() > 0;
                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            LOGGER.error("Error adding course: " + course.getName(), e);
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
    public boolean update(Course course) {
        String sql = "UPDATE courses SET code = ?, name = ?, description = ?, units = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, course.getCode());
                pstmt.setString(2, course.getName());
                pstmt.setString(3, course.getDescription());
                pstmt.setInt(4, course.getUnits());
                pstmt.setInt(5, course.getId());
                boolean result = pstmt.executeUpdate() > 0;
                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating course with id: " + course.getId(), e);
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
        String sql = "DELETE FROM courses WHERE id = ?";
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
            LOGGER.error("Error deleting course with id: " + id, e);
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
    public Course getById(Integer id) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCourseFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving course with id: " + id, e);
        }
        return null;
    }

    @Override
    public List<Course> getAll() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving all courses", e);
        }
        return courses;
    }

    private Course extractCourseFromResultSet(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("id"));
        course.setCode(rs.getString("code"));
        course.setName(rs.getString("name"));
        course.setDescription(rs.getString("description"));
        course.setUnits(rs.getInt("units"));
        return course;
    }
}
