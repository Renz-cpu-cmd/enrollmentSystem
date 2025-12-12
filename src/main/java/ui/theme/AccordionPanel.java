package ui.theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

public class AccordionPanel extends JPanel {
    private final JPanel header;
    private final JPanel contentWrapper; // Wrapper to hold the actual content and animate height
    private final JLabel toggleIcon;
    private boolean expanded;
    private JPanel actualContent; // The panel passed by setContent

    public AccordionPanel(String title) {
        super(new BorderLayout());
        setBackground(Theme.SURFACE_COLOR);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR));

        header = new JPanel(new BorderLayout());
        header.setBackground(Theme.SURFACE_COLOR);
        header.setCursor(new Cursor(Cursor.HAND_CURSOR));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.SUBHEADING_FONT);
        header.add(titleLabel, BorderLayout.CENTER);

        toggleIcon = new JLabel("▼"); // Down arrow for collapsed, Up arrow for expanded
        toggleIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        toggleIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        header.add(toggleIcon, BorderLayout.EAST);

        contentWrapper = new JPanel(new BorderLayout()); // Use BorderLayout for contentWrapper
        contentWrapper.setBackground(Theme.BACKGROUND_COLOR); // Different background for content
        contentWrapper.setPreferredSize(new Dimension(getWidth(), 0)); // Start collapsed
        contentWrapper.setVisible(false);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Padding for content

        add(header, BorderLayout.NORTH);
        add(contentWrapper, BorderLayout.CENTER);

        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setExpanded(!expanded);
            }
        });
    }

    public void setContent(JPanel newContent) {
        this.actualContent = newContent;
        contentWrapper.removeAll();
        contentWrapper.add(actualContent, BorderLayout.CENTER);
        // Do not revalidate/repaint here, it will happen during animation
    }

    public void setExpanded(boolean expand) {
        if (this.expanded == expand) {
            return; // No change needed
        }
        this.expanded = expand;
        toggleIcon.setText(expanded ? "▲" : "▼");

        int startHeight = contentWrapper.getHeight();
        // Calculate target height by temporarily setting actual content visible and getting its preferred size
        int targetHeight = 0;
        if (expanded) {
            contentWrapper.setVisible(true);
            contentWrapper.add(actualContent, BorderLayout.CENTER); // Ensure actual content is added for preferred size calculation
            actualContent.setVisible(true); // Ensure its components are counted
            contentWrapper.revalidate();
            targetHeight = actualContent.getPreferredSize().height + (contentWrapper.getBorder().getBorderInsets(contentWrapper).top + contentWrapper.getBorder().getBorderInsets(contentWrapper).bottom);
        } else {
             actualContent.setVisible(false); // Hide actual content when collapsing
        }
        
        int animationDuration = 200; // milliseconds
        Timer timer = new Timer(10, null);
        timer.addActionListener(new AbstractAction() {
            long startTime = -1;
            final int initialHeight = startHeight;
            final int finalHeight = targetHeight;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime < 0) {
                    startTime = System.currentTimeMillis();
                    if(expanded){
                         contentWrapper.setVisible(true);
                         actualContent.setVisible(true);
                    }
                }
                long now = System.currentTimeMillis();
                long elapsed = now - startTime;
                double progress = (double) elapsed / animationDuration;

                if (progress >= 1.0) {
                    progress = 1.0;
                    contentWrapper.setPreferredSize(new Dimension(getWidth(), finalHeight));
                    contentWrapper.setVisible(expanded);
                    if (!expanded) {
                        contentWrapper.removeAll(); // Remove content when collapsed
                    }
                    ((Timer) e.getSource()).stop();
                } else {
                    int animatedHeight = initialHeight + (int) ((finalHeight - initialHeight) * progress);
                    contentWrapper.setPreferredSize(new Dimension(getWidth(), animatedHeight));
                }
                contentWrapper.revalidate();
                contentWrapper.repaint();
                revalidate();
                repaint();
                // Repaint parent to ensure layout updates propagate
                if (getParent() != null) {
                    getParent().revalidate();
                    getParent().repaint();
                }
            }
        });
        timer.setRepeats(true);
        timer.setCoalesce(true);
        timer.start();
    }

    public boolean isExpanded() {
        return expanded;
    }
}