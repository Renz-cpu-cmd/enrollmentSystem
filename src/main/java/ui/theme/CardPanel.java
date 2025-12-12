package ui.theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class CardPanel extends JPanel {

    private int cornerRadius = 15;
    private int baseShadowSize = 5;
    private int hoveredShadowSize = 10;
    private int currentShadowSize = baseShadowSize;

    private int baseOffsetY = 0;
    private int hoveredOffsetY = -5; // Move up by 5 pixels
    private int currentOffsetY = baseOffsetY;

    public CardPanel(LayoutManager layoutManager) {
        super(layoutManager);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Add padding inside the card

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                animateLift(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                animateLift(false);
            }
        });
    }

    public CardPanel() {
        this(new BorderLayout());
    }

    private void animateLift(boolean lift) {
        int targetShadow = lift ? hoveredShadowSize : baseShadowSize;
        int targetOffsetY = lift ? hoveredOffsetY : baseOffsetY;
        int animationDuration = 100; // milliseconds

        Timer timer = new Timer(10, new AbstractAction() {
            long startTime = -1;
            int startShadow = currentShadowSize;
            int startOffsetY = currentOffsetY;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime < 0) {
                    startTime = System.currentTimeMillis();
                }
                long now = System.currentTimeMillis();
                long elapsed = now - startTime;
                double progress = (double) elapsed / animationDuration;

                if (progress >= 1.0) {
                    progress = 1.0;
                    currentShadowSize = targetShadow;
                    currentOffsetY = targetOffsetY;
                    ((Timer) e.getSource()).stop();
                } else {
                    currentShadowSize = startShadow + (int) ((targetShadow - startShadow) * progress);
                    currentOffsetY = startOffsetY + (int) ((targetOffsetY - startOffsetY) * progress);
                }
                repaint();
            }
        });
        timer.setRepeats(true);
        timer.setCoalesce(true);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(cornerRadius, cornerRadius);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        // Draw shadow (adjusted for lift)
        graphics.setColor(Theme.SHADOW_COLOR);
        graphics.fillRoundRect(baseShadowSize, baseShadowSize + currentOffsetY, width - baseShadowSize, height - baseShadowSize, arcs.width, arcs.height);

        // Draws the rounded panel
        graphics.translate(0, currentOffsetY); // Apply Y-offset for lift effect
        graphics.setColor(Theme.SURFACE_COLOR);
        graphics.fillRoundRect(0, 0, width - currentShadowSize, height - currentShadowSize, arcs.width, arcs.height); //paint background
        graphics.setColor(Theme.BORDER_COLOR);
        graphics.drawRoundRect(0, 0, width - currentShadowSize - 1, height - currentShadowSize - 1, arcs.width, arcs.height); //paint border
        graphics.translate(0, -currentOffsetY); // Reset translation for children
    }

    @Override
    protected void paintChildren(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(0, currentOffsetY); // Apply Y-offset for children
        g2.setClip(new RoundRectangle2D.Double(0, 0, getWidth() - currentShadowSize, getHeight() - currentShadowSize, cornerRadius, cornerRadius));
        super.paintChildren(g2);
        g2.dispose();
    }
}