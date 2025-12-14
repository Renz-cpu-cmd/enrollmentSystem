package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FormSectionPanel extends JPanel {

    private final JPanel bodyPanel;

    public FormSectionPanel(String title) {
        setLayout(new BorderLayout(0, 16));
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
            new EmptyBorder(24, 28, 24, 28)));

        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(Theme.SUBHEADER_FONT);
        headerLabel.setForeground(Theme.TEXT_HEADER);

        bodyPanel = new JPanel();
        bodyPanel.setOpaque(false);
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));

        add(headerLabel, BorderLayout.NORTH);
        add(bodyPanel, BorderLayout.CENTER);
    }

    public JPanel getBodyPanel() {
        return bodyPanel;
    }
}
