
package ui.theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CardPanel extends JPanel {

    private int cornerRadius = 15;

    public CardPanel(LayoutManager layoutManager) {
        super(layoutManager);
        setOpaque(false);
        setBorder(new EmptyBorder(15, 15, 15, 15)); // Add padding inside the card
    }

    public CardPanel() {
        this(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(cornerRadius, cornerRadius);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draws the rounded panel with shadow
        graphics.setColor(Color.WHITE);
        graphics.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height); //paint background
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height); //paint border
    }

    @Override
    protected void paintChildren(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
        super.paintChildren(g2);
        g2.dispose();
    }
}
