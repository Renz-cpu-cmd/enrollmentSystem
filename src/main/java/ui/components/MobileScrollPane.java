package ui.components;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Mobile-friendly scroll pane enforcing phone width and hidden scrollbars.
 */
public class MobileScrollPane extends JScrollPane {

    public MobileScrollPane(Component view) {
        super(view);
        init();
    }

    public MobileScrollPane() {
        super();
        init();
    }

    private void init() {
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        setBorder(null);
        getVerticalScrollBar().setUnitIncrement(16);
        hideScrollBar(getVerticalScrollBar());
        hideScrollBar(getHorizontalScrollBar());
        setOpaque(false);
        getViewport().setOpaque(false);
    }

    private void hideScrollBar(JScrollBar bar) {
        if (bar == null) {
            return;
        }
        bar.setPreferredSize(new Dimension(0, 0));
        bar.setUnitIncrement(16);
        bar.setOpaque(false);
        bar.setUI(new BasicScrollBarUI() {
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                // No-op: invisible track
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                // Draw a minimal thumb to hint scroll position
                g.setColor(new Color(0, 0, 0, 40));
                g.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 6, 6);
            }

            @Override
            protected Dimension getMinimumThumbSize() {
                return new Dimension(6, 32);
            }
        });
    }
}
