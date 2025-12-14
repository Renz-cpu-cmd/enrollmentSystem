package ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central navigation coordinator responsible for lazy-loading screens,
 * managing back-stack semantics, and broadcasting navigation events to
 * interested listeners (e.g., {@link MobileFrame}).
 */
public final class NavigationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavigationController.class);

    private final ScreenFactory screenFactory;
    private final Map<Screen, ScreenView> screenCache = new EnumMap<>(Screen.class);
    private final Deque<Screen> backStack = new ArrayDeque<>();
    private final List<NavigationListener> listeners = new CopyOnWriteArrayList<>();

    private Screen currentScreen;
    private ScreenView currentView;

    public NavigationController(ScreenFactory screenFactory) {
        this.screenFactory = Objects.requireNonNull(screenFactory, "ScreenFactory is required");
    }

    public void addNavigationListener(NavigationListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeNavigationListener(NavigationListener listener) {
        listeners.remove(listener);
    }

    public void navigateTo(Screen target) {
        navigateTo(target, new NavigationContext());
    }

    public void navigateTo(Screen target, NavigationContext context) {
        performNavigation(target, context, false);
    }

    public boolean canGoBack() {
        return !backStack.isEmpty();
    }

    public void goBack() {
        if (!canGoBack()) {
            return;
        }
        Screen previous = backStack.pop();
        performNavigation(previous, new NavigationContext(), true);
    }

    public Optional<Screen> getCurrentScreen() {
        return Optional.ofNullable(currentScreen);
    }

    public Optional<ScreenView> getCurrentView() {
        return Optional.ofNullable(currentView);
    }

    private void performNavigation(Screen target, NavigationContext context, boolean fromBackStack) {
        if (target == null) {
            LOGGER.warn("Navigation request ignored: target screen is null");
            return;
        }
        NavigationContext safeContext = context != null ? context : new NavigationContext();
        ScreenView nextView = resolveScreenView(target);
        if (nextView == null) {
            LOGGER.error("Unable to obtain screen view for {}", target.getName());
            return;
        }

        ScreenView previousView = currentView;
        if (previousView != null) {
            try {
                previousView.onLeave();
            } catch (Exception ex) {
                LOGGER.warn("Error during onLeave for screen {}", currentScreen, ex);
            }
        }

        if (!fromBackStack && currentScreen != null && currentScreen != target) {
            backStack.push(currentScreen);
        }

        currentScreen = target;
        currentView = nextView;

        NavigationEvent event = new NavigationEvent(target, nextView, safeContext, fromBackStack);
        notifyListeners(event);

        try {
            nextView.onEnter(safeContext);
        } catch (Exception ex) {
            LOGGER.warn("Error during onEnter for screen {}", target, ex);
        }
    }

    private ScreenView resolveScreenView(Screen screen) {
        return screenCache.computeIfAbsent(screen, this::createScreenSafely);
    }

    private ScreenView createScreenSafely(Screen screen) {
        try {
            return screenFactory.create(screen);
        } catch (RuntimeException ex) {
            LOGGER.error("Failed to create screen {}", screen.getName(), ex);
            return null;
        }
    }

    private void notifyListeners(NavigationEvent event) {
        for (NavigationListener listener : listeners) {
            try {
                listener.onNavigationChanged(event);
            } catch (Exception ex) {
                LOGGER.warn("Navigation listener threw an exception", ex);
            }
        }
    }

    /** Listener for navigation changes. */
    public interface NavigationListener {
        void onNavigationChanged(NavigationEvent event);
    }

    /** Immutable navigation change payload. */
    public static final class NavigationEvent {
        private final Screen screen;
        private final ScreenView screenView;
        private final NavigationContext context;
        private final boolean backNavigation;

        private NavigationEvent(Screen screen, ScreenView screenView, NavigationContext context, boolean backNavigation) {
            this.screen = screen;
            this.screenView = screenView;
            this.context = context;
            this.backNavigation = backNavigation;
        }

        public Screen getScreen() {
            return screen;
        }

        public ScreenView getScreenView() {
            return screenView;
        }

        public NavigationContext getContext() {
            return context;
        }

        public boolean isBackNavigation() {
            return backNavigation;
        }
    }
}
