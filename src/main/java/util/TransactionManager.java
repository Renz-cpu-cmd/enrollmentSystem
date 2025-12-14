package util;

import dao.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

public final class TransactionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManager.class);

    private TransactionManager() {
    }

    public static <T> T executeInTransaction(Function<Connection, T> work) {
        try (Connection conn = DatabaseManager.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                T result = work.apply(conn);
                conn.commit();
                return result;
            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.error("Rollback failed", ex);
                }
                throw new RuntimeException("Transaction failed", e);
            } finally {
                try {
                    conn.setAutoCommit(originalAutoCommit);
                } catch (SQLException e) {
                    LOGGER.error("Failed to restore autoCommit", e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to obtain connection", e);
        }
    }
}
