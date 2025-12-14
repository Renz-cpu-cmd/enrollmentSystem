
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
        setBackground(Theme.BACKGROUND_COLOR);
        add(buildBackdrop(), BorderLayout.CENTER);

        startLoader();
    }

    private JComponent buildBackdrop() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout(0, 24));
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new FlatDropShadowBorder(),
            new EmptyBorder(36, 48, 32, 48)));

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

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(12, 8));
        footer.setOpaque(false);
        footer.add(progressBar, BorderLayout.CENTER);
        footer.add(skipButton, BorderLayout.EAST);
        footer.add(statusLabel, BorderLayout.SOUTH);
        return footer;
    }

    private JProgressBar createProgressBar() {
        JProgressBar bar = new JProgressBar(0, PROGRESS_MAX);
        bar.setOpaque(true);
        bar.setForeground(Theme.PRIMARY_COLOR);
        bar.setBackground(new Color(0, 0, 0, 25));
        bar.setBorder(new EmptyBorder(4, 0, 4, 0));
        bar.setPreferredSize(new Dimension(0, 12));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
        bar.putClientProperty(FlatClientProperties.STYLE,
            "arc:999; trackArc:999; trackWidth:6;" +
                "foreground:#0C5CB1;" +
                "track:#E3E8EF;");
        return bar;
    }

    private JLabel createStatusLabel() {
        JLabel label = new JLabel("Initializing services...");
        label.setForeground(new Color(90, 99, 112));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }

    private JButton createSkipButton() {
        JButton button = new JButton("Skip Intro");
        button.putClientProperty(FlatClientProperties.STYLE,
            "arc:12; background:#0C5CB1; foreground:#FFFFFF;" +
                "focusWidth:2; hoverBackground:#0f6ed8; pressedBackground:#0a4f8d;");
        button.addActionListener(e -> advance());
        return button;
    }

    private JComponent buildCenterContent() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        LogoBadge logo = new LogoBadge();
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Enrollment System");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Theme.TEXT_PRIMARY_COLOR);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JLabel subtitle = new JLabel("Preparing your campus experience");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setForeground(new Color(110, 117, 130));

        content.add(logo);
        content.add(Box.createVerticalStrut(20));
        content.add(title);
        content.add(Box.createVerticalStrut(8));
        content.add(subtitle);
        return content;
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
        if (advanced) {
            return;
        }
        advanced = true;
        skipButton.setEnabled(false);
        cancelLoader();
        Navigation.to(this, Screen.PORTAL_GATEWAY);
    }

    @Override
    public void onEnter(NavigationContext context) {
        advanced = false;
        progressBar.setValue(0);
        statusLabel.setText("Initializing services...");
        skipButton.setEnabled(true);
        startLoader();
    }

    @Override
    public void onLeave() {
        cancelLoader();
        skipButton.setEnabled(false);
    }

    private final class SplashLoader extends SwingWorker<Void, Integer> {
        @Override
        protected Void doInBackground() throws Exception {
            for (int i = 0; i <= PROGRESS_MAX && !isCancelled(); i++) {
                Thread.sleep(35);
                publish(i);
            }
            return null;
        }

        @Override
        protected void process(List<Integer> chunks) {
            if (isCancelled() || chunks.isEmpty()) {
                return;
            }
            int value = chunks.get(chunks.size() - 1);
            progressBar.setValue(value);
            statusLabel.setText("Loading portal... " + value + "%");
        }

        @Override
        protected void done() {
            if (!advanced && !isCancelled()) {
                advance();
            }
        }
    }

    /** Simple vector badge so the splash has a crisp hero icon without external assets. */
    private static final class LogoBadge extends JComponent {
        private static final int SIZE = 120;

        LogoBadge() {
            setPreferredSize(new Dimension(SIZE, SIZE));
            putClientProperty(FlatClientProperties.STYLE, "arc:999");
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gradient = new GradientPaint(0, 0, Theme.PRIMARY, getWidth(), getHeight(), Theme.INFO);
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), SIZE, SIZE);

            g2.setColor(new Color(255, 255, 255, 220));
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(8, 8, getWidth() - 16, getHeight() - 16, SIZE, SIZE);

            String initials = "ES";
            Font font = getFont().deriveFont(Font.BOLD, 42f);
            FontMetrics fm = g2.getFontMetrics(font);
            int textWidth = fm.stringWidth(initials);
            int textHeight = fm.getAscent();
            g2.setFont(font);
            g2.setColor(Color.WHITE);
            g2.drawString(initials,
                (getWidth() - textWidth) / 2,
                (getHeight() + textHeight) / 2 - 8);

            g2.dispose();
        }
    }
}
