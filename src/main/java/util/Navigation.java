package util;

import ui.MainFrame;
import ui.Screen;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Window;

/**
 * Lightweight helper so screens can trigger navigation without direct frame casts.
 */
public final class Navigation {

    private Navigation() {
    }

    public static void to(JComponent sourceComponent, Screen targetScreen) {
        if (sourceComponent == null || targetScreen == null) {
            System.err.println("Navigation error: missing component or target screen");
            return;
        }
        Window window = SwingUtilities.getWindowAncestor(sourceComponent);
        if (window instanceof MainFrame mainFrame) {
            mainFrame.showScreen(targetScreen);
        } else {
            System.err.println("Navigation error: Parent is not MainFrame");
        }
    }
}
