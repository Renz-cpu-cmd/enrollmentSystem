import ui.MobileFrame;
import ui.Screen;
import ui.theme.Theme;
import util.GlobalExceptionHandler;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Set the global exception handler
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
        
        // Set up the entire application's look and feel
        Theme.setupTheme();

        // Run the UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MobileFrame frame = new MobileFrame();
            frame.setVisible(true);

            // Use a Timer to switch from the splash screen
            Timer timer = new Timer(3000, e -> {
                frame.showScreen(Screen.PORTAL_GATEWAY, true);
            });
            timer.setRepeats(false); // Only run once
            timer.start();
        });
    }
}
