package ui;

import context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.theme.FloatingActionButton;
import ui.theme.IconCreator;
import ui.theme.Theme;
import util.GeminiClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.EnumMap;
import java.util.Map;

public class MobileFrame extends JFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(MobileFrame.class);
    private final JLayeredPane layeredPane;
    private final FloatingActionButton aiButton;
    private final ApplicationContext applicationContext;
    private final ScreenFactory screenFactory;
    private final CardLayout cardLayout;
    private final JPanel screenContainer;
    private final Map<Screen, ScreenView> screenCache = new EnumMap<>(Screen.class);
    private ScreenView currentScreen;
    private Screen currentScreenKey;

    public MobileFrame(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.screenFactory = new ScreenFactory(applicationContext);
        setTitle("Enrollment System");
        setSize(Theme.MOBILE_WIDTH, Theme.MOBILE_HEIGHT);
        setPreferredSize(new Dimension(Theme.MOBILE_WIDTH, Theme.MOBILE_HEIGHT));
        setMinimumSize(new Dimension(Theme.MOBILE_WIDTH, Theme.MOBILE_HEIGHT));
        setMaximumSize(new Dimension(Theme.MOBILE_WIDTH, Theme.MOBILE_HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        layeredPane = new JLayeredPane();
        layeredPane.setBackground(Color.BLACK); // To see gaps if any

        cardLayout = new CardLayout();
        screenContainer = new JPanel(cardLayout);
        screenContainer.setOpaque(true);
        screenContainer.setBackground(Color.BLACK);
        screenContainer.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(screenContainer, JLayeredPane.DEFAULT_LAYER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                screenContainer.setBounds(0, 0, getWidth(), getHeight());
                positionFloatingActionButton();
            }
        });

        // Create and add the Floating Action Button for the AI Assistant
        aiButton = new FloatingActionButton(IconCreator.AI_ASSISTANT_ICON);
        layeredPane.add(aiButton, JLayeredPane.PALETTE_LAYER);
        if (GeminiClient.isAvailable()) {
            aiButton.addActionListener(e -> showScreen(Screen.AI_ASSISTANT, true));
        } else {
            aiButton.setVisible(false);
        }

        setContentPane(layeredPane);
        positionFloatingActionButton();

        // Initial screen selection handled by the entry point while testing specific flows.
        // showScreen(Screen.SPLASH, false);
    }

    private void positionFloatingActionButton() {
        if (aiButton == null) {
            return;
        }
        int margin = 16;
        int size = 56;
        int x = Math.max(margin, getWidth() - size - margin);
        int y = Math.max(margin, getHeight() - size - 48);
        aiButton.setBounds(x, y, size, size);
    }

    public void showScreen(Screen screen, boolean animate) {
        showScreen(screen);
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
                JPanel panel = nextView.getPanel();
                screenContainer.add(panel, screen.name());
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
        cardLayout.show(screenContainer, screen.name());

        try {
            currentScreen.onEnter(safeContext);
        } catch (Exception ex) {
            LOGGER.warn("Error during onEnter for screen {}", screen, ex);
        }
    }
}