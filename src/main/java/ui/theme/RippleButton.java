package ui.theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class RippleButton extends JButton {

    private final List<RippleAnimation> ripples = new ArrayList<>();
    private int cornerRadius = 999; // For pill-shape

    public RippleButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setForeground(Color.WHITE);
        setBackground(Theme.PRIMARY_COLOR);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    ripples.add(new RippleAnimation(e.getPoint()));
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Create the button's shape
        Area buttonArea = new Area(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        // Paint background
        if (getModel().isPressed()) {
            g2.setColor(getBackground().darker());
        } else if (getModel().isRollover()) {
            g2.setColor(getBackground().brighter());
        } else {
            g2.setColor(getBackground());
        }
        g2.fill(buttonArea);

        // Clip subsequent painting to the button's area
        g2.setClip(buttonArea);

        // Paint ripples
        for (RippleAnimation ripple : new ArrayList<>(ripples)) {
            if (ripple.isFinished()) {
                ripples.remove(ripple);
            } else {
                ripple.paint(g2);
            }
        }

        // Let the superclass paint the text
        super.paintComponent(g2);

        g2.dispose();
    }

    private class RippleAnimation {
        private final Point point;
        private final Timer timer;
        private float radius = 0;
        private float alpha = 0.4f;

        RippleAnimation(Point point) {
            this.point = point;
            this.timer = new Timer(20, e -> {
                radius += 8;
                if (alpha > 0) {
                    alpha -= 0.03f;
                } else {
                    alpha = 0;
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            });
            timer.start();
        }

        boolean isFinished() {
            return !timer.isRunning();
        }

        void paint(Graphics2D g2) {
            g2.setColor(new Color(1.0f, 1.0f, 1.0f, Math.max(0, alpha)));
            int r = (int) radius;
            g2.fillOval(point.x - r, point.y - r, r * 2, r * 2);
        }
    }
}