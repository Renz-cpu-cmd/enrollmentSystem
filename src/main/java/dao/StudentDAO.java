package dao;

/**
 * Data Access Object for `Student` records.
 *
 * <p>Implements repository-style CRUD and lookup operations and centralizes
 * SQL access via `DatabaseManager`. This class demonstrates polymorphism by
 * adhering to the `StudentRepository` contract, allowing callers to treat
 * different repository implementations uniformly. It also participates in the
 * layered architecture (UI → service → DAO → DB).</p>
 */

import dao.mapper.StudentMapper;
import dao.repository.StudentRepository;
import dao.DatabaseManager;
import model.Student;
import model.Student.StudentType;
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
        try {
            return DatabaseManager.runInTransaction(conn -> addInternal(student, conn));
        } catch (SQLException e) {
            LOGGER.error("Error adding student: {} {}", student.getFirstName(), student.getLastName(), e);
            return false;
        }
    }

    public boolean addWithConnection(Student student, Connection connection) {
        Connection conn = resolveConnection(connection);
        boolean shouldClose = connection == null;
        try {
            return addInternal(student, conn);
        } catch (SQLException e) {
            LOGGER.error("Error adding student: {} {}", student.getFirstName(), student.getLastName(), e);
            return false;
        } finally {
            closeIfNecessary(conn, shouldClose);
        }
    }

    @Override
    public boolean update(Student student) {
        try {
            return DatabaseManager.runInTransaction(conn -> updateInternal(student, conn));
        } catch (SQLException e) {
            LOGGER.error("Error updating student with id: {}", student.getId(), e);
            return false;
        }
    }

    public boolean updateWithConnection(Student student, Connection connection) {
        Connection conn = resolveConnection(connection);
        boolean shouldClose = connection == null;
        try {
            return updateInternal(student, conn);
        } catch (SQLException e) {
            LOGGER.error("Error updating student with id: {}", student.getId(), e);
            return false;
        } finally {
            closeIfNecessary(conn, shouldClose);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try {
            return DatabaseManager.runInTransaction(conn -> deleteInternal(id, conn));
        } catch (SQLException e) {
            LOGGER.error("Error deleting student with id: {}", id, e);
            return false;
        }
    }

    public boolean deleteWithConnection(Integer id, Connection connection) {
        Connection conn = resolveConnection(connection);
        boolean shouldClose = connection == null;
        try {
            return deleteInternal(id, conn);
        } catch (SQLException e) {
            LOGGER.error("Error deleting student with id: {}", id, e);
            return false;
        } finally {
            closeIfNecessary(conn, shouldClose);
        }
    }

    @Override
    public Student getById(Integer id) {
        return getByIdWithConnection(id, null);
    }

    public Student getByIdWithConnection(Integer id, Connection connection) {
        String sql = "SELECT * FROM students WHERE id = ?";
        Connection conn = resolveConnection(connection);
        boolean shouldClose = connection == null;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving student with id: {}", id, e);
        } finally {
            closeIfNecessary(conn, shouldClose);
        }
        return null;
    }

    @Override
    public List<Student> getAll() {
        return getAllPaged(200, 0);
    }

    @Override
    public List<Student> getAllPaged(int limit, int offset) {
        return getAllPagedWithConnection(limit, offset, null);
    }

    public List<Student> getAllPagedWithConnection(int limit, int offset, Connection connection) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT id, student_id, password, last_name, first_name, middle_name, suffix, birth_date, sex, " +
            "mobile_number, email, home_address, guardian_name, guardian_mobile, last_school_attended, shs_strand, " +
            "college, program, year_level, block_section, student_type FROM students ORDER BY id LIMIT ? OFFSET ?";
        Connection conn = resolveConnection(connection);
        boolean shouldClose = connection == null;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving students (paged)", e);
        } finally {
            closeIfNecessary(conn, shouldClose);
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
        return getStudentByStudentIdWithConnection(studentId, null);
    }

    public Student getStudentByStudentIdWithConnection(String studentId, Connection connection) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        Connection conn = resolveConnection(connection);
        boolean shouldClose = connection == null;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving student with studentId: {}", studentId, e);
        } finally {
            closeIfNecessary(conn, shouldClose);
        }
        return null;
    }

    public boolean existsByStudentId(String studentId) {
        return existsByStudentIdWithConnection(studentId, null);
    }

    public boolean existsByStudentIdWithConnection(String studentId, Connection connection) {
        String sql = "SELECT 1 FROM students WHERE student_id = ? LIMIT 1";
        Connection conn = resolveConnection(connection);
        boolean shouldClose = connection == null;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.error("Error checking existence of studentId: {}", studentId, e);
            return false;
        } finally {
            closeIfNecessary(conn, shouldClose);
        }
    }

    public boolean existsByEmail(String email) {
        return existsByEmailWithConnection(email, null);
    }

    public boolean existsByEmailWithConnection(String email, Connection connection) {
        String sql = "SELECT 1 FROM students WHERE email = ? LIMIT 1";
        Connection conn = resolveConnection(connection);
        boolean shouldClose = connection == null;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.error("Error checking existence of email: {}", email, e);
            return false;
        } finally {
            closeIfNecessary(conn, shouldClose);
        }
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = StudentMapper.fromResultSet(rs);
        String type = rs.getString("student_type");
        StudentType resolvedType = StudentType.REGULAR;
        if (type != null && !type.isBlank()) {
            try {
                resolvedType = StudentType.valueOf(type.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                resolvedType = StudentType.REGULAR;
            }
        }
        student.setStudentType(resolvedType);
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
        pstmt.setString(20, (student.getStudentType() != null ? student.getStudentType() : StudentType.REGULAR).name());
    }

    private Connection resolveConnection(Connection connection) {
        if (connection != null) {
            return connection;
        }
        try {
            return DatabaseManager.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to acquire connection", e);
        }
    }

    private void closeIfNecessary(Connection connection, boolean shouldClose) {
        if (!shouldClose || connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.warn("Unable to close connection", e);
        }
    }

    private boolean addInternal(Student student, Connection conn) throws SQLException {
        String sql = "INSERT INTO students(student_id, password, last_name, first_name, middle_name, suffix, " +
            "birth_date, sex, mobile_number, email, home_address, guardian_name, guardian_mobile, " +
            "last_school_attended, shs_strand, college, program, year_level, block_section, student_type) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            mapStudentToStatement(student, pstmt);
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        student.setId(keys.getInt(1));
                    }
                }
            }
            return affected > 0;
        }
    }

    private boolean updateInternal(Student student, Connection conn) throws SQLException {
        String sql = "UPDATE students SET student_id = ?, password = ?, last_name = ?, first_name = ?, middle_name = ?, " +
            "suffix = ?, birth_date = ?, sex = ?, mobile_number = ?, email = ?, home_address = ?, " +
            "guardian_name = ?, guardian_mobile = ?, last_school_attended = ?, shs_strand = ?, " +
            "college = ?, program = ?, year_level = ?, block_section = ?, student_type = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            mapStudentToStatement(student, pstmt);
            pstmt.setString(20, (student.getStudentType() != null ? student.getStudentType() : StudentType.REGULAR).name());
            pstmt.setInt(21, student.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    private boolean deleteInternal(Integer id, Connection conn) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}

