package service;

import dao.StudentDAO;
import model.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.DatabaseMigrator;

import static org.junit.jupiter.api.Assertions.*;

public class EnrollmentServiceTest {

    private static StudentDAO studentDAO;

    @BeforeAll
    static void setup() {
        // Prepare in-memory schema for tests
        DatabaseMigrator.runMigrations();
        studentDAO = new StudentDAO();
    }

    @Test
    void registerNewStudent_generatesId_andHashesPassword_whenPasswordMissing() {
        EnrollmentService svc = new EnrollmentService(studentDAO, null, null, null);

        var cmd = EnrollmentService.EnrollStudentCommand.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .mobileNumber("09171234567")
                .build();

        var result = svc.registerNewStudent(cmd);
        assertTrue(result.isSuccess(), "Expected success registering student");
        assertNotNull(result.getData(), "Expected enrollment result payload");
        Student s = result.getData().getStudent();
        assertNotNull(s.getStudentId(), "Student ID should be generated");
        assertTrue(s.getStudentId().matches("S-\\d{4}-[A-Z0-9]{6}"), "Student ID pattern");
        assertNotNull(s.getPassword(), "Password should be hashed");
        assertTrue(s.getPassword().startsWith("$2"), "Expected BCrypt hash format");
        assertTrue(result.getData().hasGeneratedPassword(), "Should include generated password when none provided");
        assertTrue(s.getId() > 0, "Saved student should have DB id");
    }

    @Test
    void registerNewStudent_failsOnMissingEmail() {
        EnrollmentService svc = new EnrollmentService(studentDAO, null, null, null);

        var cmd = EnrollmentService.EnrollStudentCommand.builder()
                .firstName("Bob")
                .lastName("Jones")
                .mobileNumber("09181234567")
                .build();

        var result = svc.registerNewStudent(cmd);
        assertFalse(result.isSuccess(), "Expected validation failure without email");
        assertTrue(result.getMessage().toLowerCase().contains("email"));
    }
}
