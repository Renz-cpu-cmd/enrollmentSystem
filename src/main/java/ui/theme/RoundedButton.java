package ui.theme;

import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Rounded, borderless button with hover/press feedback for modern CTA styling.
 */
public class RoundedButton extends JButton {

    private Color fillColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color textColor;
    private Color borderColor;
    private boolean outlined;
    private boolean hovered;
    private boolean pressed;
    private final int cornerRadius = 14;

    public RoundedButton(String text, Color fillColor, Color textColor) {
        this(text, fillColor, textColor, false, fillColor);
    }

    private RoundedButton(String text, Color fillColor, Color textColor, boolean outlined, Color borderColor) {
        super(text);
        this.fillColor = fillColor;
        this.textColor = textColor;
        this.outlined = outlined;
        this.borderColor = borderColor;
        this.hoverColor = outlined ? Theme.BACKGROUND_COLOR : adjustBrightness(fillColor, 12);
        this.pressedColor = outlined ? adjustBrightness(fillColor, -6) : adjustBrightness(fillColor, -10);
        init();
    }

    public static RoundedButton primary(String text) {
        return new RoundedButton(text, Theme.PRIMARY_COLOR, Color.WHITE, false, Theme.PRIMARY_COLOR);
    }

    public static RoundedButton outline(String text) {
        return new RoundedButton(text, Theme.SURFACE_COLOR, Theme.PRIMARY_COLOR, true, Theme.PRIMARY_COLOR);
    }

    public static RoundedButton subtle(String text) {
        return new RoundedButton(text, Theme.SURFACE_COLOR, Theme.TEXT_SECONDARY_COLOR, true, Theme.BORDER_COLOR);
    }

    private void init() {
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Theme.SUBHEADER_FONT);
        setMargin(new Insets(0, 24, 0, 24));
        Dimension enforcedSize = new Dimension(0, Theme.BUTTON_HEIGHT);
        setPreferredSize(enforcedSize);
        setMinimumSize(new Dimension(0, Theme.BUTTON_HEIGHT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BUTTON_HEIGHT));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = resolveBackground();
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius * 2, cornerRadius * 2);

        if (outlined) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius * 2, cornerRadius * 2);
        }

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(getText());
        int textX = (width - textWidth) / 2;
        int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(resolveTextColor());
        g2.drawString(getText(), textX, textY);
        g2.dispose();
    }

    private Color resolveBackground() {
        Color base = outlined ? fillColor : fillColor;
        if (!isEnabled()) {
            return adjustBrightness(base, 25);
        }
        if (pressed) {
            return outlined ? hoverColor : pressedColor;
        }
        if (hovered) {
            return hoverColor;
        }
        return base;
    }

    private Color resolveTextColor() {
        if (!isEnabled()) {
            return adjustBrightness(textColor, 40);
        }
        return textColor;
    }

    private Color adjustBrightness(Color color, int delta) {
        int r = clamp(color.getRed() + delta);
        int g = clamp(color.getGreen() + delta);
        int b = clamp(color.getBlue() + delta);
        return new Color(r, g, b, color.getAlpha());
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
