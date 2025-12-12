package dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import util.Config;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Config.getProperty("db.url"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        // Recommended settings for SQLite
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("EnrollmentSystemPool");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(600000); // 10 minutes
        config.setConnectionTimeout(30000); // 30 seconds

        dataSource = new HikariDataSource(config);
    }

    private DatabaseManager() {
        // private constructor to prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
