package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LOGGER.error("An uncaught exception occurred in thread " + t.getName(), e);

        // Show a user-friendly error dialog
        // This should be run on the Event Dispatch Thread
        if (SwingUtilities.isEventDispatchThread()) {
            showErrorDialog(e);
        } else {
            SwingUtilities.invokeLater(() -> showErrorDialog(e));
        }
    }

    private void showErrorDialog(Throwable e) {
        String message = "An unexpected error occurred. Please contact support.\n\n" +
                         "Error: " + e.getMessage();
        JOptionPane.showMessageDialog(
                null,
                message,
                "Application Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
