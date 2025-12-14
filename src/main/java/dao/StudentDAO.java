package dao;

import dao.mapper.StudentMapper;
import dao.repository.StudentRepository;
import model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO implements StudentRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentDAO.class);

    @Override
    public boolean add(Student student) {
        return addWithConnection(student, null);
    }

    public boolean addWithConnection(Student student, Connection connection) {
        String sql = "INSERT INTO students(student_id, password, last_name, first_name, middle_name, suffix, " +
                "birth_date, sex, mobile_number, email, home_address, guardian_name, guardian_mobile, " +
                "last_school_attended, shs_strand, college, program, year_level, block_section) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = connection != null ? connection : DatabaseManager.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                mapStudentToStatement(student, pstmt);
                return pstmt.executeUpdate() > 0;
            } finally {
                if (connection == null) {
                    conn.close();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error adding student: " + student.getFirstName() + " " + student.getLastName(), e);
            return false;
        }
    }

    @Override
    public boolean update(Student student) {
        return updateWithConnection(student, null);
    }

    public boolean updateWithConnection(Student student, Connection connection) {
        String sql = "UPDATE students SET student_id = ?, password = ?, last_name = ?, first_name = ?, middle_name = ?, " +
                "suffix = ?, birth_date = ?, sex = ?, mobile_number = ?, email = ?, home_address = ?, " +
                "guardian_name = ?, guardian_mobile = ?, last_school_attended = ?, shs_strand = ?, " +
                "college = ?, program = ?, year_level = ?, block_section = ? WHERE id = ?";

        try {
            Connection conn = connection != null ? connection : DatabaseManager.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                mapStudentToStatement(student, pstmt);
                pstmt.setInt(20, student.getId());
                return pstmt.executeUpdate() > 0;
            } finally {
                if (connection == null) {
                    conn.close();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating student with id: " + student.getId(), e);
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {
        return deleteWithConnection(id, null);
    }

    public boolean deleteWithConnection(Integer id, Connection connection) {
        String sql = "DELETE FROM students WHERE id = ?";
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
            LOGGER.error("Error deleting student with id: " + id, e);
            return false;
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
        return getAllPaged(200, 0);
    }

    @Override
    public List<Student> getAllPaged(int limit, int offset) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT id, student_id, password, last_name, first_name, middle_name, suffix, birth_date, sex, " +
                "mobile_number, email, home_address, guardian_name, guardian_mobile, last_school_attended, shs_strand, " +
                "college, program, year_level, block_section FROM students ORDER BY id LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving students (paged)", e);
        }
        return students;
    }

    public int countAll() {
        String sql = "SELECT COUNT(1) FROM students";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Error counting students", e);
        }
        return 0;
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

    public boolean existsByStudentId(String studentId) {
        String sql = "SELECT 1 FROM students WHERE student_id = ? LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.error("Error checking existence of studentId: " + studentId, e);
            return false;
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM students WHERE email = ? LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.error("Error checking existence of email: " + email, e);
            return false;
        }
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return StudentMapper.fromResultSet(rs);
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

