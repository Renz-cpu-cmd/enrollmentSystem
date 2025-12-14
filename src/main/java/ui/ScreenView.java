package ui;

import javax.swing.JPanel;

/**
 * Base lifecycle contract for navigable screens.
 * Implementations are typically Swing panels.
 */
public interface ScreenView {
	/**
	 * Returns the panel that should be displayed for this screen.
	 */
	default JPanel getPanel() {
		if (this instanceof JPanel panel) {
			return panel;
		}
		throw new IllegalStateException("ScreenView implementations must provide a JPanel");
	}

	/**
	 * Called when the screen becomes active, providing optional navigation data.
	 */
	void onEnter(NavigationContext context);

	/**
	 * Called right before navigating away from the screen for cleanup.
	 */
	void onLeave();
}
