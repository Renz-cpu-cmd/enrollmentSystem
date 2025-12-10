package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:enrollment.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void createTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createStudentsTable = "CREATE TABLE IF NOT EXISTS students ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "student_id TEXT NOT NULL UNIQUE, "
                    + "name TEXT NOT NULL, "
                    + "email TEXT NOT NULL UNIQUE, "
                    + "phone_number TEXT, "
                    + "address TEXT);";

            String createCoursesTable = "CREATE TABLE IF NOT EXISTS courses ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "code TEXT NOT NULL UNIQUE, "
                    + "name TEXT NOT NULL, "
                    + "description TEXT, "
                    + "units INTEGER NOT NULL);";

            String createEnrollmentsTable = "CREATE TABLE IF NOT EXISTS enrollments ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "student_id INTEGER NOT NULL, "
                    + "course_id INTEGER NOT NULL, "
                    + "academic_year TEXT NOT NULL, "
                    + "term TEXT NOT NULL, "
                    + "status TEXT NOT NULL, "
                    + "FOREIGN KEY (student_id) REFERENCES students(id), "
                    + "FOREIGN KEY (course_id) REFERENCES courses(id));";

            String createMessagesTable = "CREATE TABLE IF NOT EXISTS messages ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "session_id TEXT NOT NULL, "
                    + "sender TEXT NOT NULL, "
                    + "message TEXT NOT NULL, "
                    + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";

            stmt.execute(createStudentsTable);
            stmt.execute(createCoursesTable);
            stmt.execute(createEnrollmentsTable);
            stmt.execute(createMessagesTable);

        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }
}
