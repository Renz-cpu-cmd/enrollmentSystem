
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A utility class for managing the SQLite database connection.
 * It also handles the initial creation of the database schema.
 */
public class DBUtil {
    private static final String URL = "jdbc:sqlite:enrollment.db";

    /**
     * Establishes a connection to the SQLite database.
     *
     * @return A Connection object to the database.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found.", e);
        }
    }

    /**
     * Initializes the database by creating the necessary tables if they do not already exist.
     * This method is idempotent and safe to call on every application startup.
     */
    public static void initDB() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS students ("
                   + "  id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "  student_id TEXT UNIQUE NOT NULL,"
                   + "  password TEXT NOT NULL,"
                   + "  last_name TEXT NOT NULL,"
                   + "  first_name TEXT NOT NULL,"
                   + "  middle_name TEXT,"
                   + "  suffix TEXT,"
                   + "  birth_date TEXT,"
                   + "  sex TEXT,"
                   + "  mobile_number TEXT,"
                   + "  email TEXT,"
                   + "  home_address TEXT,"
                   + "  guardian_name TEXT,"
                   + "  guardian_mobile TEXT,"
                   + "  last_school_attended TEXT,"
                   + "  shs_strand TEXT,"
                   + "  college TEXT,"
                   + "  program TEXT,"
                   + "  year_level INTEGER,"
                   + "  block_section TEXT"
                   + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            // Create the students table
            stmt.execute(sql);
            System.out.println("Database initialized successfully. 'students' table is ready.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
