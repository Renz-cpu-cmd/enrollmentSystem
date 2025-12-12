
package ui.theme;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * A centralized class for defining the application's visual theme, inspired by Material Design.
 * Encapsulates colors, fonts, and borders to ensure a consistent and modern look and feel.
 */
public class Theme {

    // region Color Palette (Refined Material Design)
    public static final Color PRIMARY_COLOR = new Color(2, 136, 209);      // A brighter, more modern blue
    public static final Color ACCENT_COLOR = new Color(255, 179, 0);       // A warmer, more inviting amber
    public static final Color ACCENT_HOVER_COLOR = new Color(255, 204, 51); // Lighter amber for hover
    public static final Color ACCENT_PRESSED_COLOR = new Color(255, 160, 0);  // Darker amber for pressed
    public static final Color BACKGROUND_COLOR = new Color(248, 249, 250); // A very light grey, almost white
    public static final Color SURFACE_COLOR = new Color(255, 255, 255);    // Pure White
    public static final Color TEXT_PRIMARY_COLOR = new Color(26, 26, 26);   // Darker for better contrast
    public static final Color TEXT_SECONDARY_COLOR = new Color(108, 117, 125);// A muted grey for secondary text
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);        // A modern, friendly green
    public static final Color DANGER_COLOR = new Color(220, 53, 69);         // A clear, attention-grabbing red
    public static final Color BORDER_COLOR = new Color(233, 236, 239);       // A subtle border color
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 25);         // A very light, transparent black for shadows
    // endregion

    // region Typography
    public static final Font HEADING_FONT = new Font("SansSerif", Font.BOLD, 22);
    public static final Font SUBHEADING_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 15);
    public static final Font BOLD_BODY_FONT = new Font("SansSerif", Font.BOLD, 15);
    public static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 13);
    // endregion

    // region Borders
    public static final Border LINE_BORDER = BorderFactory.createLineBorder(BORDER_COLOR, 1, true);
    public static final Border PADDING_BORDER = BorderFactory.createEmptyBorder(15, 15, 15, 15);
    // endregion

    /**
     * Initializes and applies the global FlatLaf theme and custom UI settings.
     * This is the single entry point for theming the entire application.
     */
    public static void setupTheme() {
        try {
            // 1. Set the FlatLaf look and feel
            UIManager.setLookAndFeel(new FlatLightLaf());

            // 2. Apply global rounding to components
            UIManager.put("Component.arc", 8);       // General rounding for panels, etc.
            UIManager.put("Button.arc", 999);          // Pill-shaped buttons
            UIManager.put("TextComponent.arc", 8);   // Rounded text fields
            UIManager.put("ProgressBar.arc", 999);

            // 3. Set global colors and fonts
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("Label.foreground", TEXT_PRIMARY_COLOR);
            UIManager.put("TextField.foreground", TEXT_PRIMARY_COLOR);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.background", PRIMARY_COLOR);

        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
            ex.printStackTrace();
        }
    }
}
