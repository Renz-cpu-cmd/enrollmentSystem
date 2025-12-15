package ui.screens;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.components.FlatTextField;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import ui.MainFrame;
import ui.MobileFrame;
import ui.NavigationContext;
import ui.Screen;
import ui.ScreenView;
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
import java.util.Objects;

public class ProgramSelectionScreen extends JPanel implements ScreenView {

    private static final List<String> COLLEGES = List.of(
        "All Colleges",
        "College of Computing",
        "College of Nursing",
        "College of Business",
        "College of Education"
    );

    private static final List<Course> COURSES = List.of(
        new Course("College of Computing", "BSIT", "Bachelor of Science in Information Technology",
            "Studio-driven build sprints, UX immersion, and DevOps readiness for future product engineers.",
            "₱25,000 / sem", "4 Years", "Tech", new Color(12, 92, 177)),
        new Course("College of Computing", "BSCS", "Bachelor of Science in Computer Science",
            "Machine learning research, data science pipelines, and algorithm mastery with capstone labs.",
            "₱26,500 / sem", "4 Years", "Research", new Color(77, 107, 255)),
        new Course("College of Nursing", "BSN", "Bachelor of Science in Nursing",
            "CHED-compliant clinical immersion with simulation labs and international board review prep.",
            "₱32,000 / sem", "4 Years", "Board Exam", new Color(0, 150, 136)),
        new Course("College of Business", "BSACCY", "Bachelor of Science in Accountancy",
            "CPA-track curriculum covering audit analytics, taxation law, and digital ERP practice.",
            "₱28,500 / sem", "4 Years", "CPA Ready", new Color(217, 119, 6)),
        new Course("College of Business", "BSBA", "Bachelor of Science in Business Administration",
            "Strategy, finance, and growth marketing studios run with real startup case partners.",
            "₱24,000 / sem", "4 Years", "Enterprise", new Color(244, 114, 182)),
        new Course("College of Education", "BSEd Psy", "Bachelor of Secondary Education major in Psychology",
            "Learning sciences, cognitive development, and practicum placements in partner schools.",
            "₱22,500 / sem", "4 Years", "Humanities", new Color(99, 102, 241))
    );

    private static final Map<String, Course> COURSE_LOOKUP = new LinkedHashMap<>();

    static {
        COURSES.forEach(course -> COURSE_LOOKUP.put(course.code(), course));
    }

    private final FlatTextField searchField;
    private final JPanel cardsContainer;
    private final ButtonGroup categoryGroup = new ButtonGroup();
    private final CardLayout inspectorCardLayout = new CardLayout();
    private final JPanel inspectorStack;
    private final JLabel inspectorTitle;
    private final JTextArea inspectorDescription;
    private final JLabel inspectorTuition;
    private final JLabel inspectorDuration;
    private final JButton confirmButton;

    private String activeCollege = COLLEGES.get(0);
    private Course selectedCourse;

    public ProgramSelectionScreen() {
        setLayout(new BorderLayout(16, 16));
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        searchField = new FlatTextField();
        searchField.setPlaceholderText("Search for courses...");
        searchField.setTrailingIcon(new FlatSearchIcon());
        searchField.setColumns(24);
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search for courses...");
        searchField.getDocument().addDocumentListener(createSearchListener());

        cardsContainer = new JPanel(new GridLayout(0, 2, 18, 18));
        cardsContainer.setOpaque(false);

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildCategorySidebar(), BorderLayout.WEST);
        add(buildGrid(), BorderLayout.CENTER);
        inspectorStack = new JPanel(inspectorCardLayout);
        inspectorStack.setOpaque(false);
        inspectorTitle = new JLabel();
        inspectorDescription = new JTextArea();
        inspectorTuition = new JLabel();
        inspectorDuration = new JLabel();
        confirmButton = createConfirmButton();
        add(buildInspectorPanel(), BorderLayout.EAST);

        restoreSessionSelection();
        renderCourseGrid();
        updateInspectorPanel();
    }

    private JComponent buildToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(16, 0));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(new CompoundBorder(new FlatDropShadowBorder(), new EmptyBorder(16, 24, 16, 24)));

        JLabel title = new JLabel("Select Your Path");
        title.setFont(Theme.HEADING_FONT.deriveFont(Font.BOLD, 26f));
        title.setForeground(new Color(26, 32, 44));

        toolbar.add(title, BorderLayout.WEST);
        toolbar.add(searchField, BorderLayout.EAST);
        return toolbar;
    }

    private JComponent buildCategorySidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(247, 248, 252));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new CompoundBorder(
            new FlatRoundBorder(),
            new EmptyBorder(24, 16, 24, 16)
        ));

        JLabel label = new JLabel("Colleges");
        label.setFont(Theme.SUBHEADER_FONT);
        label.setForeground(new Color(31, 41, 55));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(label);
        sidebar.add(Box.createVerticalStrut(16));

        for (String college : COLLEGES) {
            sidebar.add(createCategoryButton(college));
            sidebar.add(Box.createVerticalStrut(10));
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JComponent buildGrid() {
        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(18);
        return scrollPane;
    }

    private JComponent buildInspectorPanel() {
        JPanel inspector = new JPanel(new BorderLayout());
        inspector.setPreferredSize(new Dimension(300, 0));
        inspector.setBackground(Color.WHITE);
        inspector.setBorder(new CompoundBorder(new FlatDropShadowBorder(), new EmptyBorder(24, 24, 24, 24)));

        JPanel placeholder = new JPanel(new BorderLayout());
        placeholder.setOpaque(false);
        JLabel placeholderLabel = new JLabel("<html><div style='text-align:center;'>Select a course to view details.</div></html>");
        placeholderLabel.setForeground(new Color(107, 114, 128));
        placeholderLabel.setFont(Theme.BODY_FONT);
        placeholder.add(placeholderLabel, BorderLayout.CENTER);

        JPanel detailPanel = new JPanel();
        detailPanel.setOpaque(false);
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));

        inspectorTitle.setFont(Theme.SUBHEADER_FONT.deriveFont(Font.BOLD, 20f));
        inspectorTitle.setForeground(new Color(15, 23, 42));
        inspectorTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        inspectorDescription.setWrapStyleWord(true);
        inspectorDescription.setLineWrap(true);
        inspectorDescription.setEditable(false);
        inspectorDescription.setOpaque(false);
        inspectorDescription.setFont(Theme.BODY_FONT);
        inspectorDescription.setForeground(new Color(71, 85, 105));
        inspectorDescription.setAlignmentX(Component.LEFT_ALIGNMENT);

        inspectorTuition.setFont(Theme.BOLD_BODY_FONT);
        inspectorTuition.setForeground(new Color(12, 92, 177));
        inspectorTuition.setAlignmentX(Component.LEFT_ALIGNMENT);

        inspectorDuration.setFont(Theme.BODY_FONT);
        inspectorDuration.setForeground(new Color(55, 65, 81));
        inspectorDuration.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailPanel.add(inspectorTitle);
        detailPanel.add(Box.createVerticalStrut(8));
        detailPanel.add(inspectorDescription);
        detailPanel.add(Box.createVerticalStrut(16));
        detailPanel.add(inspectorTuition);
        detailPanel.add(Box.createVerticalStrut(6));
        detailPanel.add(inspectorDuration);
        detailPanel.add(Box.createVerticalStrut(18));
        detailPanel.add(confirmButton);

        inspectorStack.add(placeholder, "placeholder");
        inspectorStack.add(detailPanel, "details");
        inspector.add(inspectorStack, BorderLayout.CENTER);
        return inspector;
    }

    private JButton createConfirmButton() {
        JButton button = new JButton("Confirm Selection");
        button.setEnabled(false);
        button.putClientProperty(FlatClientProperties.STYLE,
            "arc:18; background:#0C5CB1; foreground:#FFFFFF; font:+1;" +
                "hoverBackground:#0f6ed8; pressedBackground:#0a4f8d; focusWidth:2; innerFocusWidth:1;");
        button.setBorder(new EmptyBorder(12, 18, 12, 18));
        button.addActionListener(e -> confirmSelection());
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private JToggleButton createCategoryButton(String label) {
        JToggleButton button = new JToggleButton(label);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.putClientProperty(FlatClientProperties.STYLE,
            "arc:16; margin:4,12,4,12; background:#FFFFFF; foreground:#1F2937;" +
                "selectedBackground:#0C5CB1; selectedForeground:#FFFFFF; borderWidth:0; font:+0;");
        button.addActionListener(e -> {
            activeCollege = label;
            renderCourseGrid();
        });
        if (label.equals(activeCollege)) {
            button.setSelected(true);
        }
        categoryGroup.add(button);
        return button;
    }

    private void renderCourseGrid() {
        cardsContainer.removeAll();
        String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        COURSES.stream()
            .filter(course -> activeCollege.equals("All Colleges") || course.category().equals(activeCollege))
            .filter(course -> query.isBlank() || matchesQuery(course, query))
            .forEach(course -> cardsContainer.add(createCourseCard(course.code(), course.title(), course.category())));

        if (cardsContainer.getComponentCount() == 0) {
            JLabel empty = new JLabel("No courses match your search.", SwingConstants.CENTER);
            empty.setForeground(new Color(120, 126, 140));
            empty.setFont(Theme.BODY_FONT);
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.add(empty, BorderLayout.CENTER);
            cardsContainer.add(wrapper);
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private boolean matchesQuery(Course course, String query) {
        return course.code().toLowerCase().contains(query)
            || course.title().toLowerCase().contains(query)
            || course.description().toLowerCase().contains(query)
            || course.badge().toLowerCase().contains(query);
    }

    private JComponent createCourseCard(String code, String title, String category) {
        Course course = Objects.requireNonNull(COURSE_LOOKUP.get(code), "Course not found: " + code);
        boolean isSelected = selectedCourse != null && selectedCourse.code().equals(course.code());

        JPanel shell = new JPanel(new BorderLayout());
        shell.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(buildCardBorder(isSelected));
        card.setPreferredSize(new Dimension(260, 190));

        JLabel avatar = new JLabel(course.code().substring(0, 1), SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(course.accent());
        avatar.setForeground(Color.WHITE);
        avatar.setFont(Theme.SUBHEADER_FONT);
        avatar.setPreferredSize(new Dimension(54, 54));
        avatar.putClientProperty(FlatClientProperties.STYLE, "arc:999");

        JLabel codeLabel = new JLabel(course.code());
        codeLabel.setFont(Theme.BOLD_BODY_FONT);
        codeLabel.setForeground(new Color(15, 23, 42));
        JLabel titleLabel = new JLabel(course.title());
        titleLabel.setFont(Theme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        titleLabel.setForeground(new Color(55, 65, 81));

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        header.add(avatar, BorderLayout.WEST);

        JPanel titleStack = new JPanel();
        titleStack.setOpaque(false);
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.add(codeLabel);
        titleStack.add(titleLabel);
        header.add(titleStack, BorderLayout.CENTER);

        JTextArea description = new JTextArea(course.description());
        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        description.setEditable(false);
        description.setOpaque(false);
        description.setFont(Theme.BODY_FONT.deriveFont(13f));
        description.setForeground(new Color(100, 116, 139));

        JLabel badge = new JLabel(course.badge());
        badge.setOpaque(true);
        badge.setBackground(new Color(244, 247, 254));
        badge.setForeground(course.accent().darker());
        badge.setFont(Theme.LABEL_FONT);
        badge.setBorder(new EmptyBorder(4, 12, 4, 12));
        badge.putClientProperty(FlatClientProperties.STYLE, "arc:999");

        card.add(header, BorderLayout.NORTH);
        card.add(description, BorderLayout.CENTER);
        card.add(badge, BorderLayout.SOUTH);

        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleCourseSelection(course);
            }
        };
        installCardListener(card, adapter);

        shell.add(card, BorderLayout.CENTER);
        return shell;
    }

    private Border buildCardBorder(boolean isSelected) {
        Border inner = isSelected
            ? new CompoundBorder(new LineBorder(new Color(12, 92, 177), 2, true), new EmptyBorder(14, 14, 14, 14))
            : new CompoundBorder(new FlatRoundBorder(), new EmptyBorder(18, 18, 18, 18));
        return new CompoundBorder(new FlatDropShadowBorder(), inner);
    }

    private void installCardListener(Component component, MouseAdapter adapter) {
        component.addMouseListener(adapter);
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                installCardListener(child, adapter);
            }
        }
    }

    private void handleCourseSelection(Course course) {
        selectedCourse = course;
        SessionManager.getInstance().setSelectedProgramCode(course.code());
        SessionManager.getInstance().setSelectedProgramName(course.title());
        renderCourseGrid();
        updateInspectorPanel();
    }

    private void confirmSelection() {
        if (selectedCourse == null) {
            return;
        }
        SessionManager.getInstance().setSelectedProgramCode(selectedCourse.code());
        SessionManager.getInstance().setSelectedProgramName(selectedCourse.title());

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof MobileFrame mobileFrame) {
            mobileFrame.showScreen(Screen.BLOCK_SECTIONING);
        } else if (window instanceof MainFrame mainFrame) {
            mainFrame.showScreen(Screen.BLOCK_SECTIONING);
        } else {
            Navigation.to(this, Screen.BLOCK_SECTIONING);
        }
    }

    private void updateInspectorPanel() {
        if (selectedCourse == null) {
            inspectorCardLayout.show(inspectorStack, "placeholder");
            confirmButton.setEnabled(false);
            return;
        }
        inspectorTitle.setText(selectedCourse.title());
        inspectorDescription.setText(selectedCourse.description());
        inspectorTuition.setText("Estimated Tuition: " + selectedCourse.tuition());
        inspectorDuration.setText("Duration: " + selectedCourse.duration());
        confirmButton.setEnabled(true);
        inspectorCardLayout.show(inspectorStack, "details");
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
        updateInspectorPanel();
    }

    @Override
    public void onLeave() {
        // No-op for now.
    }

    private record Course(String category, String code, String title, String description,
                          String tuition, String duration, String badge, Color accent) {
    }
}