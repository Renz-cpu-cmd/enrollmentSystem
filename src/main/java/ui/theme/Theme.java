
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

    // region Color Palette (Material Design)
    public static final Color PRIMARY_COLOR = new Color(25, 118, 210);      // Material Blue 700
    public static final Color ACCENT_COLOR = new Color(255, 193, 7);       // Amber 500
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Grey 100
    public static final Color SURFACE_COLOR = new Color(255, 255, 255);    // White
    public static final Color TEXT_PRIMARY_COLOR = new Color(33, 33, 33);   // Grey 900
    public static final Color TEXT_SECONDARY_COLOR = new Color(117, 117, 117);// Grey 700
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);        // Green 500
    public static final Color DANGER_COLOR = new Color(211, 47, 47);         // Red 700
    public static final Color BORDER_COLOR = new Color(224, 224, 224);       // Grey 300
    // endregion

    // region Typography
    public static final Font HEADING_FONT = new Font("SansSerif", Font.BOLD, 20);
    public static final Font SUBHEADING_FONT = new Font("SansSerif", Font.BOLD, 16);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font BOLD_BODY_FONT = new Font("SansSerif", Font.BOLD, 14);
    public static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);
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
            // UIManager.setLookAndFeel(new FlatLightLaf());

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
