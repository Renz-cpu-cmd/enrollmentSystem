package ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import ui.Screen;
import ui.theme.Theme;
import util.Navigation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Gradient hero header with step tracker reused across enrollment wizard screens.
 */
public class WizardHeader extends JPanel {

    private static final String TITLE = "New Student Enrollment";
    private static final String SUBTITLE = "Academic Year 2025-2026";
    private static final String WATERMARK = "UNE";
    private static final String[] STEPS = {"Bio-Data", "Documents", "Program", "Schedule"};

    private int activeStep;
    private final JPanel stepperWrapper;

    public WizardHeader(int activeStep) {
        this.activeStep = normalizeStep(activeStep);
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel textRow = new JPanel(new BorderLayout());
        textRow.setOpaque(false);

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(TITLE);
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font:+6; font:bold; foreground:#FFFFFF;");
        JLabel subtitleLabel = new JLabel(SUBTITLE);
        // Use hex ARGB; avoid decimal alpha values.
        subtitleLabel.putClientProperty(FlatClientProperties.STYLE, "font:+1; foreground:#D9FFFFFF;");
        textBlock.add(titleLabel);
        textBlock.add(Box.createVerticalStrut(4));
        textBlock.add(subtitleLabel);

        JLabel watermarkLabel = new JLabel(WATERMARK);
        // Use hex ARGB; avoid decimal alpha values.
        watermarkLabel.putClientProperty(FlatClientProperties.STYLE,
            "font:+12; font:bold; foreground:#40FFFFFF;");

        textRow.add(textBlock, BorderLayout.WEST);
        textRow.add(watermarkLabel, BorderLayout.EAST);

        stepperWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        stepperWrapper.setOpaque(false);
        stepperWrapper.setBorder(new EmptyBorder(24, 0, 0, 0));

        add(textRow, BorderLayout.CENTER);
        add(stepperWrapper, BorderLayout.SOUTH);
        updateStepper();
    }

    public WizardHeader(Screen activeScreen) {
        this(stepFromScreen(activeScreen));
    }

    public void setActiveStep(int activeStep) {
        int normalized = normalizeStep(activeStep);
        if (this.activeStep != normalized) {
            this.activeStep = normalized;
            updateStepper();
        }
    }

    private void updateStepper() {
        stepperWrapper.removeAll();
        stepperWrapper.add(buildStepper());
        stepperWrapper.revalidate();
        stepperWrapper.repaint();
    }

    private JPanel buildStepper() {
        JPanel stepper = new JPanel();
        stepper.setOpaque(false);
        stepper.setLayout(new BoxLayout(stepper, BoxLayout.X_AXIS));

        for (int i = 0; i < STEPS.length; i++) {
            int stepNumber = i + 1;
            stepper.add(createStepPill(stepNumber, STEPS[i], stepNumber == activeStep));
            if (i < STEPS.length - 1) {
                stepper.add(createStepSeparator());
            }
        }
        return stepper;
    }

    private Component createStepSeparator() {
        JLabel separator = new JLabel("->");
        separator.setBorder(new EmptyBorder(0, 12, 0, 12));
        separator.setForeground(new Color(210, 224, 247));
        separator.setFont(Theme.BODY_FONT);
        return separator;
    }

    private JPanel createStepPill(int stepNumber, String label, boolean active) {
        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        container.setOpaque(false);

        JLabel index = new JLabel(String.format("%02d", stepNumber));
        index.putClientProperty(FlatClientProperties.STYLE,
            active
                ? "font:+2; font:bold; foreground:#FFFFFF;"
                : "font:+1; foreground:#B7C6E6;");

        JLabel text = new JLabel(label);
        text.putClientProperty(FlatClientProperties.STYLE,
            active
                ? "font:+2; font:bold; foreground:#FFFFFF;"
                : "font:+1; foreground:#CFDAF4;");

        Screen target = screenForStep(stepNumber);
        if (target != null) {
            makeInteractive(container, target);
            makeInteractive(index, target);
            makeInteractive(text, target);
        }

        container.add(index);
        container.add(text);
        return container;
    }

    private void makeInteractive(Component component, Screen target) {
        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Navigation.to(target);
            }
        });
    }

    private Screen screenForStep(int stepNumber) {
        return switch (stepNumber) {
            case 1 -> Screen.BIO_DATA;
            case 2 -> Screen.DOCUMENTS;
            case 3 -> Screen.PROGRAM_SELECTION;
            case 4 -> Screen.BLOCK_SECTIONING;
            default -> null;
        };
    }

    private static int stepFromScreen(Screen screen) {
        if (screen == null) {
            return 1;
        }
        return switch (screen) {
            case BIO_DATA -> 1;
            case DOCUMENTS -> 2;
            case PROGRAM_SELECTION -> 3;
            case BLOCK_SECTIONING -> 4;
            default -> 1;
        };
    }

    private int normalizeStep(int step) {
        if (step < 1) {
            return 1;
        }
        if (step > STEPS.length) {
            return STEPS.length;
        }
        return step;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint paint = new GradientPaint(
            0, 0, new Color(12, 92, 177),
            getWidth(), getHeight(), new Color(5, 32, 84)
        );
        g2.setPaint(paint);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
