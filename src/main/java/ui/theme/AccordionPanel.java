
package ui.theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AccordionPanel extends JPanel {
    private final JPanel header;
    private final JPanel content;
    private final JLabel toggleIcon;
    private boolean expanded;

    public AccordionPanel(String title) {
        super(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel("  " + title);
        titleLabel.setFont(Theme.SUBHEADING_FONT);
        header.add(titleLabel, BorderLayout.CENTER);

        toggleIcon = new JLabel("+"); // Use a proper icon in a real app
        toggleIcon.setFont(new Font("Segoe UI", Font.BOLD, 20));
        toggleIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        header.add(toggleIcon, BorderLayout.EAST);

        content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setVisible(false);

        add(header, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);

        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setExpanded(!expanded);
            }
        });
    }

    public void setContent(JPanel newContent) {
        content.removeAll();
        content.add(newContent, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        content.setVisible(expanded);
        toggleIcon.setText(expanded ? "-" : "+");
        revalidate();
        if (getParent() != null) {
            getParent().revalidate();
        }
    }

    public boolean isExpanded() {
        return expanded;
    }
}
