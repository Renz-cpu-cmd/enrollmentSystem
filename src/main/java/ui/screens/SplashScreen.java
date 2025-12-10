
package ui.screens;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JPanel {

    public SplashScreen() {
        setLayout(new BorderLayout());

        // School Logo (Placeholder)
        JLabel logoLabel = new JLabel("School Logo", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(logoLabel, BorderLayout.CENTER);

        // Progress Bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        add(progressBar, BorderLayout.SOUTH);
    }
}
