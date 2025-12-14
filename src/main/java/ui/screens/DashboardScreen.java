package ui.screens;

import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.components.ModernStatCard;
import ui.theme.Theme;
import util.Navigation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DashboardScreen extends JPanel implements ScreenView {

    private static final class StatusCardData {
        private final String title;
        private final String status;
        private final boolean cleared;
        private final Color iconColor;

        private StatusCardData(String title, String status, boolean cleared, Color iconColor) {
            this.title = title;
            this.status = status;
            this.cleared = cleared;
            this.iconColor = iconColor;
        }
    }

    private static final class ActivityItem {
        private final String headline;
        private final String timestamp;
        private final String description;

        private ActivityItem(String headline, String timestamp, String description) {
            this.headline = headline;
            this.timestamp = timestamp;
            this.description = description;
        }
    }

    private final List<StatusCardData> statusCards = List.of(
        new StatusCardData("Accounting", "Cleared", true, Theme.ICON_BG_GREEN),
        new StatusCardData("Grades", "Submitted", true, Theme.ICON_BG_BLUE),
        new StatusCardData("Discipline", "No Record", true, Theme.ICON_BG_RED)
    );

    private final List<ActivityItem> recentActivity = List.of(
        new ActivityItem("Payment verified", "Today, 10:32 AM", "Treasury marked your tuition for 2nd term as paid."),
        new ActivityItem("Grades uploaded", "Yesterday, 5:10 PM", "All instructors finalized your 2nd year grades."),
        new ActivityItem("No violations", "Aug 10", "Student affairs confirmed you have no active cases.")
    );

    public DashboardScreen() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);
        add(buildScrollableContent(), BorderLayout.CENTER);
    }

    private JComponent buildScrollableContent() {
        JPanel column = new JPanel();
        column.setBackground(Theme.BACKGROUND_COLOR);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setBorder(new EmptyBorder(24, 24, 24, 24));

        column.add(createHeroSection());
        column.add(Box.createVerticalStrut(24));
        column.add(createStatusSection());
        column.add(Box.createVerticalStrut(24));
        column.add(createActivitySection());

        JScrollPane scrollPane = new JScrollPane(column);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private JComponent createHeroSection() {
        JPanel hero = new JPanel(new BorderLayout(24, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint paint = new GradientPaint(0, 0, Theme.PRIMARY, getWidth(), getHeight(), Theme.INFO.brighter());
                g2.setPaint(paint);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        hero.setOpaque(false);
        hero.setBorder(new EmptyBorder(28, 32, 28, 32));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));

        JLabel pill = new JLabel("STUDENT PORTAL", SwingConstants.CENTER);
        pill.setFont(Theme.LABEL_FONT);
        pill.setForeground(Theme.PRIMARY);
        pill.setBackground(new Color(255, 255, 255, 210));
        pill.setOpaque(true);
        pill.setBorder(new EmptyBorder(6, 12, 6, 12));
        pill.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel headline = new JLabel("Hi, Juan Dela Cruz");
        headline.setFont(Theme.HEADING_FONT.deriveFont(Font.BOLD, 26f));
        headline.setForeground(Color.WHITE);
        headline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtext = new JLabel("2nd Semester SY 2024-2025 enrollment is open.");
        subtext.setFont(Theme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        subtext.setForeground(new Color(255, 255, 255, 220));
        subtext.setAlignmentX(Component.LEFT_ALIGNMENT);

        textStack.add(pill);
        textStack.add(Box.createVerticalStrut(12));
        textStack.add(headline);
        textStack.add(Box.createVerticalStrut(8));
        textStack.add(subtext);

        JPanel ctaColumn = new JPanel();
        ctaColumn.setOpaque(false);
        ctaColumn.setLayout(new BoxLayout(ctaColumn, BoxLayout.Y_AXIS));

        JButton ctaButton = new JButton(isEnrollmentReady() ? "Enroll Now" : "Complete Requirements");
        ctaButton.setFont(Theme.SUBHEADING_FONT);
        ctaButton.setPreferredSize(new Dimension(200, Theme.BUTTON_HEIGHT));
        ctaButton.setMaximumSize(new Dimension(240, Theme.BUTTON_HEIGHT));
        ctaButton.setBackground(Color.WHITE);
        ctaButton.setForeground(Theme.PRIMARY);
        ctaButton.setFocusPainted(false);
        ctaButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        ctaButton.addActionListener(e -> {
            if (!isEnrollmentReady()) {
                JOptionPane.showMessageDialog(DashboardScreen.this,
                    "Complete all checklist items before proceeding.",
                    "Checklist Incomplete",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            boolean isRegular = true; // TODO: replace with service-driven determination
            if (isRegular) {
                JOptionPane.showMessageDialog(DashboardScreen.this,
                    "Status Verified: REGULAR. Proceeding to Block Sectioning...",
                    "Status Verified",
                    JOptionPane.INFORMATION_MESSAGE);
                Navigation.to(DashboardScreen.this, Screen.BLOCK_SECTIONING);
            } else {
                JOptionPane.showMessageDialog(DashboardScreen.this,
                    "Status Verified: IRREGULAR. Proceeding to Subject Scheduler...",
                    "Status Verified",
                    JOptionPane.INFORMATION_MESSAGE);
                Navigation.to(DashboardScreen.this, Screen.IRREGULAR_PATH);
            }
        });

        JLabel helper = new JLabel(isEnrollmentReady()
            ? "You cleared all checklist items."
            : "Finish pending requirements to continue.");
        helper.setFont(Theme.BODY_FONT);
        helper.setForeground(new Color(255, 255, 255, 200));
        helper.setBorder(new EmptyBorder(12, 0, 0, 0));

        ctaColumn.add(ctaButton);
        ctaColumn.add(helper);

        hero.add(textStack, BorderLayout.CENTER);
        hero.add(ctaColumn, BorderLayout.EAST);
        return hero;
    }

    private JComponent createStatusSection() {
        JPanel section = new JPanel(new BorderLayout(0, 16));
        section.setOpaque(false);

        JLabel title = new JLabel("Eligibility checklist");
        title.setFont(Theme.SUBHEADER_FONT);
        title.setForeground(Theme.TEXT_HEADER);

        JPanel grid = new JPanel(new GridLayout(1, statusCards.size(), 20, 0));
        grid.setOpaque(false);

        for (StatusCardData cardData : statusCards) {
            ModernStatCard card = new ModernStatCard(cardData.title, cardData.status, cardData.iconColor);
            card.setStatus(cardData.cleared);
            grid.add(card);
        }

        section.add(title, BorderLayout.NORTH);
        section.add(grid, BorderLayout.CENTER);
        return section;
    }

    private JComponent createActivitySection() {
        JPanel section = new JPanel(new BorderLayout(0, 16));
        section.setOpaque(false);

        JLabel title = new JLabel("Recent activity");
        title.setFont(Theme.SUBHEADER_FONT);
        title.setForeground(Theme.TEXT_HEADER);

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new GridLayout(recentActivity.size(), 1, 0, 12));

        for (ActivityItem item : recentActivity) {
            list.add(buildActivityRow(item));
        }

        section.add(title, BorderLayout.NORTH);
        section.add(list, BorderLayout.CENTER);
        return section;
    }

    private JComponent buildActivityRow(ActivityItem item) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(Theme.CARD_BG);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.SHADOW_COLOR, 1, true),
            new EmptyBorder(18, 18, 18, 18)));

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));

        JLabel headline = new JLabel(item.headline);
        headline.setFont(Theme.BOLD_BODY_FONT);
        headline.setForeground(Theme.TEXT_HEADER);

        JLabel description = new JLabel(item.description);
        description.setFont(Theme.BODY_FONT);
        description.setForeground(Theme.TEXT_BODY);
        description.setBorder(new EmptyBorder(6, 0, 0, 0));

        textStack.add(headline);
        textStack.add(description);

        JLabel timestamp = new JLabel(item.timestamp);
        timestamp.setFont(Theme.LABEL_FONT);
        timestamp.setForeground(Theme.TEXT_BODY);

        row.add(textStack, BorderLayout.CENTER);
        row.add(timestamp, BorderLayout.EAST);
        return row;
    }

    private boolean isEnrollmentReady() {
        return statusCards.stream().allMatch(card -> card.cleared);
    }

    @Override
    public void onEnter(NavigationContext context) {
        // Future: load profile + activity data via services.
    }

    @Override
    public void onLeave() {
        // No cleanup required for static view.
    }
}