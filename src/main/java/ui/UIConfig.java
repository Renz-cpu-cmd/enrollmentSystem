package ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Centralized UI configuration that applies the FlatLaf look & feel
 * plus premium overrides (fonts, radii, accent colors) before any
 * Swing components are created.
 */
public final class UIConfig {

    private static final Color ACCENT_PRIMARY = new Color(12, 92, 177); // Enterprise blue
    private static final Color ACCENT_PRIMARY_HOVER = ACCENT_PRIMARY.brighter();
    private static final Color ACCENT_PRIMARY_PRESSED = ACCENT_PRIMARY.darker();
    private static final int GLOBAL_ARC = 18;
    private static final int FONT_SIZE = 14;
    private static final String[] PREFERRED_FONTS = {"Inter", "Segoe UI", "Helvetica Neue", "Roboto", "SansSerif"};

    private UIConfig() {
    }

    public static void initialize(boolean darkMode) {
        installLookAndFeel(darkMode);
        applyPremiumDefaults();
    }

    private static void installLookAndFeel(boolean darkMode) {
        try {
            if (darkMode) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to initialize FlatLaf", ex);
        }
    }

    private static void applyPremiumDefaults() {
        FontUIResource uiFont = resolveUiFont();
        UIManager.put("defaultFont", uiFont);
        UIManager.put("Button.font", uiFont);
        UIManager.put("Label.font", uiFont);
        UIManager.put("TextComponent.font", uiFont);

        UIManager.put("Component.arc", GLOBAL_ARC);
        UIManager.put("Button.arc", GLOBAL_ARC);
        UIManager.put("TextComponent.arc", GLOBAL_ARC);
        UIManager.put("ScrollBar.thumbArc", GLOBAL_ARC);
        UIManager.put("ScrollBar.trackArc", GLOBAL_ARC);
        UIManager.put("ProgressBar.arc", GLOBAL_ARC);
        UIManager.put("Component.focusWidth", 2);
        UIManager.put("Component.innerFocusWidth", 1);

        UIManager.put("Component.focusColor", ACCENT_PRIMARY);
        UIManager.put("Button.background", ACCENT_PRIMARY);
        UIManager.put("Button.hoverBackground", ACCENT_PRIMARY_HOVER);
        UIManager.put("Button.pressedBackground", ACCENT_PRIMARY_PRESSED);
        UIManager.put("ToggleButton.selectedBackground", ACCENT_PRIMARY);
        UIManager.put("ToggleButton.hoverBackground", ACCENT_PRIMARY_HOVER);
        UIManager.put("Component.accentColor", ACCENT_PRIMARY);
    }

    private static FontUIResource resolveUiFont() {
        Set<String> installed = new HashSet<>(Arrays.asList(GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames()));

        for (String family : PREFERRED_FONTS) {
            if (installed.contains(family)) {
                return new FontUIResource(new Font(family, Font.PLAIN, FONT_SIZE));
            }
        }
        return new FontUIResource(new Font("SansSerif", Font.PLAIN, FONT_SIZE));
    }
}
