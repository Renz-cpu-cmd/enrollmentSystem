package util;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Migration runner backed by Flyway. Uses SQLite datasource configured via config.properties or env vars.
 */
public final class DatabaseMigrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseMigrator.class);

    private DatabaseMigrator() {
    }

    public static void runMigrations() {
        String url = Config.getEnvOrProperty("db.url", "jdbc:sqlite:enrollment.db");

        Flyway flyway = Flyway.configure()
                .dataSource(url, null, null) // SQLite uses file-based auth
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();

        var result = flyway.migrate();
        LOGGER.info("Flyway migration complete. Applied: {}", result.migrationsExecuted);
    }
}
