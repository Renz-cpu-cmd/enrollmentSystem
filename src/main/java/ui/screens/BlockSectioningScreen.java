
package ui.screens;

import ui.MobileFrame;

import javax.swing.*;
import java.awt.*;

public class BlockSectioningScreen extends JPanel {

    public BlockSectioningScreen() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Available Blocks for BSIT 1st Year", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(headerLabel, BorderLayout.NORTH);

        // Block List
        JPanel blockListPanel = new JPanel();
        blockListPanel.setLayout(new BoxLayout(blockListPanel, BoxLayout.Y_AXIS));
        
        // Example Block Items
        blockListPanel.add(createBlockCard("Block 1-A (Morning)", "FULL", Color.RED));
        blockListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        blockListPanel.add(createBlockCard("Block 1-B (Afternoon)", "23/45 Slots", Color.GREEN));
        blockListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        blockListPanel.add(createBlockCard("Block 1-C (Evening)", "AVAILABLE", Color.GREEN));

        JScrollPane scrollPane = new JScrollPane(blockListPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Confirm Button
        JButton confirmButton = new JButton("Confirm Selection");
        add(confirmButton, BorderLayout.SOUTH);

        confirmButton.addActionListener(e -> {
            // In a real application, this would save the selection
            // For now, it will show a confirmation and move to the next logical screen (which is not yet built)
            JOptionPane.showMessageDialog(this, "Block Section Confirmed!");
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                // Placeholder for the next screen
                frame.showScreen("AssessmentOfFees"); 
            }
        });
    }

    private JPanel createBlockCard(String title, String status, Color statusColor) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel statusLabel = new JLabel(status);
        statusLabel.setForeground(statusColor);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        card.add(titleLabel, BorderLayout.CENTER);
        card.add(statusLabel, BorderLayout.EAST);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height)); // Constrain height

        return card;
    }
}
