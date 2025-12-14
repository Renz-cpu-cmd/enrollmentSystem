package dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import util.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String URL = Config.getEnvOrProperty("db.url", "jdbc:sqlite:enrollment.db");
    private static final String USER = Config.getEnvOrProperty("db.username", "");
    private static final String PASSWORD = Config.getEnvOrProperty("db.password", "");
    private static final boolean SQLITE = URL.toLowerCase().startsWith("jdbc:sqlite");

    private static final HikariDataSource dataSource;

    static {
        if (SQLITE) {
            dataSource = null; // use direct connections; pooling does not help SQLite
        } else {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(URL);
            if (!USER.isBlank()) {
                config.setUsername(USER);
            }
            if (!PASSWORD.isBlank()) {
                config.setPassword(PASSWORD);
            }
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.setConnectionTestQuery("SELECT 1");
            config.setPoolName("EnrollmentSystemPool");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(600000); // 10 minutes
            config.setConnectionTimeout(30000); // 30 seconds

            dataSource = new HikariDataSource(config);
        }
    }

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        if (SQLITE) {
            return DriverManager.getConnection(URL);
        }
        return dataSource.getConnection();
    }

    public static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
