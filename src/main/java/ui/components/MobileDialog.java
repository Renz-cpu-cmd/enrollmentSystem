package ui.components;

import ui.theme.RoundedButton;
import ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Simple mobile-style modal overlay rendered on the frame glass pane.
 */
public final class MobileDialog {

    private MobileDialog() {
    }

    public static void show(JFrame frame, String title, String message, String type) {
        if (frame == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            JPanel overlay = new JPanel(new GridBagLayout()) {
                @Override
                public boolean isOpaque() {
                    return false;
                }

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(0, 0, 0, 150));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
            };

            overlay.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    e.consume();
                }
            });

            JPanel card = new JPanel();
            card.setOpaque(true);
            card.setBackground(Theme.CARD_BG);
            card.setBorder(new EmptyBorder(24, 32, 24, 32));
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setMaximumSize(new Dimension(320, Integer.MAX_VALUE));

            card.add(createIconBadge(type));
            card.add(Box.createVerticalStrut(16));

            JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(Theme.HEADER_FONT);
            titleLabel.setForeground(Theme.TEXT_PRIMARY);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(titleLabel);

            card.add(Box.createVerticalStrut(10));
            JLabel messageLabel = new JLabel("<html><div style='text-align:center;width:220px;'>" + message + "</div></html>");
            messageLabel.setFont(Theme.BODY_FONT);
            messageLabel.setForeground(Theme.TEXT_SECONDARY);
            messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(messageLabel);

            card.add(Box.createVerticalStrut(20));
            RoundedButton dismissButton = RoundedButton.primary("OK");
            dismissButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            dismissButton.addActionListener(e -> hideOverlay(frame));
            card.add(dismissButton);

            overlay.add(card, new GridBagConstraints());

            frame.setGlassPane(overlay);
            overlay.setVisible(true);
        });
    }

    private static JComponent createIconBadge(String type) {
        Color color = switch (type == null ? "" : type.toUpperCase()) {
            case "SUCCESS" -> Theme.SUCCESS;
            case "ERROR" -> Theme.DANGER;
            default -> Theme.PRIMARY;
        };

        return new JComponent() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(56, 56);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3f));
                g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);
                g2.dispose();
            }
        };
    }

    private static void hideOverlay(JFrame frame) {
        if (frame == null) {
            return;
        }
        Component glass = frame.getGlassPane();
        if (glass != null) {
            glass.setVisible(false);
        }
    }
}
