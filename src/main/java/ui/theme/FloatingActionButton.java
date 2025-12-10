
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

    private Color hoverColor;

    public FloatingActionButton(Icon icon) {
        super(icon);
        this.hoverColor = Theme.PRIMARY_COLOR.darker();

        applyStyle();
    }

    private void applyStyle() {
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBackground(Theme.PRIMARY_COLOR);

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

        // Determine the color based on the button's state
        if (getModel().isPressed()) {
            g2.setColor(Theme.PRIMARY_COLOR.darker().darker()); // Darkest for pressed
        } else if (getModel().isRollover()) {
            g2.setColor(Theme.PRIMARY_COLOR.darker()); // Darker for hover
        } else {
            g2.setColor(getBackground()); // Normal color
        }

        g2.fillOval(0, 0, getWidth(), getHeight());

        // Paint the icon in the center
        Icon icon = getIcon();
        if (icon != null) {
            int x = (getWidth() - icon.getIconWidth()) / 2;
            int y = (getHeight() - icon.getIconHeight()) / 2;
            icon.paintIcon(this, g2, x, y);
        }

        g2.dispose();
    }
}
