
package ui.theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Toast extends JDialog {

    public enum Type {
        SUCCESS, WARNING, ERROR, INFO
    }

    public static final int LENGTH_SHORT = 2000;
    public static final int LENGTH_LONG = 3500;

    public Toast(Frame owner, String text, Type type, int duration) {
        super(owner);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setFocusableWindowState(false);
        setBackground(new Color(0, 0, 0, 0)); // Transparent background

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());

        Color bgColor;
        switch (type) {
            case SUCCESS:
                bgColor = new Color(76, 175, 80, 230); // Green
                break;
            case WARNING:
                bgColor = new Color(255, 152, 0, 230); // Amber
                break;
            case ERROR:
                bgColor = new Color(229, 115, 115, 230); // Soft Red
                break;
            case INFO:
            default:
                bgColor = new Color(33, 33, 33, 210); // Dark grey
                break;
        }
        panel.setBackground(bgColor);

        JLabel label = new JLabel("<html><div style='text-align: center;'>" + text + "</div></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        panel.add(label, new GridBagConstraints());

        add(panel);

        pack();
        Dimension dim = getPreferredSize();
        // Add padding
        setSize(new Dimension(dim.width + 40, dim.height + 20));

        // Position at bottom center
        int x = owner.getX() + (owner.getWidth() - getWidth()) / 2;
        int y = owner.getY() + owner.getHeight() - getHeight() - 50; // 50px from bottom
        setLocation(x, y);

        // Timer to hide
        Timer timer = new Timer(duration, e -> {
            setVisible(false);
            dispose();
        });
        timer.setRepeats(false);

        // Show and start timer
        setVisible(true);
        timer.start();
    }

    public static void makeText(Component parent, String text, Type type, int duration) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(parent);
        if (owner != null && owner.isShowing()) {
            new Toast(owner, text, type, duration);
        }
    }
}
