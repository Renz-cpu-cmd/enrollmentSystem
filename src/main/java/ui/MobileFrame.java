package ui;

import context.ApplicationContext;
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
    private final JLayeredPane layeredPane;
    private final FloatingActionButton aiButton;
    private final CardLayout cardLayout;
    private final JPanel screenContainer;
    private final Map<Screen, JPanel> registeredPanels = new EnumMap<>(Screen.class);
    private final NavigationController navigationController;

    public MobileFrame(ApplicationContext applicationContext) {
        ScreenFactory screenFactory = new ScreenFactory(applicationContext);
        this.navigationController = new NavigationController(screenFactory);
        this.navigationController.addNavigationListener(this::handleNavigationEvent);
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
            aiButton.addActionListener(e -> navigationController.navigateTo(Screen.AI_ASSISTANT));
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
        navigationController.navigateTo(screen, context);
    }

    public NavigationController getNavigationController() {
        return navigationController;
    }

    private void handleNavigationEvent(NavigationController.NavigationEvent event) {
        Screen screen = event.getScreen();
        ScreenView view = event.getScreenView();
        JPanel panel = view.getPanel();
        if (!registeredPanels.containsKey(screen)) {
            screenContainer.add(panel, screen.name());
            registeredPanels.put(screen, panel);
        }
        cardLayout.show(screenContainer, screen.name());
    }
}