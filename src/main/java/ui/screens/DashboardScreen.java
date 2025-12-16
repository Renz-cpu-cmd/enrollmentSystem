package ui.screens;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import dao.AssessmentDAO;
import dao.BlockDAO;
import dao.EnrollmentDAO;
import dao.StudentDAO;
import model.Assessment;
import model.Block;
import model.Enrollment;
import model.Schedule;
import model.Student;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.screens.AIAssistantScreen;
import util.Navigation;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DashboardScreen extends JPanel implements ScreenView {

    // Palette definitions for light and dim themes
    private static final Palette LIGHT = new Palette(
            new Color(244, 247, 254), // bg
            Color.WHITE,              // card
            new Color(0x0C5CB1),      // primary
            new Color(0x0EA5E9),      // accent
            new Color(0xF59E0B),      // warning
            new Color(0x10B981),      // success
            new Color(0x64748B),      // slate
            new Color(0x0F172A),      // text
            new Color(255, 255, 255, 200) // glow
    );

    private static final Palette DIM = new Palette(
            new Color(26, 32, 46),
            new Color(36, 44, 60),
            new Color(0x0C5CB1),
            new Color(0x38BDF8),
            new Color(0xFBBF24),
            new Color(0x34D399),
            new Color(0x94A3B8),
            new Color(0xE2E8F0),
            new Color(12, 92, 177, 120)
    );

    private Palette palette;

    private final StudentDAO studentDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final AssessmentDAO assessmentDAO;
    private final BlockDAO blockDAO;

    private JPanel mainContent;
    private JPanel heroPanel;
    private JLabel welcomeLabel;
    private JLabel dateLabel;
    private JLabel statusBadge;
    private JLabel blockBadge;
    private JLabel financeText;
    private JLabel nextClassTimer;
    private JProgressBar financeBar;
    private JPanel financeCardContent;
    private JPanel financeEmptyState;
    private FinanceAreaChart financeAreaChart;
    private FinanceStackedBar financeStackedBar;
    private JPanel trendChipRow;
    private JPanel scheduleContainer;
    private JPanel scheduleEmptyState;
    private JPanel personalizationRow;
    private JLabel nextActionTitle;
    private JLabel nextActionSubtitle;
    private JButton nextActionButton;
    private SparklinePanel sparklinePanel;
    private JPanel nextActionPanel;
    private JPanel assistantActions;
    private JButton mainActionButton;
    private JButton signOutButton;
    private BadgeButton notificationButton;
    private JDialog notificationSheet;
    private JToggleButton themeToggle;
    private JToggleButton todayFilter;
    private JToggleButton weekFilter;
    private JToggleButton compactToggle;
    private JToggleButton focusToggle;
    private HeatmapPanel heatmapPanel;
    private ProgressRing progressRing;
    private JSlider timelineSlider;
    private JLabel timelineLabel;
    private JLabel countdownLabel;
    private Timer countdownTimer;
    private JPanel actionPanel;
    private JPanel schedulePanel;

    private final List<JComponent> cards = new ArrayList<>();
    private final List<JButton> quickActionButtons = new ArrayList<>();
    private final Map<String, List<JComponent>> scheduleCache = new HashMap<>();
    private List<Schedule> cachedSchedules = List.of();
    private List<DashboardNotice> cachedNotices = List.of();
    private boolean compactMode = false;
    private boolean focusMode = false;
    private double financePaidValue = 0;
    private double financeDueValue = 0;
    private int[] financeHistory = new int[]{8, 12, 16, 20, 24, 28, 32};

    public DashboardScreen(StudentDAO studentDAO, EnrollmentDAO enrollmentDAO, AssessmentDAO assessmentDAO, BlockDAO blockDAO) {
        this.studentDAO = studentDAO;
        this.enrollmentDAO = enrollmentDAO;
        this.assessmentDAO = assessmentDAO;
        this.blockDAO = blockDAO;
        this.palette = SessionManager.getInstance().getDarkMode() ? DIM : LIGHT;

        setLayout(new BorderLayout());
        setBackground(palette.bg());

        JScrollPane scroll = new JScrollPane(buildBody());
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        scroll.getVerticalScrollBar().setBlockIncrement(120);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "trackArc:999;thumbArc:999;");
        add(scroll, BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::playEntranceAnimation);
    }

    private JPanel buildBody() {
        mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(palette.bg());
        mainContent.setBorder(new EmptyBorder(32, 40, 32, 40));

        mainContent.add(buildHeroSection());
        mainContent.add(Box.createVerticalStrut(12));
        mainContent.add(buildNextBestAction());
        mainContent.add(Box.createVerticalStrut(20));
        mainContent.add(buildStatsRow());
        mainContent.add(Box.createVerticalStrut(18));
        mainContent.add(buildFinanceVizCard());
        mainContent.add(Box.createVerticalStrut(18));
        mainContent.add(buildHeatmapAndProgressRow());
        mainContent.add(Box.createVerticalStrut(18));
        mainContent.add(buildTimelineCard());
        mainContent.add(Box.createVerticalStrut(24));

        JPanel lowerSplit = new JPanel(new GridLayout(1, 2, 24, 0));
        lowerSplit.setOpaque(false);
        schedulePanel = buildSchedulePanel();
        actionPanel = buildActionPanel();
        lowerSplit.add(schedulePanel);
        lowerSplit.add(actionPanel);
        mainContent.add(lowerSplit);
        return mainContent;
    }

    private JPanel buildHeroSection() {
        GradientHeroPanel hero = new GradientHeroPanel();
        heroPanel = hero;
        hero.setLayout(new BorderLayout());
        hero.setOpaque(false);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        welcomeLabel = new JLabel("Welcome back, Student");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setForeground(palette.text());

        dateLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateLabel.setForeground(palette.slate());

        personalizationRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        personalizationRow.setOpaque(false);
        personalizationRow.add(createChip("Upcoming deadlines: syncing..."));

        left.add(welcomeLabel);
        left.add(Box.createVerticalStrut(4));
        left.add(dateLabel);
        left.add(Box.createVerticalStrut(8));
        left.add(personalizationRow);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        themeToggle = createThemeToggle();
        compactToggle = createModeToggle("Compact", compactMode, () -> applyDensity(true));
        focusToggle = createModeToggle("Focus", focusMode, this::applyFocusMode);
        notificationButton = new BadgeButton("🔔");
        notificationButton.setToolTipText("Recent notices");
        notificationButton.addActionListener(e -> toggleNotificationSheet());
        JButton aiButton = createGhostButton("Ask AI", () -> {
            Window owner = SwingUtilities.getWindowAncestor(this);
            AIAssistantScreen.openFloating(owner);
        });
        signOutButton = createGhostButton("Sign out", () -> {
            SessionManager.getInstance().clearSession();
            showSnackbar("Signed out", true);
            Navigation.to(this, Screen.STUDENT_LOGIN);
        });

        mainActionButton = createPrimaryButton("Start Enrollment");
        mainActionButton.addActionListener(e -> Navigation.to(this, Screen.RETURNING_BLOCK_SCHEDULE));

        right.add(themeToggle);
        right.add(compactToggle);
        right.add(focusToggle);
        right.add(notificationButton);
        right.add(aiButton);
        right.add(signOutButton);
        right.add(mainActionButton);

        hero.add(left, BorderLayout.CENTER);
        hero.add(right, BorderLayout.EAST);

        hero.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hero.setParallax(e.getPoint());
            }
        });
        attachHoverLift(hero);
        return hero;
    }

    private JPanel buildNextBestAction() {
        nextActionPanel = createCard();
        nextActionTitle = new JLabel("Next Best Action");
        nextActionTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nextActionTitle.setForeground(palette.text());

        nextActionSubtitle = new JLabel("We’ll guide you to the most impactful next step.");
        nextActionSubtitle.setForeground(palette.slate());

        nextActionButton = createPrimaryButton("Start");
        nextActionButton.addActionListener(e -> Navigation.to(this, Screen.RETURNING_BLOCK_SCHEDULE));

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        copy.add(nextActionTitle);
        copy.add(Box.createVerticalStrut(4));
        copy.add(nextActionSubtitle);

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.add(copy, BorderLayout.CENTER);
        row.add(nextActionButton, BorderLayout.EAST);

        assistantActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        assistantActions.setOpaque(false);
        assistantActions.add(createGhostButton("Upload docs", () -> showSnackbar("Upload docs (coming soon)", true)));
        assistantActions.add(createGhostButton("Quick pay", () -> Navigation.to(this, Screen.PAYMENT)));
        assistantActions.add(createGhostButton("Confirm info", () -> Navigation.to(this, Screen.ASSESSMENT)));

        nextActionPanel.add(row, BorderLayout.CENTER);
        nextActionPanel.add(assistantActions, BorderLayout.SOUTH);
        return nextActionPanel;
    }

    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 24, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 170));

        JPanel statusCard = createCard();
        JLabel statusTitle = new JLabel("Enrollment Status");
        statusTitle.setForeground(palette.slate());
        statusBadge = new JLabel("Checking...");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 20));
        statusBadge.setForeground(palette.primary());
        statusBadge.setIconTextGap(10);
        statusBadge.setToolTipText("Enrollment completion state");
        statusCard.add(statusTitle, BorderLayout.NORTH);
        statusCard.add(statusBadge, BorderLayout.CENTER);
        attachHoverLift(statusCard);

        JPanel finCard = createCard();
        JLabel finTitle = new JLabel("Financial Standing");
        finTitle.setForeground(palette.slate());
        financeCardContent = new JPanel();
        financeCardContent.setLayout(new BoxLayout(financeCardContent, BoxLayout.Y_AXIS));
        financeCardContent.setOpaque(false);
        financeText = new JLabel("₱ 0.00 / ₱ 0.00");
        financeText.setFont(new Font("Consolas", Font.BOLD, 18));
        financeText.setForeground(palette.text());
        financeBar = new JProgressBar(0, 100);
        financeBar.setPreferredSize(new Dimension(100, 10));
        financeBar.putClientProperty(FlatClientProperties.STYLE, "arc:999; foreground:#10B981; track:#E2E8F0;");
        financeBar.setToolTipText("Paid vs Due");

        trendChipRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        trendChipRow.setOpaque(false);
        trendChipRow.add(createChip("Trend: --"));

        financeCardContent.add(financeText);
        financeCardContent.add(Box.createVerticalStrut(6));
        financeCardContent.add(financeBar);
        financeCardContent.add(Box.createVerticalStrut(8));
        financeCardContent.add(trendChipRow);

        financeEmptyState = buildFinanceEmptyState();
        JPanel finCenter = new JPanel(new CardLayout());
        finCenter.setOpaque(false);
        finCenter.add(financeCardContent, "data");
        finCenter.add(financeEmptyState, "empty");

        finCard.add(finTitle, BorderLayout.NORTH);
        finCard.add(finCenter, BorderLayout.CENTER);
        attachHoverLift(finCard);

        JPanel blockCard = createCard();
        JLabel blockTitle = new JLabel("Current Block");
        blockTitle.setForeground(palette.slate());
        blockBadge = new JLabel("--");
        blockBadge.setFont(new Font("Segoe UI", Font.BOLD, 24));
        blockBadge.setForeground(palette.text());
        nextClassTimer = new JLabel("Next class info...");
        nextClassTimer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nextClassTimer.setForeground(palette.accent());
        blockCard.add(blockTitle, BorderLayout.NORTH);
        blockCard.add(blockBadge, BorderLayout.CENTER);
        blockCard.add(nextClassTimer, BorderLayout.SOUTH);
        attachHoverLift(blockCard);

        row.add(statusCard);
        row.add(finCard);
        row.add(blockCard);
        return row;
    }

    private JPanel buildFinanceVizCard() {
        JPanel card = createCard();
        card.setPreferredSize(new Dimension(0, 200));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Finance Trends");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(palette.text());
        JLabel subtitle = new JLabel("Payments over time and installment split");
        subtitle.setForeground(palette.slate());
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JPanel titleWrap = new JPanel();
        titleWrap.setOpaque(false);
        titleWrap.setLayout(new BoxLayout(titleWrap, BoxLayout.Y_AXIS));
        titleWrap.add(title);
        titleWrap.add(subtitle);
        header.add(titleWrap, BorderLayout.WEST);

        JPanel body = new JPanel(new GridLayout(1, 2, 12, 0));
        body.setOpaque(false);
        financeAreaChart = new FinanceAreaChart();
        financeStackedBar = new FinanceStackedBar();
        body.add(financeAreaChart);
        body.add(financeStackedBar);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildHeatmapAndProgressRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 18, 0));
        row.setOpaque(false);

        JPanel heatCard = createCard();
        JLabel heatTitle = new JLabel("Schedule Heatmap");
        heatTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heatTitle.setForeground(palette.text());
        heatmapPanel = new HeatmapPanel();
        heatmapPanel.setPreferredSize(new Dimension(0, 180));
        heatCard.add(heatTitle, BorderLayout.NORTH);
        heatCard.add(heatmapPanel, BorderLayout.CENTER);

        JPanel progressCard = createCard();
        JLabel progressTitle = new JLabel("Enrollment Progress");
        progressTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        progressTitle.setForeground(palette.text());
        progressRing = new ProgressRing();
        progressRing.setPreferredSize(new Dimension(180, 180));
        JPanel progressCenter = new JPanel(new BorderLayout());
        progressCenter.setOpaque(false);
        progressCenter.add(progressRing, BorderLayout.CENTER);
        JLabel progressHint = new JLabel("Profile • Docs • Assessment • Payment");
        progressHint.setForeground(palette.slate());
        progressHint.setHorizontalAlignment(SwingConstants.CENTER);
        progressHint.setBorder(new EmptyBorder(4, 0, 0, 0));
        progressCenter.add(progressHint, BorderLayout.SOUTH);
        progressCard.add(progressTitle, BorderLayout.NORTH);
        progressCard.add(progressCenter, BorderLayout.CENTER);

        row.add(heatCard);
        row.add(progressCard);
        return row;
    }

    private JPanel buildTimelineCard() {
        JPanel card = createCard();
        JLabel title = new JLabel("Timeline Preview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(palette.text());
        title.setBorder(new EmptyBorder(0, 0, 8, 0));

        int hourNow = Math.min(20, Math.max(6, LocalTime.now().getHour()));
        timelineSlider = new JSlider(6, 20, hourNow);
        timelineSlider.setOpaque(false);
        timelineSlider.addChangeListener(e -> previewTimeline(timelineSlider.getValue()));

        timelineLabel = new JLabel("Preview: --:--");
        timelineLabel.setForeground(palette.slate());
        countdownLabel = new JLabel("Next class countdown: --");
        countdownLabel.setForeground(palette.accent());

        JPanel meta = new JPanel(new GridLayout(1, 2));
        meta.setOpaque(false);
        meta.add(timelineLabel);
        meta.add(countdownLabel);

        card.add(title, BorderLayout.NORTH);
        card.add(timelineSlider, BorderLayout.CENTER);
        card.add(meta, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildSchedulePanel() {
        JPanel container = createCard();
        container.setPreferredSize(new Dimension(0, 320));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Schedule");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(palette.text());

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filters.setOpaque(false);
        todayFilter = createFilterToggle("Today", true);
        weekFilter = createFilterToggle("Week", false);
        filters.add(todayFilter);
        filters.add(weekFilter);

        sparklinePanel = new SparklinePanel();
        sparklinePanel.setPreferredSize(new Dimension(120, 36));
        sparklinePanel.setOpaque(false);
        filters.add(sparklinePanel);

        header.add(title, BorderLayout.WEST);
        header.add(filters, BorderLayout.EAST);

        scheduleContainer = new JPanel();
        scheduleContainer.setLayout(new BoxLayout(scheduleContainer, BoxLayout.Y_AXIS));
        scheduleContainer.setOpaque(false);
        scheduleContainer.setBorder(new EmptyBorder(4, 0, 0, 0));

        scheduleEmptyState = buildScheduleEmptyState();

        JScrollPane scroll = new JScrollPane(scheduleContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        container.add(header, BorderLayout.NORTH);
        container.add(scroll, BorderLayout.CENTER);
        return container;
    }

    private JPanel buildActionPanel() {
        JPanel container = createCard();
        JLabel title = new JLabel("Quick Actions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(palette.text());
        title.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12));
        grid.setOpaque(false);
        grid.add(createActionTile("📄 View COR", "Officially enrolled—view COR", () -> Navigation.to(this, Screen.COR)));
        grid.add(createActionTile("💳 Payments", "Pay or view receipts", () -> Navigation.to(this, Screen.PAYMENT)));
        grid.add(createActionTile("🧾 Assessment", "Review assessed fees", () -> Navigation.to(this, Screen.ASSESSMENT)));
        grid.add(createActionTile("📅 History", "Past enrollments", () -> JOptionPane.showMessageDialog(this, "History feature coming soon.")));

        container.add(title, BorderLayout.NORTH);
        container.add(grid, BorderLayout.CENTER);
        return container;
    }

    private JPanel buildFinanceEmptyState() {
        JPanel empty = new JPanel();
        empty.setOpaque(false);
        empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
        JLabel illustration = new JLabel("(•‿•)〰" );
        illustration.setForeground(palette.slate());
        illustration.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel("No assessment yet. Start enrollment to see your finances.");
        label.setForeground(palette.slate());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton cta = createGhostButton("Start Enrollment", () -> Navigation.to(this, Screen.RETURNING_BLOCK_SCHEDULE));
        cta.setAlignmentX(Component.LEFT_ALIGNMENT);
        empty.add(illustration);
        empty.add(Box.createVerticalStrut(6));
        empty.add(label);
        empty.add(Box.createVerticalStrut(8));
        empty.add(cta);
        return empty;
    }

    private JPanel buildScheduleEmptyState() {
        JPanel empty = new JPanel();
        empty.setOpaque(false);
        empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
        JLabel illustration = new JLabel("⌛ No classes yet");
        illustration.setForeground(palette.slate());
        illustration.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel("Enroll or pick a block to populate your schedule.");
        label.setForeground(palette.slate());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton cta = createGhostButton("Pick a schedule", () -> Navigation.to(this, Screen.RETURNING_BLOCK_SCHEDULE));
        cta.setAlignmentX(Component.LEFT_ALIGNMENT);
        empty.add(illustration);
        empty.add(Box.createVerticalStrut(6));
        empty.add(label);
        empty.add(Box.createVerticalStrut(8));
        empty.add(cta);
        return empty;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(palette.primary());
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 24, 12, 24));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc:14; shadowWidth:6; shadowColor:" + toRgba(palette.glow()) + "; outerFocusWidth:3; outerFocusColor:" + toHex(palette.glow()) + ";");
        attachButtonMicroInteraction(btn);
        return btn;
    }

    private JButton createGhostButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(new Color(255, 255, 255, 40));
        btn.setForeground(palette.text());
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(180, 198, 232), 1, true));
        btn.addActionListener(e -> action.run());
        attachButtonMicroInteraction(btn);
        return btn;
    }

    private JButton createActionTile(String text, String tooltip, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(palette.text());
        btn.setBackground(new Color(0xF8FAFC));
        btn.setBorder(new LineBorder(new Color(0xE2E8F0), 1, true));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setToolTipText(tooltip);
        btn.addActionListener(e -> action.run());
        attachButtonMicroInteraction(btn);
        quickActionButtons.add(btn);
        return btn;
    }

    private JPanel createCard() {
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setOpaque(true);
        card.setBackground(palette.card());
        card.setBorder(new CompoundBorder(new FlatDropShadowBorder(), new EmptyBorder(20, 24, 20, 24)));
        cards.add(card);
        return card;
    }

    private JLabel createChip(String text) {
        JLabel chip = new JLabel(text);
        chip.setOpaque(true);
        chip.setBackground(new Color(12, 92, 177, 18));
        chip.setForeground(palette.text());
        chip.setBorder(new EmptyBorder(6, 10, 6, 10));
        chip.putClientProperty(FlatClientProperties.STYLE, "arc:12;");
        return chip;
    }

    private JToggleButton createThemeToggle() {
        boolean dim = SessionManager.getInstance().getDarkMode();
        JToggleButton toggle = new JToggleButton(dim ? "Dim" : "Light", dim);
        toggle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        toggle.setFocusPainted(false);
        toggle.setBackground(new Color(0xE2E8F0));
        toggle.setBorder(new EmptyBorder(8, 12, 8, 12));
        toggle.addActionListener(e -> {
            boolean useDim = toggle.isSelected();
            SessionManager.getInstance().setDarkMode(useDim);
            toggle.setText(useDim ? "Dim" : "Light");
            palette = useDim ? DIM : LIGHT;
            applyPalette();
            loadDashboardData();
        });
        attachButtonMicroInteraction(toggle);
        return toggle;
    }

    private JToggleButton createModeToggle(String label, boolean initial, Runnable onChange) {
        JToggleButton toggle = new JToggleButton(label, initial);
        toggle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        toggle.setFocusPainted(false);
        toggle.setBackground(new Color(0xE2E8F0));
        toggle.setBorder(new EmptyBorder(8, 12, 8, 12));
        toggle.addActionListener(e -> onChange.run());
        attachButtonMicroInteraction(toggle);
        return toggle;
    }

    private JToggleButton createFilterToggle(String text, boolean selected) {
        JToggleButton btn = new JToggleButton(text, selected);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0xE2E8F0));
        btn.setBorder(new LineBorder(new Color(0xCBD5E1), 1, true));
        btn.addActionListener(e -> {
            if (btn == todayFilter && todayFilter.isSelected()) {
                weekFilter.setSelected(false);
                renderScheduleList(cachedSchedules, false);
            } else if (btn == weekFilter && weekFilter.isSelected()) {
                todayFilter.setSelected(false);
                renderScheduleList(cachedSchedules, true);
            } else {
                todayFilter.setSelected(true);
                weekFilter.setSelected(false);
                renderScheduleList(cachedSchedules, false);
            }
        });
        attachButtonMicroInteraction(btn);
        return btn;
    }

    private void attachHoverLift(JComponent c) {
        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                c.putClientProperty(FlatClientProperties.STYLE, "borderColor:#C7D2FE; background:" + toHex(new Color(255,255,255,30)) + ";");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                c.putClientProperty(FlatClientProperties.STYLE, "borderColor:#E2E8F0;");
            }
        });
    }

    private void attachButtonMicroInteraction(AbstractButton btn) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.setOpaque(true);
                btn.setBackground(btn.getBackground().brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(btn.getBackground());
            }
            @Override
            public void mousePressed(MouseEvent e) {
                btn.setOpaque(true);
                btn.setBackground(btn.getBackground().darker());
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setBackground(btn.getBackground().brighter());
            }
        });
    }

    private void playEntranceAnimation() {
        List<JComponent> targets = new ArrayList<>(cards);
        int delay = 50;
        for (int i = 0; i < targets.size(); i++) {
            JComponent comp = targets.get(i);
            comp.setVisible(false);
            int startDelay = i * delay;
            new Timer(startDelay, e -> {
                comp.setVisible(true);
                comp.setOpaque(true);
                comp.repaint();
            }).start();
        }
    }

    private void showSnackbar(String message, boolean success) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window == null) return;
        JWindow toast = new JWindow(window);
        JPanel panel = new JPanel();
        panel.setBackground(success ? new Color(16, 185, 129, 230) : new Color(239, 68, 68, 230));
        panel.setBorder(new EmptyBorder(10, 16, 10, 16));
        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        panel.add(label);
        toast.add(panel);
        toast.pack();
        int x = window.getX() + (window.getWidth() - toast.getWidth()) / 2;
        int y = window.getY() + window.getHeight() - toast.getHeight() - 60;
        toast.setLocation(x, y);
        toast.setVisible(true);
        Timer timer = new Timer(2400, e -> toast.setVisible(false));
        timer.setRepeats(false);
        timer.start();
    }

    private void loadDashboardData() {
        mainActionButton.setEnabled(false);
        SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardData doInBackground() {
                Student student = SessionManager.getInstance().getCurrentStudent();
                if (student == null) return null;

                Enrollment enrollment = enrollmentDAO.findActiveByStudent(student.getId()).orElse(null);
                Assessment assessment = null;
                Assessment lastPaid = assessmentDAO.findLatestPaidForStudent(student.getId()).orElse(null);
                Optional<Assessment> pending = assessmentDAO.findPendingForStudent(student.getId());
                if (pending.isPresent()) {
                    assessment = pending.get();
                } else if (enrollment != null) {
                    assessment = lastPaid;
                }

                Block block = null;
                if (enrollment != null && enrollment.getBlockId() != null) {
                    block = blockDAO.findById(enrollment.getBlockId()).orElse(null);
                }

                List<DashboardNotice> notices = collectNotices(enrollment, assessment);
                return new DashboardData(student, enrollment, assessment, lastPaid, block, notices);
            }

            @Override
            protected void done() {
                try {
                    render(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void render(DashboardData data) {
        if (data == null || data.student() == null) {
            welcomeLabel.setText("Welcome, Guest");
            statusBadge.setText("Not Logged In");
            return;
        }

        int hour = LocalTime.now().getHour();
        String greeting = (hour < 12 ? "Good Morning" : hour < 18 ? "Good Afternoon" : "Good Evening");
        welcomeLabel.setText(greeting + ", " + data.student().getFirstName());

        String status = "Not Enrolled";
        Color statusColor = palette.slate();
        String btnText = "Start Enrollment";
        final Runnable[] btnActionRef = new Runnable[]{() -> Navigation.to(this, Screen.RETURNING_BLOCK_SCHEDULE)};

        if (data.enrollment() != null) {
            if ("OFFICIALLY_ENROLLED".equalsIgnoreCase(data.enrollment().getStatus())) {
                status = "Officially Enrolled";
                statusColor = palette.success();
                btnText = "View Certificate";
                btnActionRef[0] = () -> Navigation.to(this, Screen.COR);
                statusBadge.setIcon(new TextIcon("✔", palette.success()));
                statusBadge.setToolTipText("Officially enrolled—view COR");
            } else {
                status = "Payment Pending";
                statusColor = palette.warning();
                btnText = "Pay Now";
                btnActionRef[0] = () -> Navigation.to(this, Screen.PAYMENT);
                statusBadge.setToolTipText("Awaiting payment");
            }
        } else {
            statusBadge.setIcon(null);
            statusBadge.setToolTipText("Not enrolled—start your flow");
        }

        statusBadge.setText(status);
        statusBadge.setForeground(statusColor);

        mainActionButton.setText(btnText);
        for (var l : mainActionButton.getActionListeners()) mainActionButton.removeActionListener(l);
        mainActionButton.addActionListener(e -> btnActionRef[0].run());
        mainActionButton.setEnabled(true);

        updateNextBestAction(data, btnText, btnActionRef[0]);
        updateProgressRing(data);

        renderFinance(data.assessment(), data.lastPaid());

        if (data.block() != null) {
            blockBadge.setText(data.block().getBlockCode());
            cachedSchedules = data.block().getSchedules();
            renderScheduleList(cachedSchedules, weekFilter != null && weekFilter.isSelected());
        } else {
            cachedSchedules = List.of();
            blockBadge.setText("--");
            nextClassTimer.setText("No active classes.");
            showScheduleEmptyState();
        }

        cachedNotices = data.notices();
        notificationButton.setBadgeCount(cachedNotices.size());
        updatePersonalizationRow(data);
        if (timelineSlider != null) {
            previewTimeline(timelineSlider.getValue());
        }
        showSnackbar("Dashboard updated", true);
    }

    private void renderFinance(Assessment assessment, Assessment lastPaid) {
        CardLayout cl = (CardLayout)((Container)financeCardContent.getParent()).getLayout();
        double due = 0;
        double paid = 0;
        String status = "Draft";

        if (assessment != null) {
            due = assessment.getTotalDue() != null ? assessment.getTotalDue().doubleValue() : 0;
            if ("PAID".equalsIgnoreCase(assessment.getStatus())) {
                paid = due;
            }
            status = assessment.getStatus();
        }

        if (due <= 0) {
            cl.show(financeCardContent.getParent(), "empty");
            financeBar.setValue(0);
            financeText.setText("₱ 0.00 / ₱ 0.00");
            updateTrendChip(0, 0);
        } else {
            cl.show(financeCardContent.getParent(), "data");
            financeText.setText(String.format("Paid: ₱ %.0f / %.0f (%s)", paid, due, status));
            int progress = (int)((paid / due) * 100);
            financeBar.setValue(progress);
            double last = (lastPaid != null && lastPaid.getTotalDue() != null) ? lastPaid.getTotalDue().doubleValue() : due;
            updateTrendChip(due, last);
        }
        financeBar.setToolTipText("Paid vs Due");
        financePaidValue = paid;
        financeDueValue = due;
        financeHistory = synthesizeFinanceHistory(due, paid);
        updateFinanceViz();
    }

    private void updateTrendChip(double current, double last) {
        trendChipRow.removeAll();
        double baseline = last <= 0 ? current : last;
        double delta = baseline == 0 ? 0 : ((current - baseline) / baseline) * 100.0;
        String direction = delta > 0.5 ? "↑" : delta < -0.5 ? "↓" : "→";
        Color tone = delta > 0 ? palette.warning() : palette.success();
        JLabel chip = createChip(String.format("Trend: %s %.1f%% vs last term", direction, delta));
        chip.setForeground(tone);
        trendChipRow.add(chip);
        trendChipRow.revalidate();
        trendChipRow.repaint();
    }

    private int[] synthesizeFinanceHistory(double due, double paid) {
        int points = 7;
        int[] series = new int[points];
        double target = Math.max(due, paid == 0 ? due : paid);
        for (int i = 0; i < points; i++) {
            double factor = (i + 1) / (double) points;
            series[i] = (int)(target * factor * 0.25);
        }
        series[points - 1] = (int)Math.max(paid, due);
        return series;
    }

    private void updateFinanceViz() {
        if (financeAreaChart != null) {
            financeAreaChart.setSeries(financeHistory);
        }
        if (financeStackedBar != null) {
            double outstanding = Math.max(0, financeDueValue - financePaidValue);
            financeStackedBar.setValues(financePaidValue, outstanding);
        }
    }

    private void renderScheduleList(List<Schedule> allSchedules, boolean weekly) {
        scheduleContainer.removeAll();
        scheduleCache.clear();
        if (allSchedules == null || allSchedules.isEmpty()) {
            showScheduleEmptyState();
            return;
        }

        Map<String, Boolean> conflictMap = detectConflicts(allSchedules);

        if (weekly) {
            Map<DayOfWeek, List<Schedule>> grouped = allSchedules.stream()
                    .collect(Collectors.groupingBy(s -> parseDayOfWeek(s.getDayPattern())));
            grouped.keySet().stream().sorted().forEach(day -> {
                JLabel dayLabel = new JLabel(day.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
                dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                dayLabel.setForeground(palette.text());
                scheduleContainer.add(dayLabel);
                grouped.get(day).stream()
                        .sorted(Comparator.comparing(Schedule::getTimeStart))
                        .forEach(s -> scheduleContainer.add(buildScheduleRow(s, conflictMap)));
                scheduleContainer.add(Box.createVerticalStrut(8));
            });
            nextClassTimer.setText("Weekly view");
        } else {
            DayOfWeek today = LocalDate.now().getDayOfWeek();
            String dayName = today.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            List<Schedule> todayClasses = allSchedules.stream()
                    .filter(s -> s.getDayPattern().toLowerCase().contains(dayName.toLowerCase()))
                    .sorted(Comparator.comparing(Schedule::getTimeStart))
                    .collect(Collectors.toList());

            if (todayClasses.isEmpty()) {
                JLabel empty = new JLabel("<html><i>No classes scheduled for " + dayName + ".</i></html>");
                empty.setForeground(palette.slate());
                empty.setBorder(new EmptyBorder(10, 0, 0, 0));
                scheduleContainer.add(empty);
                nextClassTimer.setText("Free day");
            } else {
                LocalTime now = LocalTime.now();
                boolean foundNext = false;
                for (Schedule s : todayClasses) {
                    scheduleContainer.add(buildScheduleRow(s, conflictMap));
                    scheduleContainer.add(new JSeparator());
                    if (!foundNext) {
                        try {
                            LocalTime start = LocalTime.parse(s.getTimeStart(), DateTimeFormatter.ofPattern("HH:mm"));
                            if (start.isAfter(now)) {
                                nextClassTimer.setText("Next: " + s.getSubject() + " @ " + s.getTimeStart());
                                foundNext = true;
                            }
                        } catch (Exception ignored) { }
                    }
                }
                if (!foundNext) {
                    nextClassTimer.setText("All classes done for today.");
                }
            }
        }

        updateSparkline(allSchedules);
        updateHeatmap(allSchedules, conflictMap);
        startNextClassCountdown(allSchedules);
        scheduleContainer.revalidate();
        scheduleContainer.repaint();
    }

    private JPanel buildScheduleRow(Schedule s, Map<String, Boolean> conflictMap) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(6, 0, 6, 0));
        JLabel time = new JLabel(s.getTimeStart());
        time.setFont(new Font("Consolas", Font.BOLD, 14));
        time.setForeground(palette.primary());
        time.setPreferredSize(new Dimension(70, 0));
        boolean conflict = conflictMap.getOrDefault(keyForSchedule(s), false);
        String badge = conflict ? " <span style='color:#EF4444'>(Conflict)</span>" : "";
        JLabel subj = new JLabel("<html><b>" + s.getSubject() + badge + "</b><br><span style='font-size:10px;color:gray'>" + s.getRoom() + "</span></html>");
        subj.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        row.add(time, BorderLayout.WEST);
        row.add(subj, BorderLayout.CENTER);
        attachHoverLift(row);
        return row;
    }

    private void updateSparkline(List<Schedule> schedules) {
        if (schedules == null) return;
        int[] weeklyLoad = new int[7];
        for (Schedule s : schedules) {
            DayOfWeek day = parseDayOfWeek(s.getDayPattern());
            if (day != null) {
                weeklyLoad[day.getValue() % 7] += 1;
            }
        }
        sparklinePanel.setData(weeklyLoad);
    }

    private void updateHeatmap(List<Schedule> schedules, Map<String, Boolean> conflictMap) {
        if (heatmapPanel == null || schedules == null) return;
        int[][] load = new int[7][8];
        boolean[][] conflicts = new boolean[7][8];
        for (Schedule s : schedules) {
            DayOfWeek day = parseDayOfWeek(s.getDayPattern());
            int dayIdx = (day.getValue() % 7);
            int start = parseHour(s.getTimeStart());
            int end = parseHour(s.getTimeEnd());
            for (int h = start; h <= end; h += 2) {
                int slot = (h - 8) / 2;
                if (slot >= 0 && slot < 8) {
                    load[dayIdx][slot] += 1;
                    boolean isConflict = conflictMap.getOrDefault(keyForSchedule(s), false);
                    conflicts[dayIdx][slot] = conflicts[dayIdx][slot] || isConflict;
                }
            }
        }
        heatmapPanel.setData(load, conflicts);
    }

    private void startNextClassCountdown(List<Schedule> schedules) {
        if (countdownTimer != null) countdownTimer.stop();
        if (countdownLabel == null) return;
        Schedule next = findNextSchedule(schedules);
        if (next == null) {
            countdownLabel.setText("Next class countdown: none today");
            return;
        }
        Runnable updater = () -> {
            try {
                LocalTime now = LocalTime.now();
                LocalTime start = LocalTime.parse(next.getTimeStart(), DateTimeFormatter.ofPattern("HH:mm"));
                if (!now.isBefore(start)) {
                    countdownLabel.setText("Next class countdown: started");
                    return;
                }
                Duration d = Duration.between(now, start);
                long mins = d.toMinutes();
                long secs = d.minusMinutes(mins).getSeconds();
                countdownLabel.setText("Next class in " + mins + "m " + secs + "s");
            } catch (Exception ex) {
                countdownLabel.setText("Next class countdown: --");
            }
        };
        updater.run();
        countdownTimer = new Timer(1000, e -> updater.run());
        countdownTimer.start();
    }

    private DayOfWeek parseDayOfWeek(String dayPattern) {
        if (dayPattern == null) return DayOfWeek.MONDAY;
        String normalized = dayPattern.toLowerCase(Locale.ENGLISH);
        for (DayOfWeek d : DayOfWeek.values()) {
            if (normalized.contains(d.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toLowerCase(Locale.ENGLISH))) {
                return d;
            }
        }
        return DayOfWeek.MONDAY;
    }

    private int parseHour(String time) {
        try {
            LocalTime t = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            return t.getHour();
        } catch (Exception e) {
            return 8;
        }
    }

    private Schedule findNextSchedule(List<Schedule> schedules) {
        if (schedules == null) return null;
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        LocalTime now = LocalTime.now();
        return schedules.stream()
                .filter(s -> parseDayOfWeek(s.getDayPattern()) == today)
                .filter(s -> {
                    try {
                        return LocalTime.parse(s.getTimeStart(), DateTimeFormatter.ofPattern("HH:mm")).isAfter(now);
                    } catch (Exception ex) { return false; }
                })
                .sorted(Comparator.comparing(Schedule::getTimeStart))
                .findFirst()
                .orElse(null);
    }

    private void previewTimeline(int hour) {
        if (timelineLabel == null) return;
        timelineLabel.setText(String.format("Preview: %02d:00", hour));
        if (cachedSchedules == null) return;
        LocalTime preview = LocalTime.of(hour, 0);
        Schedule match = cachedSchedules.stream()
                .filter(s -> parseDayOfWeek(s.getDayPattern()) == LocalDate.now().getDayOfWeek())
                .filter(s -> {
                    try {
                        LocalTime start = LocalTime.parse(s.getTimeStart(), DateTimeFormatter.ofPattern("HH:mm"));
                        LocalTime end = LocalTime.parse(s.getTimeEnd(), DateTimeFormatter.ofPattern("HH:mm"));
                        return !preview.isBefore(start) && !preview.isAfter(end);
                    } catch (Exception ex) { return false; }
                })
                .findFirst()
                .orElse(null);
        if (match != null) {
            timelineLabel.setText(String.format("Preview: %02d:00 • %s (%s)", hour, match.getSubject(), match.getRoom()));
        }
    }

    private Map<String, Boolean> detectConflicts(List<Schedule> schedules) {
        Map<String, Boolean> conflictMap = new HashMap<>();
        Map<DayOfWeek, List<Schedule>> byDay = schedules.stream()
                .collect(Collectors.groupingBy(s -> parseDayOfWeek(s.getDayPattern())));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        byDay.forEach((day, list) -> {
            list.sort(Comparator.comparing(Schedule::getTimeStart));
            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    try {
                        LocalTime startA = LocalTime.parse(list.get(i).getTimeStart(), fmt);
                        LocalTime endA = LocalTime.parse(list.get(i).getTimeEnd(), fmt);
                        LocalTime startB = LocalTime.parse(list.get(j).getTimeStart(), fmt);
                        LocalTime endB = LocalTime.parse(list.get(j).getTimeEnd(), fmt);
                        boolean overlaps = !startA.isAfter(endB) && !startB.isAfter(endA);
                        if (overlaps) {
                            conflictMap.put(keyForSchedule(list.get(i)), true);
                            conflictMap.put(keyForSchedule(list.get(j)), true);
                        }
                    } catch (Exception ignored) { }
                }
            }
        });
        return conflictMap;
    }

    private String keyForSchedule(Schedule s) {
        return (s.getDayPattern() + "|" + s.getTimeStart() + "|" + s.getTimeEnd() + "|" + s.getSubject()).toLowerCase(Locale.ENGLISH);
    }

    private void showScheduleEmptyState() {
        scheduleContainer.removeAll();
        scheduleContainer.add(scheduleEmptyState);
        scheduleContainer.revalidate();
        scheduleContainer.repaint();
    }

    private void updatePersonalizationRow(DashboardData data) {
        personalizationRow.removeAll();
        String deadline = data.assessment() != null ? "Upcoming deadline: payment " + data.assessment().getStatus() : "Upcoming deadlines: none";
        String reminder = data.assessment() != null ? "Payment reminder: ₱" + (data.assessment().getTotalDue() == null ? 0 : data.assessment().getTotalDue().intValue()) : "Payment reminder: not assessed";
        String adviser = data.enrollment() != null ? "Adviser note: you're on track" : "Adviser note: start enrollment";
        personalizationRow.add(createChip(deadline));
        personalizationRow.add(createChip(reminder));
        personalizationRow.add(createChip(adviser));
        personalizationRow.revalidate();
        personalizationRow.repaint();
    }

    private void updateProgressRing(DashboardData data) {
        if (progressRing == null) return;
        boolean profileDone = data.student() != null;
        boolean docsDone = data.enrollment() != null;
        boolean assessDone = data.assessment() != null;
        boolean payDone = data.assessment() != null && "PAID".equalsIgnoreCase(data.assessment().getStatus());
        if (data.enrollment() != null && "OFFICIALLY_ENROLLED".equalsIgnoreCase(data.enrollment().getStatus())) {
            payDone = true;
        }
        progressRing.setStates(profileDone, docsDone, assessDone, payDone);
    }

    private void updateNextBestAction(DashboardData data, String btnText, Runnable action) {
        if (nextActionTitle == null || nextActionButton == null) return;
        String copy = "Let's keep you moving.";
        String label = btnText;
        final Runnable[] targetRef = new Runnable[]{action};

        if (data.assessment() != null && data.assessment().getTotalDue() != null && data.assessment().getTotalDue().doubleValue() > 0) {
            copy = "Settle your balance to finalize enrollment.";
            label = "Pay balance";
            targetRef[0] = () -> Navigation.to(this, Screen.PAYMENT);
        } else if (data.enrollment() == null) {
            copy = "Pick a block and preview your schedule.";
            label = "Start enrollment";
            targetRef[0] = () -> Navigation.to(this, Screen.RETURNING_BLOCK_SCHEDULE);
        } else if ("OFFICIALLY_ENROLLED".equalsIgnoreCase(data.enrollment().getStatus())) {
            copy = "You’re official. Grab your COR.";
            label = "Download COR";
            targetRef[0] = () -> Navigation.to(this, Screen.COR);
        }

        nextActionTitle.setText("Next Best Action");
        nextActionSubtitle.setText(copy);
        nextActionButton.setText(label);
        for (var l : nextActionButton.getActionListeners()) nextActionButton.removeActionListener(l);
        nextActionButton.addActionListener(e -> targetRef[0].run());

        boolean duplicateCta = label.equalsIgnoreCase(mainActionButton.getText());
        if (nextActionPanel != null) {
            nextActionPanel.setVisible(!duplicateCta);
        }
    }

    private void toggleNotificationSheet() {
        if (notificationSheet != null && notificationSheet.isShowing()) {
            notificationSheet.dispose();
            notificationSheet = null;
            return;
        }
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window == null) return;
        notificationSheet = new JDialog(window, "Notifications", Dialog.ModalityType.MODELESS);
        notificationSheet.setUndecorated(true);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new CompoundBorder(new LineBorder(new Color(0xCBD5E1), 1, true), new EmptyBorder(12, 16, 12, 16)));
        content.setBackground(palette.card());
        for (DashboardNotice n : cachedNotices) {
            JLabel lbl = new JLabel("<html><b>" + n.title() + "</b><br>" + n.detail() + "</html>");
            lbl.setForeground(palette.text());
            lbl.setBorder(new EmptyBorder(6, 0, 6, 0));
            content.add(lbl);
            content.add(new JSeparator());
        }
        if (cachedNotices.isEmpty()) {
            JLabel empty = new JLabel("No recent notices");
            empty.setForeground(palette.slate());
            content.add(empty);
        }
        notificationSheet.setContentPane(content);
        notificationSheet.pack();
        Point base = window.getLocationOnScreen();
        notificationSheet.setLocation(base.x + window.getWidth() - notificationSheet.getWidth() - 24, base.y + 80);
        notificationSheet.setVisible(true);
    }

    private List<DashboardNotice> collectNotices(Enrollment enrollment, Assessment assessment) {
        List<DashboardNotice> list = new ArrayList<>();
        if (assessment != null) {
            list.add(new DashboardNotice("Assessment", "Total due ₱" + (assessment.getTotalDue() == null ? 0 : assessment.getTotalDue().intValue())));
            list.add(new DashboardNotice("Status", assessment.getStatus()));
        }
        if (enrollment != null) {
            list.add(new DashboardNotice("Enrollment", enrollment.getStatus()));
        }
        return list;
    }

    private void applyPalette() {
        setBackground(palette.bg());
        mainContent.setBackground(palette.bg());
        welcomeLabel.setForeground(palette.text());
        dateLabel.setForeground(palette.slate());
        cards.forEach(card -> card.setBackground(palette.card()));
        quickActionButtons.forEach(btn -> btn.setForeground(palette.text()));
        if (financeAreaChart != null) financeAreaChart.repaint();
        if (financeStackedBar != null) financeStackedBar.repaint();
        if (heatmapPanel != null) heatmapPanel.repaint();
        if (progressRing != null) progressRing.repaint();
        mainContent.revalidate();
        mainContent.repaint();
    }

    private void applyDensity(boolean toggle) {
        compactMode = toggle ? !compactMode : compactMode;
        int padding = compactMode ? 18 : 32;
        int spacing = compactMode ? 14 : 24;
        cards.clear();
        quickActionButtons.clear();
        mainContent.setBorder(new EmptyBorder(padding, padding + 8, padding, padding + 8));
        mainContent.removeAll();
        mainContent.add(buildHeroSection());
        mainContent.add(Box.createVerticalStrut(compactMode ? 8 : 12));
        mainContent.add(buildNextBestAction());
        mainContent.add(Box.createVerticalStrut(compactMode ? 12 : 20));
        mainContent.add(buildStatsRow());
        mainContent.add(Box.createVerticalStrut(spacing - 6));
        mainContent.add(buildFinanceVizCard());
        mainContent.add(Box.createVerticalStrut(spacing - 6));
        mainContent.add(buildHeatmapAndProgressRow());
        mainContent.add(Box.createVerticalStrut(spacing - 6));
        mainContent.add(buildTimelineCard());
        mainContent.add(Box.createVerticalStrut(spacing));
        JPanel lowerSplit = new JPanel(new GridLayout(1, 2, spacing, 0));
        lowerSplit.setOpaque(false);
        schedulePanel = buildSchedulePanel();
        actionPanel = buildActionPanel();
        lowerSplit.add(schedulePanel);
        lowerSplit.add(actionPanel);
        mainContent.add(lowerSplit);
        mainContent.revalidate();
        mainContent.repaint();
        playEntranceAnimation();
    }

    private void applyFocusMode() {
        focusMode = !focusMode;
        if (actionPanel != null) {
            actionPanel.setVisible(!focusMode);
        }
        if (nextActionPanel != null) {
            nextActionPanel.setVisible(!focusMode);
        }
        revalidate();
        repaint();
        showSnackbar(focusMode ? "Focus mode on" : "Focus mode off", true);
    }

    @Override
    public void onEnter(NavigationContext context) {
        prefetchAssessment();
        loadDashboardData();
    }

    private void prefetchAssessment() {
        Student student = SessionManager.getInstance().getCurrentStudent();
        if (student == null) return;
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                assessmentDAO.findPendingForStudent(student.getId());
                assessmentDAO.findLatestPaidForStudent(student.getId());
                return null;
            }
        };
        worker.execute();
    }

    @Override
    public void onLeave() {}

    private static String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private static String toRgba(Color c) {
        return String.format("rgba(%d,%d,%d,%d)", c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    private record Palette(Color bg, Color card, Color primary, Color accent, Color warning, Color success, Color slate, Color text, Color glow) {}
    private record DashboardData(Student student, Enrollment enrollment, Assessment assessment, Assessment lastPaid, Block block, List<DashboardNotice> notices) {}
    private record DashboardNotice(String title, String detail) {}

    private static class TextIcon implements Icon {
        private final String text;
        private final Color color;
        public TextIcon(String text, Color color) { this.text = text; this.color = color; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.drawString(text, x, y + 10);
        }
        public int getIconWidth() { return 16; }
        public int getIconHeight() { return 16; }
    }

    private static class BadgeButton extends JButton {
        private int badgeCount;
        public BadgeButton(String text) {
            super(text);
            setOpaque(true);
            setBackground(new Color(0xE2E8F0));
            setBorder(new EmptyBorder(8, 12, 8, 12));
            setFocusPainted(false);
        }
        public void setBadgeCount(int count) {
            this.badgeCount = count;
            repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (badgeCount > 0) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = 16;
                int x = getWidth() - size - 6;
                int y = 6;
                g2.setColor(new Color(0xEF4444));
                g2.fillOval(x, y, size, size);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont().deriveFont(Font.BOLD, 10f));
                String txt = String.valueOf(Math.min(badgeCount, 9));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(txt, x + (size - fm.stringWidth(txt)) / 2, y + (size + fm.getAscent() - fm.getDescent()) / 2 - 1);
                g2.dispose();
            }
        }
    }

    private static class SparklinePanel extends JPanel {
        private int[] data = new int[7];
        public void setData(int[] data) {
            this.data = data;
            repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.length == 0) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int max = 1;
            for (int v : data) max = Math.max(max, v);
            int step = Math.max(1, w / (data.length - 1));
            int prevX = 0;
            int prevY = h - (int)((data[0] / (double)max) * (h - 6)) - 3;
            g2.setColor(new Color(12, 92, 177, 80));
            for (int i = 1; i < data.length; i++) {
                int x = i * step;
                int y = h - (int)((data[i] / (double)max) * (h - 6)) - 3;
                g2.drawLine(prevX, prevY, x, y);
                prevX = x;
                prevY = y;
            }
            g2.dispose();
        }
    }

    private class FinanceAreaChart extends JPanel {
        private int[] series = new int[0];
        private int hoverIndex = -1;
        public void setSeries(int[] data) {
            this.series = data == null ? new int[0] : data;
            repaint();
        }
        public FinanceAreaChart() {
            setOpaque(false);
            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (series.length == 0) return;
                    int step = Math.max(1, getWidth() / Math.max(1, series.length - 1));
                    hoverIndex = Math.min(series.length - 1, Math.max(0, e.getX() / step));
                    repaint();
                }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (series == null || series.length == 0) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int max = 1;
            for (int v : series) max = Math.max(max, v);
            int step = Math.max(1, w / Math.max(1, series.length - 1));
            Polygon poly = new Polygon();
            poly.addPoint(0, h);
            for (int i = 0; i < series.length; i++) {
                int x = i * step;
                int y = h - (int)((series[i] / (double)max) * (h - 10)) - 4;
                poly.addPoint(x, y);
            }
            poly.addPoint((series.length - 1) * step, h);
            GradientPaint gp = new GradientPaint(0, 0, new Color(palette.primary().getRed(), palette.primary().getGreen(), palette.primary().getBlue(), 120), 0, h, new Color(palette.accent().getRed(), palette.accent().getGreen(), palette.accent().getBlue(), 30));
            g2.setPaint(gp);
            g2.fill(poly);
            g2.setColor(palette.primary());
            g2.setStroke(new BasicStroke(2f));
            int prevX = 0;
            int prevY = h - (int)((series[0] / (double)max) * (h - 10)) - 4;
            for (int i = 1; i < series.length; i++) {
                int x = i * step;
                int y = h - (int)((series[i] / (double)max) * (h - 10)) - 4;
                g2.drawLine(prevX, prevY, x, y);
                prevX = x;
                prevY = y;
            }
            if (hoverIndex >= 0 && hoverIndex < series.length) {
                int x = hoverIndex * step;
                int y = h - (int)((series[hoverIndex] / (double)max) * (h - 10)) - 4;
                g2.setColor(palette.card());
                g2.fillRoundRect(x - 24, y - 32, 48, 22, 8, 8);
                g2.setColor(palette.text());
                g2.drawString("₱" + series[hoverIndex], x - 18, y - 16);
                g2.setColor(palette.accent());
                g2.fillOval(x - 5, y - 5, 10, 10);
            }
            g2.dispose();
        }
    }

    private class FinanceStackedBar extends JPanel {
        private double paid;
        private double due;
        public FinanceStackedBar() {
            setOpaque(false);
            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) { repaint(); }
            });
        }
        public void setValues(double paid, double due) {
            this.paid = paid;
            this.due = due;
            repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth() - 20;
            int h = getHeight() - 40;
            int x = 10;
            int y = (getHeight() - h) / 2;
            double total = Math.max(1, paid + due);
            int paidW = (int)(w * (paid / total));
            int dueW = w - paidW;
            g2.setColor(new Color(16, 185, 129, 200));
            g2.fillRoundRect(x, y, paidW, h, 12, 12);
            g2.setColor(new Color(239, 68, 68, 160));
            g2.fillRoundRect(x + paidW, y, dueW, h, 12, 12);
            g2.setColor(palette.text());
            g2.drawString("Paid", x + 6, y - 6);
            g2.drawString("Due", x + paidW + 6, y - 6);
            g2.drawString(String.format("₱%.0f", paid), x + 6, y + h / 2);
            g2.drawString(String.format("₱%.0f", due), x + paidW + 6, y + h / 2);
            g2.dispose();
        }
    }

    private class HeatmapPanel extends JPanel {
        private int[][] load = new int[7][8];
        private boolean[][] conflicts = new boolean[7][8];
        private final Timer pulseTimer;
        private int pulsePhase = 0;
        public HeatmapPanel() {
            setOpaque(false);
            pulseTimer = new Timer(420, e -> { pulsePhase = (pulsePhase + 1) % 3; repaint(); });
            pulseTimer.start();
        }
        public void setData(int[][] load, boolean[][] conflicts) {
            this.load = load == null ? new int[7][8] : load;
            this.conflicts = conflicts == null ? new boolean[7][8] : conflicts;
            repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int cellW = w / 8;
            int cellH = h / 7;
            int max = 1;
            for (int[] row : load) {
                for (int v : row) max = Math.max(max, v);
            }
            for (int d = 0; d < 7; d++) {
                for (int c = 0; c < 8; c++) {
                    float alpha = load[d][c] / (float)max;
                    Color base = new Color(palette.primary().getRed(), palette.primary().getGreen(), palette.primary().getBlue(), (int)(80 + alpha * 100));
                    int x = c * cellW + 4;
                    int y = d * cellH + 4;
                    g2.setColor(base);
                    g2.fillRoundRect(x, y, cellW - 8, cellH - 8, 12, 12);
                    if (conflicts[d][c]) {
                        int pulse = 3 + pulsePhase * 2;
                        g2.setColor(new Color(239, 68, 68, 180));
                        g2.setStroke(new BasicStroke(2f));
                        g2.drawRoundRect(x - pulse, y - pulse, cellW - 8 + pulse * 2, cellH - 8 + pulse * 2, 12, 12);
                    }
                }
            }
            g2.setColor(palette.slate());
            g2.drawString("Hours 8-22", 6, 12);
            g2.dispose();
        }
    }

    private class ProgressRing extends JPanel {
        private boolean[] states = new boolean[4];
        public ProgressRing() { setOpaque(false); }
        public void setStates(boolean p, boolean d, boolean a, boolean pay) {
            states[0] = p; states[1] = d; states[2] = a; states[3] = pay; repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight()) - 16;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            int sweep = 360 / states.length;
            for (int i = 0; i < states.length; i++) {
                g2.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(states[i] ? palette.success() : new Color(palette.slate().getRed(), palette.slate().getGreen(), palette.slate().getBlue(), 80));
                g2.drawArc(x, y, size, size, 90 + i * sweep, sweep - 12);
                int tickX = (int)(getWidth() / 2 + Math.cos(Math.toRadians(90 + i * sweep)) * size / 2.1);
                int tickY = (int)(getHeight() / 2 - Math.sin(Math.toRadians(90 + i * sweep)) * size / 2.1);
                g2.fillOval(tickX - 6, tickY - 6, 12, 12);
            }
            g2.dispose();
        }
    }

    private static class GradientHeroPanel extends JPanel {
        private Point parallax = new Point(0, 0);
        public void setParallax(Point p) {
            this.parallax = p;
            repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int dx = (int) ((parallax.getX() - w / 2.0) * 0.02);
            int dy = (int) ((parallax.getY() - h / 2.0) * 0.02);
            GradientPaint gp = new GradientPaint(dx, dy, new Color(12, 92, 177, 200), w, h, new Color(14, 165, 233, 140));
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w, h, 28, 28);
            g2.dispose();
        }
    }
}