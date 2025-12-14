package ui.screens;

import ui.MobileFrame;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.theme.ProgressStepper;
import ui.theme.RoundedButton;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class DocumentsScreen extends JPanel implements ScreenView {

    private final RoundedButton nextButton;

    public DocumentsScreen() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(Theme.PADDING_BORDER);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createDocumentScrollPane(), BorderLayout.CENTER);
        nextButton = RoundedButton.primary("Next");
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ProgressStepper stepper = new ProgressStepper(ProgressStepper.STEPS);
        stepper.setCurrentStep(1);
        stepper.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(stepper);

        header.add(Box.createVerticalStrut(10));

        JLabel title = new JLabel("Document Submission", SwingConstants.CENTER);
        title.setFont(Theme.HEADING_FONT);
        title.setForeground(Theme.TEXT_PRIMARY_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(title);

        JTextArea subtitle = new JTextArea("Upload the required credentials to validate your enrollment. Originals will be verified on campus.");
        subtitle.setForeground(Theme.TEXT_SECONDARY_COLOR);
        subtitle.setFont(Theme.BODY_FONT);
        subtitle.setOpaque(false);
        subtitle.setEditable(false);
        subtitle.setFocusable(false);
        subtitle.setLineWrap(true);
        subtitle.setWrapStyleWord(true);
        subtitle.setBorder(BorderFactory.createEmptyBorder(6, 20, 0, 20));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(subtitle);
        return header;
    }

    private JScrollPane createDocumentScrollPane() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        String[][] documents = {
            {"Form 138 (Report Card)", "Grade 12 Report Card (Original)", "true"},
            {"PSA Birth Certificate", "Original copy from Philippine Statistics Authority", "true"},
            {"Medical Certificate", "CBC, Urinalysis, and X-Ray results", "true"},
            {"2x2 ID Photo", "White background, formal attire, with name tag", "true"}
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 15, 0);

        int row = 0;
        for (String[] doc : documents) {
            gbc.gridy = row++;
            boolean required = Boolean.parseBoolean(doc[2]);
            mainPanel.add(createDocCard(doc[0], doc[1], required), gbc);
        }

        gbc.gridy = row;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        mainPanel.add(filler, gbc);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setPreferredSize(new Dimension(0, 0));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel createDocCard(String title, String subtitle, boolean isRequired) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 30), 1, true),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.SUBHEADING_FONT);
        titleLabel.setForeground(Theme.TEXT_PRIMARY_COLOR);
        titleRow.add(titleLabel);

        if (isRequired) {
            JLabel badge = new JLabel("Required");
            badge.setFont(Theme.LABEL_FONT);
            badge.setForeground(Color.WHITE);
            badge.setOpaque(true);
            badge.setBackground(Theme.PRIMARY_COLOR);
            badge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
            titleRow.add(Box.createHorizontalStrut(8));
            titleRow.add(badge);
        }

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(Theme.BODY_FONT);
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY_COLOR);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        textPanel.add(titleRow);
        textPanel.add(subtitleLabel);

        RoundedButton uploadButton = RoundedButton.primary("Upload");
        uploadButton.setPreferredSize(new Dimension(120, 40));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(uploadButton);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.EAST);
        return card;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 15, 20, 15));

        RoundedButton backButton = RoundedButton.subtle("Back");
        nextButton.setPreferredSize(new Dimension(160, 44));

        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(backButton);
        buttonRow.add(nextButton);

        backButton.addActionListener(e -> navigate(Screen.BIO_DATA));
        nextButton.addActionListener(e -> navigate(Screen.PROGRAM_SELECTION));

        footer.add(buttonRow, BorderLayout.CENTER);
        return footer;
    }

    private void navigate(Screen target) {
        MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.showScreen(target, true);
        }
    }

    @Override
    public void onEnter(NavigationContext context) {
        // Future: auto-load upload progress.
    }

    @Override
    public void onLeave() {
        // No teardown required yet.
    }
}
