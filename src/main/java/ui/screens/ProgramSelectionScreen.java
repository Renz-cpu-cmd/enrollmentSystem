package ui.screens;

/**
 * Program selection screen for choosing academic program/track.
 *
 * <p>Extends `JPanel` and implements `ScreenView` to integrate with router
 * logic in the application frame.</p>
 */

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.components.FlatTextField;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import ui.MainFrame;
import ui.MobileFrame;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
import ui.components.WizardHeader;
import ui.theme.Theme;
import util.Navigation;
import util.SessionManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProgramSelectionScreen extends JPanel implements ScreenView {

    private static final Color UNI_BLUE = new Color(0x0C5CB1);
    private static final Color GOLD = new Color(0xDAA520);
    private static final Color SLATE = new Color(0x64748B);
    private static final Color LIGHT_BG = new Color(0xF8FAFC);
    private static final Color BORDER_MUTED = new Color(0xE2E8F0);

    private static final List<String> COLLEGES = List.of(
        "All Colleges",
        "College of Computing",
        "College of Nursing",
        "College of Business",
        "College of Education"
    );

    private static final List<String> FILTERS = List.of("All", "Board Programs", "Non-Board");

    private static final List<Course> COURSES = List.of(
        new Course("College of Computing", "BSIT", "Bachelor of Science in Information Technology",
            "Studio-driven build sprints, UX immersion, and DevOps readiness for future product engineers.",
            "₱25,000 / sem", "4 Years", "Tech",
            List.of("Software Engineer", "DevOps Engineer", "UI/UX Developer"),
            new Color(12, 92, 177), false),
        new Course("College of Computing", "BSCS", "Bachelor of Science in Computer Science",
            "Machine learning research, data science pipelines, and algorithm mastery with capstone labs.",
            "₱26,500 / sem", "4 Years", "Research",
            List.of("Data Scientist", "ML Engineer", "Research Programmer"),
            new Color(77, 107, 255), false),
        new Course("College of Nursing", "BSN", "Bachelor of Science in Nursing",
            "Clinical immersion with simulation labs and international board review preparation.",
            "₱32,000 / sem", "4 Years", "Board Exam",
            List.of("Registered Nurse", "Clinical Instructor", "Healthcare Specialist"),
            new Color(0, 150, 136), true),
        new Course("College of Business", "BSACCY", "Bachelor of Science in Accountancy",
            "CPA-track curriculum covering audit analytics, taxation law, and digital ERP practice.",
            "₱28,500 / sem", "4 Years", "CPA Ready",
            List.of("CPA", "Audit Associate", "Tax Consultant"),
            new Color(217, 119, 6), true),
        new Course("College of Business", "BSBA", "Bachelor of Science in Business Administration",
            "Strategy, finance, and growth marketing studios run with real startup case partners.",
            "₱24,000 / sem", "4 Years", "Enterprise",
            List.of("Business Analyst", "Marketing Strategist", "Operations Lead"),
            new Color(244, 114, 182), false),
        new Course("College of Education", "BSEd Psy", "Bachelor of Secondary Education major in Psychology",
            "Learning sciences, cognitive development, and practicum placements in partner schools.",
            "₱22,500 / sem", "4 Years", "Humanities",
            List.of("Educator", "Curriculum Designer", "Guidance Associate"),
            new Color(99, 102, 241), false)
    );

    private static final Map<String, Course> COURSE_LOOKUP = new LinkedHashMap<>();

    static {
        COURSES.forEach(course -> COURSE_LOOKUP.put(course.code(), course));
    }

    private FlatTextField searchField;
    private JPanel cardsContainer;
    private final ButtonGroup collegeGroup = new ButtonGroup();
    private final ButtonGroup filterGroup = new ButtonGroup();
    private final CardLayout prospectusLayout = new CardLayout();
    private JPanel prospectusStack;
    private JLabel prospectusTitle;
    private JTextArea prospectusDescription;
    private JPanel careerPanel;
    private JLabel tuitionLabel;
    private JLabel durationLabel;
    private JButton selectButton;
    private JButton backButton;
    private JButton nextButton;
    private JScrollPane gridScrollPane;

    private String activeCollege = COLLEGES.get(0);
    private String activeFilter = FILTERS.get(0);
    private Course selectedCourse;

    public ProgramSelectionScreen() {
        setLayout(new BorderLayout());
        setBackground(LIGHT_BG);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(new WizardHeader(3), BorderLayout.NORTH);
        add(buildDashboard(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        restoreSessionSelection();
        renderCourseGrid();
        updateProspectus();
    }

    private JComponent buildDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout(0, 12));
        dashboard.setOpaque(false);
        dashboard.add(buildHero(), BorderLayout.NORTH);
        dashboard.add(buildMainContent(), BorderLayout.CENTER);
        return dashboard;
    }

    private JComponent buildHero() {
        JPanel hero = new JPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setOpaque(false);
        hero.setBorder(new EmptyBorder(16, 8, 16, 8));

        JLabel title = new JLabel("Academic Offerings");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(15, 23, 42));

        searchField = new FlatTextField();
        searchField.setColumns(30);
        searchField.setPlaceholderText("Search programs, keywords, or codes...");
        searchField.setTrailingIcon(new FlatSearchIcon());
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search programs, keywords, or codes...");
        searchField.putClientProperty(FlatClientProperties.STYLE,
            "arc:999; margin:10,16,10,16; borderWidth:1; borderColor:#CBD5E1;" +
                "innerFocusWidth:1; focusWidth:1; background:#FFFFFF;" +
                "leadingIconGap:12; trailingIconGap:12;");
        searchField.getDocument().addDocumentListener(createSearchListener());

        JPanel searchWrap = new JPanel();
        searchWrap.setOpaque(false);
        searchWrap.setLayout(new BoxLayout(searchWrap, BoxLayout.X_AXIS));
        searchWrap.add(Box.createHorizontalGlue());
        searchWrap.add(searchField);
        searchWrap.add(Box.createHorizontalGlue());

        hero.add(title);
        hero.add(Box.createVerticalStrut(12));
        hero.add(searchWrap);
        hero.add(Box.createVerticalStrut(12));
        hero.add(buildFilterChips());
        hero.add(Box.createVerticalStrut(8));
        hero.add(buildCollegeChips());
        return hero;
    }

    private JComponent buildMainContent() {
        JPanel main = new JPanel(new BorderLayout(16, 16));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(8, 0, 0, 0));

        cardsContainer = new JPanel(new GridLayout(0, 2, 16, 16));
        cardsContainer.setOpaque(false);

        gridScrollPane = new JScrollPane(cardsContainer);
        gridScrollPane.setBorder(BorderFactory.createEmptyBorder());
        gridScrollPane.getViewport().setOpaque(false);
        gridScrollPane.setOpaque(false);
        gridScrollPane.getVerticalScrollBar().setUnitIncrement(18);

        prospectusStack = new JPanel(prospectusLayout);
        prospectusStack.setPreferredSize(new Dimension(350, 0));
        prospectusStack.setBorder(new CompoundBorder(new LineBorder(BORDER_MUTED, 1, true), new EmptyBorder(24, 24, 24, 24)));
        prospectusStack.setBackground(Color.WHITE);

        prospectusTitle = new JLabel();
        prospectusTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        prospectusTitle.setForeground(new Color(23, 37, 84));

        prospectusDescription = new JTextArea();
        prospectusDescription.setWrapStyleWord(true);
        prospectusDescription.setLineWrap(true);
        prospectusDescription.setEditable(false);
        prospectusDescription.setOpaque(false);
        prospectusDescription.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        prospectusDescription.setForeground(new Color(71, 85, 105));

        careerPanel = new JPanel();
        careerPanel.setOpaque(false);
        careerPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 8, 8));

        tuitionLabel = new JLabel();
        tuitionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tuitionLabel.setForeground(UNI_BLUE);

        durationLabel = new JLabel();
        durationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        durationLabel.setForeground(new Color(51, 65, 85));

        selectButton = new JButton("Select This Program");
        selectButton.putClientProperty(FlatClientProperties.STYLE,
            "arc:18; background:#0C5CB1; foreground:#FFFFFF; font:+1;" +
                "hoverBackground:#0f6ed8; pressedBackground:#0a4f8d; shadowColor:#0C5CB1; shadowWidth:8; shadowOpacity:25;" +
                "focusWidth:2; innerFocusWidth:1;");
        selectButton.setBorder(new EmptyBorder(12, 18, 12, 18));
        selectButton.setEnabled(false);
        selectButton.addActionListener(e -> {
            if (selectedCourse != null) {
                handleSelection(selectedCourse);
                if (gridScrollPane != null) {
                    gridScrollPane.getVerticalScrollBar().setValue(gridScrollPane.getVerticalScrollBar().getMaximum());
                }
            }
        });

        prospectusStack.add(buildProspectusPlaceholder(), "placeholder");
        prospectusStack.add(buildProspectusDetail(), "details");

        main.add(gridScrollPane, BorderLayout.CENTER);
        main.add(prospectusStack, BorderLayout.EAST);
        return main;
    }

    private JPanel buildProspectusPlaceholder() {
        JPanel placeholder = new JPanel(new BorderLayout());
        placeholder.setOpaque(false);
        JLabel icon = new JLabel("\uD83D\uDCC4", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 42));
        JLabel text = new JLabel("Select a course to view its prospectus", SwingConstants.CENTER);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        text.setForeground(SLATE);
        placeholder.add(icon, BorderLayout.CENTER);
        placeholder.add(text, BorderLayout.SOUTH);
        return placeholder;
    }

    private JPanel buildProspectusDetail() {
        JPanel detail = new JPanel();
        detail.setOpaque(false);
        detail.setLayout(new BoxLayout(detail, BoxLayout.Y_AXIS));

        detail.add(prospectusTitle);
        detail.add(Box.createVerticalStrut(8));
        detail.add(prospectusDescription);
        detail.add(Box.createVerticalStrut(16));

        JLabel careerHeader = new JLabel("Career Opportunities");
        careerHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        careerHeader.setForeground(new Color(30, 41, 59));
        detail.add(careerHeader);
        detail.add(Box.createVerticalStrut(8));
        detail.add(careerPanel);
        detail.add(Box.createVerticalStrut(12));

        JPanel tuitionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tuitionRow.setOpaque(false);
        tuitionRow.add(new JLabel("\uD83D\uDCB0"));
        tuitionRow.add(tuitionLabel);

        JPanel durationRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        durationRow.setOpaque(false);
        durationRow.add(new JLabel("\u23F1"));
        durationRow.add(durationLabel);

        detail.add(tuitionRow);
        detail.add(Box.createVerticalStrut(6));
        detail.add(durationRow);
        detail.add(Box.createVerticalStrut(18));
        detail.add(selectButton);
        detail.add(Box.createVerticalGlue());
        return detail;
    }

    private JPanel buildFilterChips() {
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        chips.setOpaque(false);
        for (String filter : FILTERS) {
            JToggleButton btn = new JToggleButton(filter);
            btn.putClientProperty(FlatClientProperties.STYLE,
                "arc:999; margin:6,14,6,14; background:#FFFFFF; borderColor:#CBD5E1;" +
                    "selectedBackground:#0C5CB1; selectedForeground:#FFFFFF;" +
                    "foreground:#0F172A; borderWidth:1; focusWidth:1;");
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                activeFilter = filter;
                renderCourseGrid();
            });
            if (filter.equals(activeFilter)) {
                btn.setSelected(true);
            }
            filterGroup.add(btn);
            chips.add(btn);
        }
        return chips;
    }

    private JPanel buildCollegeChips() {
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        chips.setOpaque(false);
        for (String college : COLLEGES) {
            JToggleButton btn = new JToggleButton(college);
            btn.putClientProperty(FlatClientProperties.STYLE,
                "arc:16; margin:6,12,6,12; background:#FFFFFF; borderColor:#CBD5E1;" +
                    "selectedBackground:#0C5CB1; selectedForeground:#FFFFFF;" +
                    "foreground:#0F172A; borderWidth:1; focusWidth:1;");
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                activeCollege = college;
                renderCourseGrid();
            });
            if (college.equals(activeCollege)) {
                btn.setSelected(true);
            }
            collegeGroup.add(btn);
            chips.add(btn);
        }
        return chips;
    }

    private JComponent createCourseCard(Course course) {
        boolean isSelected = selectedCourse != null && selectedCourse.code().equals(course.code());

        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(buildCardBorder(isSelected));
        card.setPreferredSize(new Dimension(280, 190));

        JLabel codeBadge = new JLabel(course.code(), SwingConstants.CENTER);
        codeBadge.setOpaque(true);
        codeBadge.setBackground(UNI_BLUE);
        codeBadge.setForeground(Color.WHITE);
        codeBadge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        codeBadge.setPreferredSize(new Dimension(48, 48));
        codeBadge.putClientProperty(FlatClientProperties.STYLE, "arc:12");

        JLabel title = new JLabel(course.title());
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(30, 41, 59));

        JLabel category = new JLabel(course.category());
        category.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        category.setForeground(SLATE);

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        header.add(codeBadge, BorderLayout.WEST);

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.add(title);
        textStack.add(Box.createVerticalStrut(2));
        textStack.add(category);
        header.add(textStack, BorderLayout.CENTER);

        JTextArea desc = new JTextArea(course.description());
        desc.setWrapStyleWord(true);
        desc.setLineWrap(true);
        desc.setEditable(false);
        desc.setOpaque(false);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        desc.setForeground(new Color(71, 85, 105));
        desc.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel tags = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tags.setOpaque(false);
        tags.add(makeTag(course.badge()));
        tags.add(makeTag(course.duration()));

        card.add(header, BorderLayout.NORTH);
        card.add(desc, BorderLayout.CENTER);
        card.add(tags, BorderLayout.SOUTH);

        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleSelection(course);
            }
        };
        installCardListener(card, adapter);
        return card;
    }

    private JLabel makeTag(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(new Color(248, 250, 252));
        label.setForeground(new Color(51, 65, 85));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setBorder(new EmptyBorder(4, 10, 4, 10));
        label.putClientProperty(FlatClientProperties.STYLE, "arc:999");
        return label;
    }

    private Border buildCardBorder(boolean isSelected) {
        Border inner = new CompoundBorder(new LineBorder(isSelected ? UNI_BLUE : BORDER_MUTED, isSelected ? 2 : 1, true), new EmptyBorder(18, 18, 18, 18));
        return new CompoundBorder(new FlatDropShadowBorder(), new CompoundBorder(new LineBorder(GOLD, 4, false), inner));
    }

    private void installCardListener(Component component, MouseAdapter adapter) {
        component.addMouseListener(adapter);
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                installCardListener(child, adapter);
            }
        }
    }

    private void renderCourseGrid() {
        cardsContainer.removeAll();
        String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        COURSES.stream()
            .filter(this::filterByCollege)
            .filter(this::filterByBoard)
            .filter(course -> matchesQuery(course, query))
            .forEach(course -> cardsContainer.add(createCourseCard(course)));

        if (cardsContainer.getComponentCount() == 0) {
            JLabel empty = new JLabel("No courses match your search.", SwingConstants.CENTER);
            empty.setForeground(SLATE);
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.add(empty, BorderLayout.CENTER);
            cardsContainer.add(wrapper);
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private boolean filterByCollege(Course course) {
        return activeCollege.equals("All Colleges") || course.category().equals(activeCollege);
    }

    private boolean filterByBoard(Course course) {
        return switch (activeFilter) {
            case "Board Programs" -> course.boardProgram();
            case "Non-Board" -> !course.boardProgram();
            default -> true;
        };
    }

    private boolean matchesQuery(Course course, String query) {
        if (query.isBlank()) {
            return true;
        }
        String q = query.toLowerCase();
        return course.code().toLowerCase().contains(q)
            || course.title().toLowerCase().contains(q)
            || course.description().toLowerCase().contains(q)
            || course.badge().toLowerCase().contains(q);
    }

    private void handleSelection(Course course) {
        selectedCourse = course;
        SessionManager.getInstance().setSelectedProgramCode(course.code());
        SessionManager.getInstance().setSelectedProgramName(course.title());
        renderCourseGrid();
        updateProspectus();
    }

    private void updateProspectus() {
        if (selectedCourse == null) {
            prospectusLayout.show(prospectusStack, "placeholder");
            selectButton.setEnabled(false);
            nextButton.setEnabled(false);
            return;
        }
        prospectusLayout.show(prospectusStack, "details");
        selectButton.setEnabled(true);
        nextButton.setEnabled(true);
        prospectusTitle.setText(selectedCourse.title());
        prospectusDescription.setText(selectedCourse.description());
        tuitionLabel.setText(selectedCourse.tuitionFee());
        durationLabel.setText(selectedCourse.duration());

        careerPanel.removeAll();
        for (String path : selectedCourse.careerPaths()) {
            careerPanel.add(makeTag(path));
        }
        careerPanel.revalidate();
        careerPanel.repaint();
    }

    private void confirmSelection() {
        if (selectedCourse == null) {
            return;
        }
        SessionManager.getInstance().setSelectedProgramCode(selectedCourse.code());
        SessionManager.getInstance().setSelectedProgramName(selectedCourse.title());
        navigate(Screen.BLOCK_SECTIONING);
    }

    private void navigate(Screen target) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof MobileFrame mobileFrame) {
            mobileFrame.showScreen(target, true);
        } else if (window instanceof MainFrame mainFrame) {
            mainFrame.showScreen(target);
        } else {
            Navigation.to(this, target);
        }
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 0, 0, 0));

        JLabel hint = new JLabel("Select a program to view details.");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setForeground(new Color(120, 126, 140));
        footer.add(hint, BorderLayout.WEST);

        backButton = new JButton("Back");
        backButton.putClientProperty(FlatClientProperties.STYLE,
            "arc:16; background:#ffffff; foreground:#0C5CB1;" +
                "borderColor:#0C5CB1; focusWidth:1; font:+1;");
        backButton.setBorder(new EmptyBorder(12, 32, 12, 32));
        backButton.addActionListener(e -> navigate(Screen.DOCUMENTS));

        nextButton = new JButton("Next");
        nextButton.putClientProperty(FlatClientProperties.STYLE,
            "arc:16; background:#0C5CB1; foreground:#FFFFFF; font:+1;" +
                "hoverBackground:#0f6ed8; pressedBackground:#0a4f8d; focusWidth:2; innerFocusWidth:1;" +
                "shadowColor:#0C5CB1; shadowWidth:6; shadowOpacity:25;");
        nextButton.setBorder(new EmptyBorder(12, 32, 12, 32));
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> confirmSelection());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttons.setOpaque(false);
        buttons.add(backButton);
        buttons.add(nextButton);

        footer.add(buttons, BorderLayout.EAST);
        return footer;
    }

    private DocumentListener createSearchListener() {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                renderCourseGrid();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                renderCourseGrid();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                renderCourseGrid();
            }
        };
    }

    private void restoreSessionSelection() {
        String storedCode = SessionManager.getInstance().getSelectedProgramCode();
        if (storedCode != null) {
            selectedCourse = COURSE_LOOKUP.get(storedCode);
        }
    }

    @Override
    public void onEnter(NavigationContext context) {
        restoreSessionSelection();
        renderCourseGrid();
        updateProspectus();
    }

    @Override
    public void onLeave() {
        // No-op for now.
    }

    private record Course(
        String category,
        String code,
        String title,
        String description,
        String tuitionFee,
        String duration,
        String badge,
        List<String> careerPaths,
        Color accent,
        boolean boardProgram
    ) {
    }
}