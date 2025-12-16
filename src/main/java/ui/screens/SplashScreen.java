package ui.screens;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.theme.Theme;
import util.Navigation;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class SplashScreen extends JPanel implements ScreenView {

    private static final int PROGRESS_MAX = 100;

    // --- HARVARD COLORS ---
    private static final Color UNI_BLUE = new Color(12, 92, 177); // Deep Royal Blue
    private static final Color UNI_GOLD = new Color(218, 165, 32); // Academic Gold
    private static final Color TEXT_GRAY = new Color(100, 110, 120);

    private boolean advanced;
    private final JProgressBar progressBar;
    private final JLabel statusLabel;
    private final JButton skipButton;
    private SplashLoader loader;

    public SplashScreen() {
        progressBar = createProgressBar();
        statusLabel = createStatusLabel();
        skipButton = createSkipButton();

        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(245, 247, 250)); // Very light gray/white background
        add(buildBackdrop(), BorderLayout.CENTER);
    }

    private JProgressBar createProgressBar() {
        JProgressBar bar = new JProgressBar(0, PROGRESS_MAX);
        bar.setOpaque(true);
        bar.setBorder(new EmptyBorder(0, 0, 0, 0));
        bar.setPreferredSize(new Dimension(300, 6));
        bar.putClientProperty(FlatClientProperties.STYLE,
            "arc: 999;" +
                "foreground: #0C5CB1;" +
                "track: #E3E8EF;");
        return bar;
    }

    private JLabel createStatusLabel() {
        JLabel label = new JLabel("Initializing secure environment...");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(TEXT_GRAY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(12, 12));
        footer.setOpaque(false);
        footer.add(progressBar, BorderLayout.CENTER);
        footer.add(statusLabel, BorderLayout.SOUTH);
        return footer;
    }

    private JComponent buildBackdrop() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        // The "Card" in the center
        JPanel card = new JPanel(new BorderLayout(0, 24));
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        // Soft shadow for depth
        card.setBorder(new CompoundBorder(
            new FlatDropShadowBorder(),
            new EmptyBorder(40, 60, 40, 60))); // More breathing room

        card.add(buildCenterContent(), BorderLayout.CENTER);
        card.add(buildFooter(), BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        wrapper.add(card, gbc);
        return wrapper;
    }

    private JComponent buildCenterContent() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 1. The Logo
        LogoBadge logo = new LogoBadge();
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setAlignmentY(Component.CENTER_ALIGNMENT);

        // 2. The Title
        JLabel title = new JLabel("UNIVERSITY PORTAL");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(UNI_BLUE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.putClientProperty(FlatClientProperties.STYLE, "insets: 0,0,0,0");

        // 3. The Subtitle
        JLabel subtitle = new JLabel("Secure Student Enrollment System");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setForeground(TEXT_GRAY);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        content.add(logo);
        content.add(Box.createVerticalStrut(25)); // Space between logo and text
        content.add(title);
        content.add(Box.createVerticalStrut(5));
        content.add(subtitle);

        // Wrap content in a center-aligned GridBag to keep it centered at the top of the card
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.PAGE_START;
        wrapper.add(content, gbc);
        return wrapper;
    }

    private JButton createSkipButton() {
        // We hide the button in the UI but keep logic if needed visually later
        JButton button = new JButton("Skip");
        return button;
    }

    private void startLoader() {
        cancelLoader();
        loader = new SplashLoader();
        loader.execute();
    }

    private void cancelLoader() {
        if (loader != null && !loader.isDone()) {
            loader.cancel(true);
        }
    }

    private void advance() {
        if (advanced) return;
        advanced = true;
        cancelLoader();
        Navigation.to(this, Screen.PORTAL_GATEWAY);
    }

    @Override
    public void onEnter(NavigationContext context) {
        advanced = false;
        progressBar.setValue(0);
        statusLabel.setText("Initializing secure environment...");
        startLoader();
    }

    @Override
    public void onLeave() {
        cancelLoader();
    }

    // --- THE BRAINS: SIMULATED LOADING LOGIC ---
    private final class SplashLoader extends SwingWorker<Void, Integer> {
        @Override
        protected Void doInBackground() throws Exception {
            // Slower start, faster finish curve
            int[] milestones = {10, 30, 60, 85, 95, 100};
            int current = 0;
            
            for (int milestone : milestones) {
                while (current < milestone && !isCancelled()) {
                    current++;
                    publish(current);
                    // Variable speed to feel "real"
                    long sleepTime = (current < 50) ? 30 : 15; 
                    if (current > 90) sleepTime = 50; // Pause at 90% for "suspense"
                    Thread.sleep(sleepTime); 
                }
            }
            Thread.sleep(300); // Tiny pause at 100%
            return null;
        }

        @Override
        protected void process(List<Integer> chunks) {
            if (isCancelled() || chunks.isEmpty()) return;
            int value = chunks.get(chunks.size() - 1);
            progressBar.setValue(value);
            
            // DYNAMIC STATUS TEXT
            if (value < 20) statusLabel.setText("Establishing secure connection...");
            else if (value < 50) statusLabel.setText("Verifying database integrity...");
            else if (value < 80) statusLabel.setText("Loading academic modules...");
            else if (value < 99) statusLabel.setText("Finalizing session...");
            else statusLabel.setText("Welcome.");
        }

        @Override
        protected void done() {
            if (!advanced && !isCancelled()) {
                advance();
            }
        }
    }

    // --- THE LOOKS: CUSTOM BADGE ---
    private static final class LogoBadge extends JComponent {
        private static final int SIZE = 100;

        LogoBadge() {
            setPreferredSize(new Dimension(SIZE, SIZE));
            putClientProperty(FlatClientProperties.STYLE, "arc:999"); // Circle
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. Draw Background Circle (University Blue)
            g2.setColor(UNI_BLUE);
            g2.fillOval(0, 0, SIZE, SIZE);

            // 2. Draw Gold Rim (The "Premium" Touch)
            g2.setColor(UNI_GOLD);
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(4, 4, SIZE-8, SIZE-8);

            // 3. Draw Initials
            String initials = "U"; // U for University
            Font font = new Font("Serif", Font.BOLD, 55); // Serif font looks more "Academic"
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(initials)) / 2;
            int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            
            g2.setColor(Color.WHITE);
            g2.drawString(initials, textX, textY - 3); // -3 for visual centering

            g2.dispose();
        }
    }
}