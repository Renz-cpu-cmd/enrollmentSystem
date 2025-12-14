package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class ModernStatCard extends JPanel {

    private final JLabel valueLabel;
    private final JPanel statusDot;

    public ModernStatCard(String title, String value, Color iconColor) {
        setLayout(new BorderLayout(16, 0));
        setBackground(Theme.CARD_BG);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        setPreferredSize(new Dimension(200, 120));

        JPanel iconWrapper = createIconCircle(iconColor, title.substring(0, 1));
        add(iconWrapper, BorderLayout.WEST);

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.LABEL_FONT);
        titleLabel.setForeground(Theme.TEXT_BODY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel = new JLabel(value);
        valueLabel.setFont(Theme.HEADING_FONT.deriveFont(Font.BOLD, 20f));
        valueLabel.setForeground(Theme.TEXT_HEADER);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textStack.add(titleLabel);
        textStack.add(Box.createVerticalStrut(8));
        textStack.add(valueLabel);

        add(textStack, BorderLayout.CENTER);

        statusDot = new JPanel();
        statusDot.setOpaque(true);
        statusDot.setPreferredSize(new Dimension(14, 14));
        statusDot.setMaximumSize(new Dimension(14, 14));
        statusDot.setMinimumSize(new Dimension(14, 14));
        statusDot.setBackground(Theme.SUCCESS_COLOR);
        statusDot.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 30), 1, true));
        add(statusDot, BorderLayout.EAST);

        setStatus(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 0, 0, 15));
        g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 24, 24);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
        g2.dispose();
        super.paintComponent(g);
    }
    private JPanel createIconCircle(Color color, String letter) {
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
        circle.setPreferredSize(new Dimension(48, 48));

        JLabel initial = new JLabel(letter, SwingConstants.CENTER);
        initial.setFont(Theme.SUBHEADING_FONT);
        initial.setForeground(Color.WHITE);
        initial.setAlignmentX(Component.CENTER_ALIGNMENT);
        initial.setAlignmentY(Component.CENTER_ALIGNMENT);
        circle.setLayout(new BorderLayout());
        circle.add(initial, BorderLayout.CENTER);
        return circle;
    }

    public void setStatus(boolean isGood) {
        statusDot.setBackground(isGood ? Theme.SUCCESS_COLOR : Theme.DANGER_COLOR);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
