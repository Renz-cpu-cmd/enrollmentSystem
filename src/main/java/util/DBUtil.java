package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A utility class for managing database connections using a HikariCP connection pool.
 */
public class DBUtil {

    private static final String DB_URL = "jdbc:sqlite:enrollment.db";
    private static final HikariDataSource ds;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(20);
        config.setConnectionTimeout(30000);
        ds = new HikariDataSource(config);

        // Initialize the database schema
        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {
            // create users table
            st.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username TEXT UNIQUE NOT NULL,"
                    + "password_hash TEXT NOT NULL,"
                    + "role TEXT NOT NULL)");

            // create students table
            st.executeUpdate("CREATE TABLE IF NOT EXISTS students ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "student_id TEXT NOT NULL UNIQUE,"
                    + "first_name TEXT NOT NULL,"
                    + "last_name TEXT NOT NULL,"
                    + "contact_number TEXT,"
                    + "email TEXT UNIQUE,"
                    + "gender TEXT,"
                    + "address TEXT,"
                    + "course TEXT,"
                    + "year_level INTEGER,"
                    + "block TEXT,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // create courses table
            st.executeUpdate("CREATE TABLE IF NOT EXISTS courses ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "code TEXT NOT NULL UNIQUE,"
                    + "name TEXT NOT NULL,"
                    + "description TEXT,"
                    + "units INTEGER)");

            // create enrollments table
            st.executeUpdate("CREATE TABLE IF NOT EXISTS enrollments ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "student_id INTEGER NOT NULL,"
                    + "course_id INTEGER NOT NULL,"
                    + "academic_year TEXT NOT NULL,"
                    + "term TEXT NOT NULL,"
                    + "status TEXT NOT NULL,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (student_id) REFERENCES students(id),"
                    + "FOREIGN KEY (course_id) REFERENCES courses(id))");

            // create payments table
            st.executeUpdate("CREATE TABLE IF NOT EXISTS payments ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "enrollment_id INTEGER NOT NULL,"
                    + "amount DECIMAL(10, 2) NOT NULL,"
                    + "payment_method TEXT,"
                    + "transaction_id TEXT,"
                    + "status TEXT NOT NULL,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (enrollment_id) REFERENCES enrollments(id))");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a connection from the connection pool.
     *
     * @return A database connection.
     * @throws SQLException If a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
