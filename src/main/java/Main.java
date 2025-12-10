
import ui.MobileFrame;
import ui.theme.Theme;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Set up the entire application's look and feel
        Theme.setupTheme();

        // Run the UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MobileFrame frame = new MobileFrame();
            frame.setVisible(true);

            // Show splash screen for a few seconds
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            frame.showScreen("PortalGateway");
        });
    }
}
