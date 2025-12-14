package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Clickable option card used for status selection screens.
 */
public class SelectionCard extends JButton {

    private final JPanel iconCircle;
    private boolean active;

    public SelectionCard(String title, String description, Color iconColor) {
        super();
        setLayout(new BorderLayout(16, 0));
        setBackground(Theme.CARD_BG);
        setOpaque(true);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(SwingConstants.LEFT);
        setBorder(buildBorder(Theme.BORDER_COLOR));

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.SUBHEADER_FONT);
        titleLabel.setForeground(Theme.TEXT_HEADER);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(Theme.BODY_FONT);
        descriptionLabel.setForeground(Theme.TEXT_BODY);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textStack.add(titleLabel);
        textStack.add(Box.createVerticalStrut(6));
        textStack.add(descriptionLabel);

        iconCircle = createIconCircle(iconColor);

        add(textStack, BorderLayout.CENTER);
        add(iconCircle, BorderLayout.EAST);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!active) {
                    setBorder(buildBorder(Theme.PRIMARY));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!active) {
                    setBorder(buildBorder(Theme.BORDER_COLOR));
                }
            }
        });
    }

    private JPanel createIconCircle(Color color) {
        JPanel circle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(40, 40));
        return circle;
    }

    private Border buildBorder(Color color) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2, true),
            new EmptyBorder(20, 20, 20, 20)
        );
    }

    public void setSelectedState(boolean selected) {
        this.active = selected;
        setBorder(buildBorder(selected ? Theme.PRIMARY : Theme.BORDER_COLOR));
        setBackground(selected ? Theme.CARD_BG.brighter() : Theme.CARD_BG);
    }

    public boolean isActive() {
        return active;
    }
}
