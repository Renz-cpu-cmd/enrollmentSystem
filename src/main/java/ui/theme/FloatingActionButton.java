
package ui.theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A custom, circular JButton designed to look like a Floating Action Button (FAB).
 * It uses advanced rendering techniques for a polished look and feel.
 */
public class FloatingActionButton extends JButton {

    private int shadowSize = 5;

    public FloatingActionButton(Icon icon) {
        super(icon);
        applyStyle();
    }

    private void applyStyle() {
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBackground(Theme.ACCENT_COLOR);

        // Set a fixed size for the circular button
        int size = 56;
        setPreferredSize(new Dimension(size, size));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw shadow
        g2.setColor(Theme.SHADOW_COLOR);
        g2.fillOval(shadowSize, shadowSize, getWidth() - shadowSize, getHeight() - shadowSize);

        // Determine the color based on the button's state
        if (getModel().isPressed()) {
            g2.setColor(Theme.ACCENT_PRESSED_COLOR);
        } else if (getModel().isRollover()) {
            g2.setColor(Theme.ACCENT_HOVER_COLOR);
        } else {
            g2.setColor(getBackground());
        }

        g2.fillOval(0, 0, getWidth() - shadowSize, getHeight() - shadowSize);

        // Paint the icon in the center
        Icon icon = getIcon();
        if (icon != null) {
            int x = (getWidth() - shadowSize - icon.getIconWidth()) / 2;
            int y = (getHeight() - shadowSize - icon.getIconHeight()) / 2;
            icon.paintIcon(this, g2, x, y);
        }

        g2.dispose();
    }
}
