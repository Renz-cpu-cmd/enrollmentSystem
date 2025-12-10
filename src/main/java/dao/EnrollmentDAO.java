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
public class EnrollmentDAO implements DataAccessObject<Enrollment, Integer> {

    @Override
    public boolean add(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments (student_id, course_id, academic_year, term, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setString(3, enrollment.getAcademicYear());
            pstmt.setString(4, enrollment.getTerm());
            pstmt.setString(5, enrollment.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Enrollment enrollment) {
        String sql = "UPDATE enrollments SET student_id = ?, course_id = ?, academic_year = ?, term = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setString(3, enrollment.getAcademicYear());
            pstmt.setString(4, enrollment.getTerm());
            pstmt.setString(5, enrollment.getStatus());
            pstmt.setInt(6, enrollment.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM enrollments WHERE id = ?";
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
    public Enrollment getById(Integer id) {
        String sql = "SELECT * FROM enrollments WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractEnrollmentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Enrollment> getAll() {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                enrollments.add(extractEnrollmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
