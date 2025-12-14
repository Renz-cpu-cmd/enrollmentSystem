package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.UUID;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        String correlationId = UUID.randomUUID().toString();
        LOGGER.error("[{}] Uncaught exception in thread {}", correlationId, t.getName(), e);

        // Show a user-friendly error dialog
        // This should be run on the Event Dispatch Thread
        if (SwingUtilities.isEventDispatchThread()) {
            showErrorDialog(e, correlationId);
        } else {
            SwingUtilities.invokeLater(() -> showErrorDialog(e, correlationId));
        }
    }

    private void showErrorDialog(Throwable e, String correlationId) {
        String message = "An unexpected error occurred. Please contact support.\n" +
                "Reference ID: " + correlationId;
        JOptionPane.showMessageDialog(
                null,
                message,
                "Application Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
