package ui.screens;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import ui.MobileFrame;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.components.WizardHeader;
import ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DocumentsScreen extends JPanel implements ScreenView {

    private final JButton nextButton;
    private final List<DocumentCard> documentCards = new ArrayList<>();

    private static final List<DocumentRequirement> REQUIREMENTS = List.of(
        new DocumentRequirement("Form 138 (Report Card)", "Grade 12 report card, signed by school registrar.", "PDF"),
        new DocumentRequirement("PSA Birth Certificate", "Original PSA issued copy.", "PDF"),
        new DocumentRequirement("Good Moral Certificate", "Signed by guidance counselor (current school).", "PDF"),
        new DocumentRequirement("2x2 ID Photo", "White background, formal attire, no filters.", "IMG")
    );

    public DocumentsScreen() {
        setLayout(new BorderLayout());
        setBackground(new Color(244, 247, 254));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(new WizardHeader(2), BorderLayout.NORTH);
        add(createDocumentScrollPane(), BorderLayout.CENTER);
        nextButton = createPrimaryButton("Next: Program Selection");
        nextButton.setEnabled(false);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }


    private JScrollPane createDocumentScrollPane() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 8, 24, 8));

        for (DocumentRequirement requirement : REQUIREMENTS) {
            DocumentCard card = new DocumentCard(requirement);
            documentCards.add(card);
            content.add(card);
            content.add(Box.createVerticalStrut(16));
        }

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        return scrollPane;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(24, 0, 0, 0));

        JLabel note = new JLabel("All required documents must be uploaded before proceeding.");
        note.setFont(Theme.BODY_FONT);
        note.setForeground(new Color(120, 126, 140));
        footer.add(note, BorderLayout.WEST);

        JButton backButton = createSecondaryButton("Back");
        backButton.addActionListener(e -> navigate(Screen.BIO_DATA));
        nextButton.addActionListener(e -> navigate(Screen.PROGRAM_SELECTION));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(backButton);
        buttonRow.add(nextButton);
        footer.add(buttonRow, BorderLayout.EAST);
        return footer;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.putClientProperty(FlatClientProperties.STYLE,
            "arc:16; background:#0C5CB1; foreground:#FFFFFF; font:+1;" +
                "hoverBackground:#0f6ed8; pressedBackground:#0a4f8d; focusWidth:2; innerFocusWidth:1;");
        button.setBorder(new EmptyBorder(12, 32, 12, 32));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.putClientProperty(FlatClientProperties.STYLE,
            "arc:16; background:#ffffff; foreground:#0C5CB1;" +
                "borderColor:#0C5CB1; focusWidth:1; font:+1;");
        button.setBorder(new EmptyBorder(12, 32, 12, 32));
        return button;
    }

    private void handleUpload(DocumentCard card) {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            card.showUploading();
            simulateUpload(card, selectedFile.getName());
        }
    }

    private void simulateUpload(DocumentCard card, String fileName) {
        Timer timer = new Timer(40, null);
        timer.addActionListener(e -> {
            int value = card.incrementProgress(2);
            if (value >= 100) {
                timer.stop();
                card.markUploaded(fileName);
                updateNextButtonState();
            }
        });
        timer.start();
    }

    private void updateNextButtonState() {
        boolean allUploaded = documentCards.stream().allMatch(DocumentCard::isUploaded);
        nextButton.setEnabled(allUploaded);
    }

    private void navigate(Screen target) {
        MobileFrame frame = (MobileFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.showScreen(target, true);
        }
    }

    @Override
    public void onEnter(NavigationContext context) {
        updateNextButtonState();
    }

    @Override
    public void onLeave() {
        // Future: persist upload metadata.
    }

    private record DocumentRequirement(String name, String description, String iconText) {}

    private class DocumentCard extends JPanel {

        private final JLabel statusLabel;
        private final JButton uploadButton;
        private final JProgressBar progressBar;
        private final StatusBadge statusBadge;
        private boolean uploaded;

        DocumentCard(DocumentRequirement requirement) {
            setLayout(new BorderLayout(18, 0));
            setOpaque(false);

            JPanel card = new JPanel(new BorderLayout(18, 0));
            card.setBackground(Color.WHITE);
            card.setBorder(new CompoundBorder(
                new FlatDropShadowBorder(),
                new CompoundBorder(new FlatRoundBorder(), new EmptyBorder(20, 24, 20, 24))
            ));

            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            left.setOpaque(false);
            JLabel icon = new JLabel(requirement.iconText(), SwingConstants.CENTER);
            icon.setPreferredSize(new Dimension(72, 72));
            icon.setFont(Theme.HEADING_FONT);
            icon.setForeground(new Color(12, 92, 177));
            icon.setOpaque(true);
            icon.setBackground(new Color(244, 247, 254));
            icon.setBorder(new CompoundBorder(new FlatRoundBorder(), new EmptyBorder(12, 12, 12, 12)));
            left.add(icon);

            JPanel info = new JPanel();
            info.setOpaque(false);
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setBorder(new EmptyBorder(0, 12, 0, 0));

            JLabel nameLabel = new JLabel(requirement.name());
            nameLabel.setFont(Theme.SUBHEADER_FONT);
            nameLabel.setForeground(Theme.TEXT_HEADER);
            JLabel descriptionLabel = new JLabel(requirement.description());
            descriptionLabel.setFont(Theme.BODY_FONT);
            descriptionLabel.setForeground(Theme.TEXT_SECONDARY_COLOR);

            statusLabel = new JLabel();
            statusLabel.setFont(Theme.BODY_FONT);
            statusBadge = new StatusBadge();
            statusBadge.setPreferredSize(new Dimension(18, 18));
            statusBadge.setVisible(false);

            progressBar = new JProgressBar(0, 100);
            progressBar.setVisible(false);
            progressBar.setStringPainted(true);

            JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            statusRow.setOpaque(false);
            statusRow.add(statusBadge);
            statusRow.add(statusLabel);

            info.add(nameLabel);
            info.add(Box.createVerticalStrut(4));
            info.add(descriptionLabel);
            info.add(Box.createVerticalStrut(12));
            info.add(statusRow);
            info.add(Box.createVerticalStrut(8));
            info.add(progressBar);

            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            actionPanel.setOpaque(false);
            uploadButton = createPrimaryButton("Upload");
            uploadButton.addActionListener(e -> handleUpload(this));
            actionPanel.add(uploadButton);

            card.add(left, BorderLayout.WEST);
            card.add(info, BorderLayout.CENTER);
            card.add(actionPanel, BorderLayout.EAST);

            setLayout(new BorderLayout());
            add(card, BorderLayout.CENTER);

            setStatus("Pending upload", new Color(196, 126, 32), false);
        }

        void showUploading() {
            uploaded = false;
            progressBar.setVisible(true);
            progressBar.setValue(0);
            progressBar.setString("Scanning...");
            uploadButton.setEnabled(false);
            uploadButton.setText("Uploading...");
            setStatus("Scanning document for compliance...", new Color(196, 126, 32), false);
        }

        int incrementProgress(int delta) {
            if (!progressBar.isVisible()) {
                return 100;
            }
            int next = Math.min(100, progressBar.getValue() + delta);
            progressBar.setValue(next);
            progressBar.setString(next + "%");
            return next;
        }

        void markUploaded(String fileName) {
            uploaded = true;
            progressBar.setVisible(false);
            uploadButton.setEnabled(true);
            uploadButton.setText("Replace");
            setStatus("Uploaded - " + fileName, new Color(32, 158, 95), true);
        }

        boolean isUploaded() {
            return uploaded;
        }

        private void setStatus(String text, Color color, boolean showBadge) {
            statusLabel.setText(text);
            statusLabel.setForeground(color);
            statusBadge.setVisible(showBadge);
        }
    }

    private static class StatusBadge extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(32, 158, 95));
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(Color.WHITE);
            int w = getWidth();
            int h = getHeight();
            g2.drawLine(w * 3 / 10, h * 5 / 10, w * 5 / 10, h * 7 / 10);
            g2.drawLine(w * 5 / 10, h * 7 / 10, w * 8 / 10, h * 3 / 10);
            g2.dispose();
        }
    }

}
