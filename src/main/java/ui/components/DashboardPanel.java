package ui.components;

import model.Student;
import ui.Screen;
import ui.theme.Theme;
import util.Navigation;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Student dashboard content with hero CTA, heads-up cards, and schedule timeline.
 */
public class DashboardPanel extends JPanel {
    private JPanel headsUpGrid;
    private JPanel[] headsUpCards;


    private final SessionManager sessionManager = SessionManager.getInstance();
    private final List<TimelineEvent> todaySchedule = List.of(
        new TimelineEvent("09:00 AM", "Data Structures", "Innovation Lab"),
        new TimelineEvent("11:00 AM", "Algorithms", "Room 305"),
        new TimelineEvent("01:30 PM", "Software Engineering", "Design Studio"),
        new TimelineEvent("03:00 PM", "Ethics", "Room 210"),
        new TimelineEvent("04:30 PM", "Capstone Sync", "Collab Space")
    );

    public DashboardPanel() {
                // Responsive: listen for resize to adjust card layout
                addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        updateHeadsUpLayout();
                    }
                });
        setOpaque(false);
        setLayout(new BorderLayout());
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(32, 32, 48, 32));

        content.add(createHeroSection());
        content.add(Box.createVerticalStrut(32));
        content.add(createHeadsUpSection());
        content.add(Box.createVerticalStrut(40));
        content.add(createScheduleSection());

        add(content, BorderLayout.CENTER);
    }

    private JComponent createHeroSection() {
        HeroBanner hero = new HeroBanner();
        hero.setLayout(new BorderLayout());
        hero.setBorder(new EmptyBorder(36, 42, 36, 42));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));

        JLabel greeting = new JLabel(buildGreeting());
        greeting.setFont(Theme.HEADING_FONT.deriveFont(Font.BOLD, 28f));
        greeting.setForeground(Color.WHITE);

        JLabel quote = new JLabel("\"" + getQuoteOfTheDay() + "\"");
        quote.setFont(Theme.BODY_FONT.deriveFont(Font.ITALIC, 14f));
        quote.setForeground(new Color(224, 231, 255));
        quote.setBorder(new EmptyBorder(10, 0, 20, 0));

        boolean enrollmentOpen = isEnrollmentOpen();
        JButton enrollmentButton;
        if (enrollmentOpen) {
            enrollmentButton = createPrimaryButton("Enrollment for 2nd Sem is Open >",
                () -> Navigation.to(this, Screen.PROGRAM_SELECTION));
        } else {
            enrollmentButton = createPrimaryButton("Enrollment closed—see your registrar.", () -> {});
            enrollmentButton.setEnabled(false);
        }

        textStack.add(greeting);
        textStack.add(quote);
        textStack.add(enrollmentButton);

        hero.add(textStack, BorderLayout.WEST);
        return hero;
    }

    private JButton createPrimaryButton(String label, Runnable action) {
        JButton button = new JButton(label);
        button.setFont(Theme.SUBHEADER_FONT);
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(15, 23, 42));
        button.setBorder(new EmptyBorder(14, 28, 14, 28));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private JComponent createHeadsUpSection() {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Heads Up");
        title.setFont(Theme.SUBHEADER_FONT.deriveFont(Font.BOLD, 20f));
        title.setForeground(new Color(30, 41, 59));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        headsUpGrid = new JPanel();
        headsUpGrid.setOpaque(false);
        headsUpGrid.setLayout(new GridBagLayout());
        headsUpGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        headsUpCards = new JPanel[]{createFinanceCard(), createPerformanceCard(), createStatusCard()};
        layoutHeadsUpCards();

        section.add(title);
        section.add(Box.createVerticalStrut(18));
        section.add(headsUpGrid);
        return section;

    }

    private void layoutHeadsUpCards() {
        headsUpGrid.removeAll();
        int width = getWidth();
        boolean vertical = width < 900;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        int cardWidth = vertical ? 340 : 260;
        int cardHeight = 180;
        for (int i = 0; i < headsUpCards.length; i++) {
            JPanel card = headsUpCards[i];
            card.setPreferredSize(new Dimension(cardWidth, cardHeight));
            card.setMaximumSize(new Dimension(cardWidth, cardHeight));
            gbc.gridx = vertical ? 0 : i;
            gbc.gridy = vertical ? i : 0;
            gbc.insets = vertical ? new Insets(i == 0 ? 0 : 18, 0, 0, 0) : new Insets(0, i == 0 ? 0 : 24, 0, 0);
            headsUpGrid.add(card, gbc);
        }
        headsUpGrid.revalidate();
        headsUpGrid.repaint();
    }

    private void updateHeadsUpLayout() {
        if (headsUpGrid != null && headsUpCards != null) {
            layoutHeadsUpCards();
        }
    }

    private JPanel createFinanceCard() {
        JPanel card = createCardContainer();
        JLabel title = new JLabel("Total Due");
        title.setFont(Theme.SUBHEADER_FONT);
        title.setForeground(new Color(55, 65, 81));

        JLabel amount = new JLabel(formatCurrency(24_500.00));
        amount.setForeground(new Color(15, 23, 42));
        amount.setAlignmentX(Component.LEFT_ALIGNMENT);
        enableDynamicFontResize(amount, 30f, 18f);

        JSeparator separator = new JSeparator();

        JButton payNow = createLinkButton("Pay Now", () -> Navigation.to(this, Screen.ASSESSMENT));

        card.add(title);
        card.add(Box.createVerticalStrut(16));
        card.add(amount);
        card.add(Box.createVerticalStrut(12));
        card.add(separator);
        card.add(Box.createVerticalStrut(8));
        card.add(payNow);
        return card;
    }

    private JPanel createPerformanceCard() {
        JPanel card = createCardContainer();
        JLabel title = new JLabel("Academics");
        title.setFont(Theme.SUBHEADER_FONT);
        title.setForeground(new Color(55, 65, 81));

        CircularProgressView view = new CircularProgressView(0.82, "GWA 1.5");
        view.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel note = new JLabel("Dean's Lister Pace");
        note.setFont(Theme.BODY_FONT);
        note.setForeground(new Color(107, 114, 128));

        card.add(title);
        card.add(Box.createVerticalStrut(16));
        card.add(view);
        card.add(Box.createVerticalStrut(12));
        card.add(note);
        return card;
    }

    private JPanel createStatusCard() {
        JPanel card = createCardContainer();
        JLabel title = new JLabel("Status");
        title.setFont(Theme.SUBHEADER_FONT);
        title.setForeground(new Color(55, 65, 81));

        String standing = getStudentStanding();
        JLabel badge = new JLabel(standing, SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setForeground(Color.WHITE);
        badge.setBorder(new EmptyBorder(8, 14, 8, 14));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Color logic
        switch (standing) {
            case "Regular Student" -> badge.setBackground(new Color(16, 185, 129));
            case "Irregular" -> badge.setBackground(new Color(251, 191, 36));
            case "On Probation" -> badge.setBackground(new Color(239, 68, 68));
            default -> badge.setBackground(new Color(107, 114, 128));
        }

        JLabel detail = null;
        if ("Regular Student".equals(standing)) {
            detail = new JLabel("Eligible for standard block sectioning.");
        } else if ("Irregular".equals(standing)) {
            detail = new JLabel("See adviser for block sectioning.");
        } else if ("On Probation".equals(standing)) {
            detail = new JLabel("Academic warning—see your adviser.");
        }
        if (detail != null) {
            detail.setFont(Theme.BODY_FONT);
            detail.setForeground(new Color(107, 114, 128));
        }

        card.add(title);
        card.add(Box.createVerticalStrut(16));
        card.add(badge);
        if (detail != null) {
            card.add(Box.createVerticalStrut(12));
            card.add(detail);
        }
        return card;
    }

    // Simulated logic for demo; replace with real logic as needed
    private boolean isEnrollmentOpen() {
        // TODO: Replace with real term/standing logic
        return true;
    }

    private String getStudentStanding() {
        // TODO: Replace with real standing logic
        return "Regular Student";
    }

    private JPanel createScheduleSection() {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Today's Schedule");
        title.setFont(Theme.SUBHEADER_FONT.deriveFont(Font.BOLD, 18f));
        title.setForeground(new Color(30, 41, 59));

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        if (todaySchedule.isEmpty()) {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            JLabel mascot = new JLabel("\uD83D\uDE0A"); // Smiling face as mascot
            mascot.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            mascot.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel msg = new JLabel("Enjoy your free day!");
            msg.setFont(Theme.SUBHEADER_FONT.deriveFont(Font.BOLD, 16f));
            msg.setForeground(new Color(107, 114, 128));
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);
            empty.add(Box.createVerticalStrut(16));
            empty.add(mascot);
            empty.add(Box.createVerticalStrut(8));
            empty.add(msg);
            list.add(empty);
        } else {
            for (TimelineEvent event : todaySchedule) {
                list.add(createScheduleRow(event));
                list.add(Box.createVerticalStrut(12));
            }
            if (!todaySchedule.isEmpty()) {
                list.remove(list.getComponentCount() - 1);
            }
        }

        section.add(title);
        section.add(Box.createVerticalStrut(12));
        section.add(list);
        return section;
    }

    private JPanel createScheduleRow(TimelineEvent event) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setOpaque(true);
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1, true),
            new EmptyBorder(16, 20, 16, 20)));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel time = new JLabel(event.time());
        time.setFont(Theme.BOLD_BODY_FONT);
        time.setForeground(new Color(55, 65, 81));

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel subject = new JLabel(event.subject());
        subject.setFont(Theme.BOLD_BODY_FONT);
        subject.setForeground(new Color(17, 24, 39));

        JLabel location = new JLabel(event.location());
        location.setFont(Theme.BODY_FONT);
        location.setForeground(new Color(107, 114, 128));

        details.add(subject);
        details.add(Box.createVerticalStrut(4));
        details.add(location);

        row.add(time, BorderLayout.WEST);
        row.add(details, BorderLayout.CENTER);
        return row;
    }

    private JPanel createCardContainer() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Drop shadow
                g2.setColor(new Color(0,0,0,32));
                g2.fillRoundRect(4, 6, getWidth()-8, getHeight()-8, 24, 24);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(20, 20, 20, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        return card;
    }

    private JButton createLinkButton(String text, Runnable action) {
        JButton link = new JButton(text);
        link.setFont(Theme.BOLD_BODY_FONT);
        link.setForeground(new Color(37, 99, 235));
        link.setBorder(BorderFactory.createEmptyBorder());
        link.setContentAreaFilled(false);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.setFocusPainted(false);
        link.setAlignmentX(Component.LEFT_ALIGNMENT);
        link.addActionListener(e -> action.run());
        return link;
    }

    private void enableDynamicFontResize(JLabel label, float maxSize, float minSize) {
        ComponentAdapter adapter = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjust();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                adjust();
            }

            private void adjust() {
                int width = label.getWidth();
                if (width <= 0) {
                    return;
                }
                Font base = label.getFont();
                float size = maxSize;
                while (size >= minSize) {
                    Font candidate = base.deriveFont(size);
                    FontMetrics fm = label.getFontMetrics(candidate);
                    if (fm.stringWidth(label.getText()) <= width - 10 || size == minSize) {
                        label.setFont(candidate);
                        break;
                    }
                    size -= 1f;
                }
            }
        };
        label.addComponentListener(adapter);
    }

    private String buildGreeting() {
        int hour = LocalTime.now().getHour();
        String salutation;
        if (hour < 12) {
            salutation = "Good Morning";
        } else if (hour < 18) {
            salutation = "Good Afternoon";
        } else {
            salutation = "Good Evening";
        }
        return salutation + ", " + resolveName() + "!";
    }

    private String resolveName() {
        Student student = sessionManager.getCurrentStudent();
        if (student != null) {
            return student.getFirstName() + " " + student.getLastName();
        }
        return "Trailblazer";
    }

    private String getQuoteOfTheDay() {
        String[] quotes = new String[]{
            "Discipline turns dreams into reality.",
            "Small wins compound into mastery.",
            "Consistency beats intensity.",
            "Create before you consume.",
            "Your future self is watching."
        };
        int index = LocalDate.now().getDayOfMonth() % quotes.length;
        return quotes[index];
    }

    private String formatCurrency(double value) {
        return "PHP " + String.format("%,.2f", value);
    }

    private record TimelineEvent(String time, String subject, String location) {
    }

    private static final class HeroBanner extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, new Color(15, 76, 129), getWidth(), getHeight(), new Color(67, 56, 202));
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
            g2.dispose();
        }
    }

    private static final class CircularProgressView extends JPanel {
        private final double progress;
        private final String label;

        private CircularProgressView(double progress, String label) {
            this.progress = progress;
            this.label = label;
            setOpaque(false);
            setPreferredSize(new Dimension(140, 140));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 10;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(new Color(229, 231, 235));
            g2.setStroke(new BasicStroke(10f));
            g2.drawOval(x, y, size, size);

            g2.setColor(new Color(107, 70, 193));
            g2.drawArc(x, y, size, size, 90, (int) (-360 * progress));

            g2.setColor(new Color(15, 23, 42));
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18f));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, (getWidth() - fm.stringWidth(label)) / 2, getHeight() / 2 + fm.getAscent() / 2);
            g2.dispose();
        }
    }
}
