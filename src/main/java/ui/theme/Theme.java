package ui.theme;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Dashboard design system: palette, typography, and helper utilities.
 */
public class Theme {

    // region Palette
    public static final Color BG_COLOR = Color.decode("#F4F7FE");
    public static final Color SIDEBAR_BG = Color.decode("#111C44");
    public static final Color SIDEBAR_TEXT = Color.decode("#A3AED0");
    public static final Color SIDEBAR_TEXT_ACTIVE = Color.WHITE;
    public static final Color PRIMARY = Color.decode("#4318FF");
    public static final Color TEXT_HEADER = Color.decode("#2B3674");
    public static final Color TEXT_BODY = Color.decode("#A3AED0");
    public static final Color CARD_BG = Color.decode("#FFFFFF");
    public static final Color BORDER = Color.decode("#E0E5F2");
    public static final Color SUCCESS = Color.decode("#05CD99");
    public static final Color DANGER = Color.decode("#EE5D50");
    public static final Color INFO = Color.decode("#4318FF");

    // compatibility aliases for legacy names
    public static final Color TEXT_PRIMARY = TEXT_HEADER;
    public static final Color TEXT_SECONDARY = TEXT_BODY;
    public static final Color ACCENT_COLOR = PRIMARY;

    // legacy aliases for existing code
    public static final Color BACKGROUND_COLOR = BG_COLOR;
    public static final Color SURFACE_COLOR = CARD_BG;
    public static final Color PRIMARY_COLOR = PRIMARY;
    public static final Color ACCENT_HOVER_COLOR = PRIMARY.brighter();
    public static final Color ACCENT_PRESSED_COLOR = PRIMARY.darker();
    public static final Color TEXT_PRIMARY_COLOR = TEXT_HEADER;
    public static final Color TEXT_SECONDARY_COLOR = TEXT_BODY;
    public static final Color SUCCESS_COLOR = SUCCESS;
    public static final Color DANGER_COLOR = DANGER;
    public static final Color ICON_BG_GREEN = SUCCESS;
    public static final Color ICON_BG_RED = DANGER;
    public static final Color ICON_BG_BLUE = PRIMARY;
    public static final Color BORDER_COLOR = BORDER;
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 25);
    // endregion

    // region Typography
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD_BODY_FONT = BODY_FONT.deriveFont(Font.BOLD);
    public static final Font LABEL_FONT = BODY_FONT.deriveFont(Font.PLAIN, 12);
    public static final Font HEADING_FONT = HEADER_FONT;
    public static final Font SUBHEADING_FONT = SUBHEADER_FONT;
    // endregion

    // region Dimensions
    public static final int MARGIN = 24;
    public static final int MOBILE_MARGIN = MARGIN;
    public static final int MOBILE_WIDTH = 375; // legacy
    public static final int MOBILE_HEIGHT = 812; // legacy
    public static final int BUTTON_HEIGHT = 48;
    public static final int INPUT_HEIGHT = 44;
    // endregion

    // region Borders
    public static final Border LINE_BORDER = BorderFactory.createLineBorder(BORDER, 1, true);
    public static final Border PADDING_BORDER = BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN);
    // endregion

    private Theme() {
    }

    /**
     * Initializes and applies the FlatLaf theme.
     */
    public static void setupTheme() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            applyCommonTweaks();
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
            ex.printStackTrace();
        }
    }

    public static void setupTheme(boolean useDark) {
        if (useDark) {
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (Exception ex) {
                System.err.println("Failed to initialize Dark LaF, falling back to Light");
                setupTheme();
                return;
            }
            applyCommonTweaks();
        } else {
            setupTheme();
        }
    }

    private static void applyCommonTweaks() {
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("Panel.background", BG_COLOR);
        UIManager.put("Label.foreground", TEXT_HEADER);
        UIManager.put("TextField.foreground", TEXT_HEADER);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.background", PRIMARY);
        UIManager.put("ScrollBar.width", 12);
    }

    public static void styleTextField(JTextField field) {
        field.setFont(BODY_FONT);
        field.setBorder(LINE_BORDER);
        field.setBackground(CARD_BG);
        field.setForeground(TEXT_HEADER);
        field.setCaretColor(PRIMARY);
        field.setMargin(new Insets(0, 12, 0, 12));
        Dimension preferred = new Dimension(field.getPreferredSize().width, INPUT_HEIGHT);
        field.setPreferredSize(preferred);
        field.setMinimumSize(new Dimension(0, INPUT_HEIGHT));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, INPUT_HEIGHT));
    }

    public static void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
