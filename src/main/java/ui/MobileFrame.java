package ui;

import context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.screens.BioDataScreen;
import ui.screens.StudentLoginScreen;
import ui.theme.FloatingActionButton;
import ui.theme.IconCreator;
import ui.util.Animator;

import javax.swing.*;
import java.awt.*;

public class MobileFrame extends JFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(MobileFrame.class);
    private final JLayeredPane layeredPane;
    private final FloatingActionButton aiButton;
    private JComponent currentScreen;

    public MobileFrame() {
        setTitle("Enrollment System");
        setSize(400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        layeredPane = new JLayeredPane();
        layeredPane.setBackground(Color.BLACK); // To see gaps if any

        // Create and add the Floating Action Button for the AI Assistant
        aiButton = new FloatingActionButton(IconCreator.AI_ASSISTANT_ICON);
        aiButton.setBounds(320, 600, 56, 56); // Position in the bottom-right
        layeredPane.add(aiButton, JLayeredPane.PALETTE_LAYER);
        aiButton.addActionListener(e -> showScreen(Screen.AI_ASSISTANT, true));

        setContentPane(layeredPane);

        // Show the initial splash screen without animation
        showScreen(Screen.SPLASH, false);
    }

    public void showScreen(Screen screen, boolean animate) {
        JComponent newScreen = createScreen(screen);
        if (newScreen == null) {
            LOGGER.error("Could not create screen: " + screen.getName());
            return;
        }

        newScreen.setSize(getWidth(), getHeight());

        JComponent oldScreen = this.currentScreen;
        this.currentScreen = newScreen;

        if (oldScreen == null || !animate) {
            // If there's no old screen or animation is disabled, just show the new screen
            layeredPane.add(newScreen, JLayeredPane.DEFAULT_LAYER);
            if (oldScreen != null) {
                layeredPane.remove(oldScreen);
            }
            layeredPane.repaint();
            layeredPane.revalidate();
            return;
        }

        // --- Animation Logic ---
        layeredPane.add(newScreen, JLayeredPane.DEFAULT_LAYER);

        // New screen slides in from the right
        Point newScreenStart = new Point(getWidth(), 0);
        Point newScreenEnd = new Point(0, 0);
        newScreen.setLocation(newScreenStart);
        Animator.slide(newScreen, newScreenStart, newScreenEnd, 300, null);

        // Old screen slides out to the left
        Point oldScreenStart = new Point(0, 0);
        Point oldScreenEnd = new Point(-getWidth(), 0);
        Animator.slide(oldScreen, oldScreenStart, oldScreenEnd, 300, () -> {
            // When animation is finished, remove the old screen
            layeredPane.remove(oldScreen);
            layeredPane.repaint();
            layeredPane.revalidate();
        });
    }

    private JComponent createScreen(Screen screen) {
        try {
            if (screen == Screen.BIO_DATA) {
                return new BioDataScreen(ApplicationContext.getEnrollmentService());
            } else if (screen == Screen.STUDENT_LOGIN) {
                return new StudentLoginScreen(ApplicationContext.getLoginService());
            } else {
                return screen.getScreenClass().getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            LOGGER.error("Error instantiating screen: " + screen.getName(), e);
            return null;
        }
    }
}