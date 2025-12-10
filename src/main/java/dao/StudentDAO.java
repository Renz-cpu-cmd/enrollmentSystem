package dao;

import model.Student;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the Student model.
 * Implements the DataAccessObject interface to provide standard CRUD operations.
 */
public class StudentDAO implements DataAccessObject<Student> {

    /**
     * Adds a new student to the database.
     *
     * @param student The student to add.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void add(Student student) throws SQLException {
        String sql = "INSERT INTO students (student_id, first_name, last_name, contact_number, email, gender, address, course, year_level, block) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getLastName());
            pstmt.setString(4, student.getContactNumber());
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getGender());
            pstmt.setString(7, student.getAddress());
            pstmt.setString(8, student.getCourse());
            pstmt.setInt(9, student.getYearLevel());
            pstmt.setString(10, student.getBlock());
            pstmt.executeUpdate();
        }
    }

    /**
     * Updates an existing student in the database.
     *
     * @param student The student to update.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void update(Student student) throws SQLException {
        String sql = "UPDATE students SET student_id = ?, first_name = ?, last_name = ?, contact_number = ?, email = ?, gender = ?, address = ?, course = ?, year_level = ?, block = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getLastName());
            pstmt.setString(4, student.getContactNumber());
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getGender());
            pstmt.setString(7, student.getAddress());
            pstmt.setString(8, student.getCourse());
            pstmt.setInt(9, student.getYearLevel());
            pstmt.setString(10, student.getBlock());
            pstmt.setInt(11, student.getId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a student from the database by their ID.
     *
     * @param id The ID of the student to delete.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves a student from the database by their ID.
     *
     * @param id The ID of the student to retrieve.
     * @return The student with the specified ID, or null if not found.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public Student getById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Student student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setStudentId(rs.getString("student_id"));
                    student.setFirstName(rs.getString("first_name"));
                    student.setLastName(rs.getString("last_name"));
                    student.setContactNumber(rs.getString("contact_number"));
                    student.setEmail(rs.getString("email"));
                    student.setGender(rs.getString("gender"));
                    student.setAddress(rs.getString("address"));
                    student.setCourse(rs.getString("course"));
                    student.setYearLevel(rs.getInt("year_level"));
                    student.setBlock(rs.getString("block"));
                    student.setCreatedAt(rs.getString("created_at"));
                    return student;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all students from the database.
     *
     * @return A list of all students.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<Student> getAll() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setStudentId(rs.getString("student_id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setContactNumber(rs.getString("contact_number"));
                student.setEmail(rs.getString("email"));
                student.setGender(rs.getString("gender"));
                student.setAddress(rs.getString("address"));
                student.setCourse(rs.getString("course"));
                student.setYearLevel(rs.getInt("year_level"));
                student.setBlock(rs.getString("block"));
                student.setCreatedAt(rs.getString("created_at"));
                students.add(student);
            }
        }
        return students;
    }
}
