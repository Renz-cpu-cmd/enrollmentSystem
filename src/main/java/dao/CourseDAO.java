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
        return addWithConnection(course, null);
    }

    public boolean addWithConnection(Course course, Connection connection) {
        String sql = "INSERT INTO courses (code, name, description, units) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = connection != null ? connection : DatabaseManager.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, course.getCode());
                pstmt.setString(2, course.getName());
                pstmt.setString(3, course.getDescription());
                pstmt.setInt(4, course.getUnits());
                return pstmt.executeUpdate() > 0;
            } finally {
                if (connection == null) {
                    conn.close();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error adding course: " + course.getName(), e);
            return false;
        }
    }

    @Override
    public boolean update(Course course) {
        return updateWithConnection(course, null);
    }

    public boolean updateWithConnection(Course course, Connection connection) {
        String sql = "UPDATE courses SET code = ?, name = ?, description = ?, units = ? WHERE id = ?";
        try {
            Connection conn = connection != null ? connection : DatabaseManager.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, course.getCode());
                pstmt.setString(2, course.getName());
                pstmt.setString(3, course.getDescription());
                pstmt.setInt(4, course.getUnits());
                pstmt.setInt(5, course.getId());
                return pstmt.executeUpdate() > 0;
            } finally {
                if (connection == null) {
                    conn.close();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating course with id: " + course.getId(), e);
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {
        return deleteWithConnection(id, null);
    }

    public boolean deleteWithConnection(Integer id, Connection connection) {
        String sql = "DELETE FROM courses WHERE id = ?";
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
            LOGGER.error("Error deleting course with id: " + id, e);
            return false;
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
        return getAllPaged(200, 0);
    }

    public List<Course> getAllPaged(int limit, int offset) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT id, code, name, description, units FROM courses ORDER BY id LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(extractCourseFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving courses (paged)", e);
        }
        return courses;
    }

    public int countAll() {
        String sql = "SELECT COUNT(1) FROM courses";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Error counting courses", e);
        }
        return 0;
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
