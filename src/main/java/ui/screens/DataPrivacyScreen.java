package ui.screens;

import ui.MobileFrame;
import ui.Screen;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class DataPrivacyScreen extends JPanel {

    public DataPrivacyScreen() {
        setLayout(new BorderLayout(10, 10));
        setBorder(Theme.PADDING_BORDER);
        setBackground(Theme.BACKGROUND_COLOR);

        // Title
        JLabel titleLabel = new JLabel("Data Privacy & Eligibility", SwingConstants.CENTER);
        titleLabel.setFont(Theme.HEADING_FONT);
        titleLabel.setForeground(Theme.TEXT_PRIMARY_COLOR);
        add(titleLabel, BorderLayout.NORTH);

        // Agreement Text
        JTextArea agreementText = new JTextArea();
        agreementText.setEditable(false);
        agreementText.setLineWrap(true);
        agreementText.setWrapStyleWord(true);
        agreementText.setText("In compliance with the Data Privacy Act, we are committed to protecting your personal information. By checking the box below, you consent to the collection, use, and processing of your personal data for the purposes of this enrollment system. This includes, but is not limited to, your name, contact information, academic records, and other information necessary for the enrollment process.");
        agreementText.setFont(Theme.BODY_FONT);
        agreementText.setBackground(Theme.BACKGROUND_COLOR);
        agreementText.setForeground(Theme.TEXT_SECONDARY_COLOR);

        JScrollPane scrollPane = new JScrollPane(agreementText);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Checkboxes and Next button
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Theme.BACKGROUND_COLOR);

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setBackground(Theme.BACKGROUND_COLOR);
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

        JCheckBox agreeCheckbox = new JCheckBox("I have read and agree to the Data Privacy Act.");
        agreeCheckbox.setFont(Theme.BODY_FONT);
        agreeCheckbox.setOpaque(false);

        JCheckBox form138Checkbox = new JCheckBox("I have my Form 138 (Report Card) ready.");
        form138Checkbox.setFont(Theme.BODY_FONT);
        form138Checkbox.setOpaque(false);

        checkboxPanel.add(agreeCheckbox);
        checkboxPanel.add(form138Checkbox);

        JButton nextButton = new JButton("Start Registration");
        nextButton.setEnabled(false);

        // Listener to enable the button only when both are checked
        java.awt.event.ActionListener checkboxListener = e ->
                nextButton.setEnabled(agreeCheckbox.isSelected() && form138Checkbox.isSelected());

        agreeCheckbox.addActionListener(checkboxListener);
        form138Checkbox.addActionListener(checkboxListener);

        nextButton.addActionListener(e -> {
            MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.showScreen(Screen.BIO_DATA, true);
            }
        });

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonContainer.setBackground(Theme.BACKGROUND_COLOR);
        buttonContainer.add(nextButton);

        bottomPanel.add(checkboxPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonContainer, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}