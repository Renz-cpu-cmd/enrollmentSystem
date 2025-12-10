
package dao;

import model.Student;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the Student model.
 * Handles all database operations for students.
 */
public class StudentDAO implements DataAccessObject<Student, Integer> {

    @Override
    public boolean add(Student student) {
        String sql = "INSERT INTO students(student_id, password, last_name, first_name, middle_name, suffix, " +
                     "birth_date, sex, mobile_number, email, home_address, guardian_name, guardian_mobile, " +
                     "last_school_attended, shs_strand, college, program, year_level, block_section) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            mapStudentToStatement(student, pstmt);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Student student) {
        String sql = "UPDATE students SET student_id = ?, password = ?, last_name = ?, first_name = ?, middle_name = ?, " +
                     "suffix = ?, birth_date = ?, sex = ?, mobile_number = ?, email = ?, home_address = ?, " +
                     "guardian_name = ?, guardian_mobile = ?, last_school_attended = ?, shs_strand = ?, " +
                     "college = ?, program = ?, year_level = ?, block_section = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            mapStudentToStatement(student, pstmt);
            pstmt.setInt(20, student.getId()); // Set the ID for the WHERE clause

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM students WHERE id = ?";
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
    public Student getById(Integer id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Student> getAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public Student getStudentByStudentId(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setStudentId(rs.getString("student_id"));
        student.setPassword(rs.getString("password"));
        student.setLastName(rs.getString("last_name"));
        student.setFirstName(rs.getString("first_name"));
        student.setMiddleName(rs.getString("middle_name"));
        student.setSuffix(rs.getString("suffix"));
        student.setBirthDate(rs.getString("birth_date"));
        student.setSex(rs.getString("sex"));
        student.setMobileNumber(rs.getString("mobile_number"));
        student.setEmail(rs.getString("email"));
        student.setHomeAddress(rs.getString("home_address"));
        student.setGuardianName(rs.getString("guardian_name"));
        student.setGuardianMobile(rs.getString("guardian_mobile"));
        student.setLastSchoolAttended(rs.getString("last_school_attended"));
        student.setShsStrand(rs.getString("shs_strand"));
        student.setCollege(rs.getString("college"));
        student.setProgram(rs.getString("program"));
        student.setYearLevel(rs.getInt("year_level"));
        student.setBlockSection(rs.getString("block_section"));
        return student;
    }

    private void mapStudentToStatement(Student student, PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, student.getStudentId());
        pstmt.setString(2, student.getPassword());
        pstmt.setString(3, student.getLastName());
        pstmt.setString(4, student.getFirstName());
        pstmt.setString(5, student.getMiddleName());
        pstmt.setString(6, student.getSuffix());
        pstmt.setString(7, student.getBirthDate());
        pstmt.setString(8, student.getSex());
        pstmt.setString(9, student.getMobileNumber());
        pstmt.setString(10, student.getEmail());
        pstmt.setString(11, student.getHomeAddress());
        pstmt.setString(12, student.getGuardianName());
        pstmt.setString(13, student.getGuardianMobile());
        pstmt.setString(14, student.getLastSchoolAttended());
        pstmt.setString(15, student.getShsStrand());
        pstmt.setString(16, student.getCollege());
        pstmt.setString(17, student.getProgram());
        pstmt.setInt(18, student.getYearLevel());
        pstmt.setString(19, student.getBlockSection());
    }
}
