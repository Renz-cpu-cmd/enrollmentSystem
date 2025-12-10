package dao;

import model.Course;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the Course model.
 * Implements the DataAccessObject interface to provide standard CRUD operations.
 */
public class CourseDAO implements DataAccessObject<Course> {

    /**
     * Adds a new course to the database.
     *
     * @param course The course to add.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void add(Course course) throws SQLException {
        String sql = "INSERT INTO courses (code, name, description, units) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getCode());
            pstmt.setString(2, course.getName());
            pstmt.setString(3, course.getDescription());
            pstmt.setInt(4, course.getUnits());
            pstmt.executeUpdate();
        }
    }

    /**
     * Updates an existing course in the database.
     *
     * @param course The course to update.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void update(Course course) throws SQLException {
        String sql = "UPDATE courses SET code = ?, name = ?, description = ?, units = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getCode());
            pstmt.setString(2, course.getName());
            pstmt.setString(3, course.getDescription());
            pstmt.setInt(4, course.getUnits());
            pstmt.setInt(5, course.getId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a course from the database by its ID.
     *
     * @param id The ID of the course to delete.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves a course from the database by its ID.
     *
     * @param id The ID of the course to retrieve.
     * @return The course with the specified ID, or null if not found.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public Course getById(int id) throws SQLException {
        String sql = "SELECT * FROM courses WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Course course = new Course();
                    course.setId(rs.getInt("id"));
                    course.setCode(rs.getString("code"));
                    course.setName(rs.getString("name"));
                    course.setDescription(rs.getString("description"));
                    course.setUnits(rs.getInt("units"));
                    return course;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all courses from the database.
     *
     * @return A list of all courses.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<Course> getAll() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setCode(rs.getString("code"));
                course.setName(rs.getString("name"));
                course.setDescription(rs.getString("description"));
                course.setUnits(rs.getInt("units"));
                courses.add(course);
            }
        }
        return courses;
    }
}
