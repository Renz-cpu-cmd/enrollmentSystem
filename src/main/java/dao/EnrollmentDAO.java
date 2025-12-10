package dao;

import model.Enrollment;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the Enrollment model.
 * Implements the DataAccessObject interface to provide standard CRUD operations.
 */
public class EnrollmentDAO implements DataAccessObject<Enrollment> {

    /**
     * Adds a new enrollment to the database.
     *
     * @param enrollment The enrollment to add.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void add(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id, academic_year, term, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setString(3, enrollment.getAcademicYear());
            pstmt.setString(4, enrollment.getTerm());
            pstmt.setString(5, enrollment.getStatus());
            pstmt.executeUpdate();
        }
    }

    /**
     * Updates an existing enrollment in the database.
     *
     * @param enrollment The enrollment to update.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void update(Enrollment enrollment) throws SQLException {
        String sql = "UPDATE enrollments SET student_id = ?, course_id = ?, academic_year = ?, term = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setString(3, enrollment.getAcademicYear());
            pstmt.setString(4, enrollment.getTerm());
            pstmt.setString(5, enrollment.getStatus());
            pstmt.setInt(6, enrollment.getId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes an enrollment from the database by its ID.
     *
     * @param id The ID of the enrollment to delete.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves an enrollment from the database by its ID.
     *
     * @param id The ID of the enrollment to retrieve.
     * @return The enrollment with the specified ID, or null if not found.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public Enrollment getById(int id) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
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
        }
        return null;
    }

    /**
     * Retrieves all enrollments from the database.
     *
     * @return A list of all enrollments.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<Enrollment> getAll() throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setId(rs.getInt("id"));
                enrollment.setStudentId(rs.getInt("student_id"));
                enrollment.setCourseId(rs.getInt("course_id"));
                enrollment.setAcademicYear(rs.getString("academic_year"));
                enrollment.setTerm(rs.getString("term"));
                enrollment.setStatus(rs.getString("status"));
                enrollment.setCreatedAt(rs.getString("created_at"));
                enrollments.add(enrollment);
            }
        }
        return enrollments;
    }
}
