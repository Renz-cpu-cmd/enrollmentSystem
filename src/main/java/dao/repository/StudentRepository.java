package dao.repository;

import dao.DataAccessObject;
import model.Student;

import java.util.List;

public interface StudentRepository extends DataAccessObject<Student, Integer> {

    Student getStudentByStudentId(String studentId);

    boolean existsByStudentId(String studentId);

    boolean existsByEmail(String email);

    List<Student> getAllPaged(int limit, int offset);

    int countAll();
}