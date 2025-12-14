package ui.components;

import javax.swing.JTextField;

import ui.theme.Theme;

/**
 * Text field preconfigured with the mobile-friendly Theme rules.
 */
public class MobileTextField extends JTextField {

    public MobileTextField() {
        applyTheme();
    }

    public MobileTextField(int columns) {
        super(columns);
        applyTheme();
    }

    public MobileTextField(String text) {
        super(text);
        applyTheme();
    }

    public MobileTextField(String text, int columns) {
        super(text, columns);
        applyTheme();
    }

    private void applyTheme() {
        Theme.styleTextField(this);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        applyTheme();
    }
}
