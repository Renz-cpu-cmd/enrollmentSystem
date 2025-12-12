package dao;

import model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the Student model.
 * Handles all database operations for students.
 */
public class StudentDAO implements DataAccessObject<Student, Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentDAO.class);

    @Override
    public boolean add(Student student) {
        String sql = "INSERT INTO students(student_id, password, last_name, first_name, middle_name, suffix, " +
                     "birth_date, sex, mobile_number, email, home_address, guardian_name, guardian_mobile, " +
                     "last_school_attended, shs_strand, college, program, year_level, block_section) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                mapStudentToStatement(student, pstmt);
                boolean result = pstmt.executeUpdate() > 0;
                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            LOGGER.error("Error adding student: " + student.getFirstName() + " " + student.getLastName(), e);
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
    public boolean update(Student student) {
        String sql = "UPDATE students SET student_id = ?, password = ?, last_name = ?, first_name = ?, middle_name = ?, " +
                     "suffix = ?, birth_date = ?, sex = ?, mobile_number = ?, email = ?, home_address = ?, " +
                     "guardian_name = ?, guardian_mobile = ?, last_school_attended = ?, shs_strand = ?, " +
                     "college = ?, program = ?, year_level = ?, block_section = ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                mapStudentToStatement(student, pstmt);
                pstmt.setInt(20, student.getId()); // Set the ID for the WHERE clause
                boolean result = pstmt.executeUpdate() > 0;
                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating student with id: " + student.getId(), e);
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
        String sql = "DELETE FROM students WHERE id = ?";
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
            LOGGER.error("Error deleting student with id: " + id, e);
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
    public Student getById(Integer id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving student with id: " + id, e);
        }
        return null;
    }

    @Override
    public List<Student> getAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving all students", e);
        }
        return students;
    }

    public Student getStudentByStudentId(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving student with studentId: " + studentId, e);
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
