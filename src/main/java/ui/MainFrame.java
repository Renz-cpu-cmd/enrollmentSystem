package ui;

import context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

import ui.NavigationContext;
import ui.Screen;
import ui.ScreenFactory;
import ui.ScreenView;

public class MainFrame extends JFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);

    private final ApplicationContext applicationContext;
    private final ScreenFactory screenFactory;
    private final JPanel contentPanel = new JPanel(new BorderLayout());
    private final Map<Screen, ScreenView> screenCache = new EnumMap<>(Screen.class);
    private ScreenView currentScreen;
    private Screen currentScreenKey;

    public MainFrame(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.screenFactory = new ScreenFactory(applicationContext);
        setTitle("Enrolly Dashboard");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BG_COLOR);

        contentPanel.setOpaque(true);
        contentPanel.setBackground(Theme.BG_COLOR);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void showScreen(Screen screen) {
        showScreen(screen, new NavigationContext());
    }

    public void showScreen(Screen screen, NavigationContext context) {
        NavigationContext safeContext = context != null ? context : new NavigationContext();

        ScreenView nextView = screenCache.get(screen);
        if (nextView == null) {
            try {
                nextView = screenFactory.create(screen);
                screenCache.put(screen, nextView);
            } catch (RuntimeException ex) {
                LOGGER.error("Could not create screen: {}", screen.getName(), ex);
                return;
            }
        }

        if (currentScreen != null) {
            try {
                currentScreen.onLeave();
            } catch (Exception ex) {
                LOGGER.warn("Error during onLeave for screen {}", currentScreenKey, ex);
            }
        }

        currentScreen = nextView;
        currentScreenKey = screen;
        contentPanel.removeAll();
        contentPanel.add(nextView.getPanel());
        contentPanel.revalidate();
        contentPanel.repaint();

        try {
            currentScreen.onEnter(safeContext);
        } catch (Exception ex) {
            LOGGER.warn("Error during onEnter for screen {}", screen, ex);
        }
    }
}
