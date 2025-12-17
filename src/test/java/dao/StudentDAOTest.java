package dao;

import model.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.DatabaseMigrator;

import static org.junit.jupiter.api.Assertions.*;

public class StudentDAOTest {

    private static StudentDAO dao;

    @BeforeAll
    static void setup() {
        DatabaseMigrator.runMigrations();
        dao = new StudentDAO();
    }

    @Test
    void addAndFetchStudent() {
        Student s = new Student();
        s.setStudentId("S-TEST-001");
        s.setPassword("$2a$10$dummyhashforunittest");
        s.setLastName("Test");
        s.setFirstName("User");
        s.setEmail("test.user@example.com");

        boolean added = dao.add(s);
        assertTrue(added, "Insert should succeed");
        assertTrue(s.getId() > 0, "Inserted row should have generated id");

        Student fetched = dao.getById(s.getId());
        assertNotNull(fetched, "Should fetch inserted student");
        assertEquals("S-TEST-001", fetched.getStudentId());
        assertEquals("Test", fetched.getLastName());
        assertEquals("User", fetched.getFirstName());

        assertTrue(dao.existsByStudentId("S-TEST-001"));
        assertTrue(dao.countAll() >= 1);
    }
}
