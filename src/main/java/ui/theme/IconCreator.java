
package ui.theme;

import javax.swing.*;
import java.awt.*;
import java.util.Base64;

/**
 * A utility class to create and manage ImageIcons from Base64 strings.
 * This encapsulates the icon creation logic, making it easy to manage and reuse icons throughout the application.
 */
public class IconCreator {

    // Base64 Encoded Strings for PNG Icons
    private static final String DASHBOARD_ICON_STRING = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAACgdz34AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAAsQAAALEBJsbbMAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAADdSURBVEiJ7ZSxCYAwEEW/N28hNlY2fgzS2goG3sB4A621jZ29iT2Ab2AwYgM2dgH3gB54AAt8gI+gB2xAHjADlsAFuAAnuF/wD/LzZt69dy6UUn8MyCo1gOISGAkUomwCo2QEx9AImyB5YgS8oO/Zk3oTQVgGFf0g/4EVcPoJoK8Y4JGIWgE/YqI/+AifYqIBeCqifwAbaN4fAcUIfAXs0UIJdFfhE0AlREMR3wFNtFJCFD1xAgI/oH4DXmAA3AJ2wA24A2bAAHga3Q5c/8E/ADd+S3gFW3gGAAAAAElFTkSuQmCC";
    private static final String COURSES_ICON_STRING = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAACgdz34AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAAsQAAALEBJsbbMAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAADpSURBVEiJ7ZQxCsJAEEX/dhe7VYsLttrYWFp7Kx/CKwsfwwmWsLWGgi2VBYXcB0jwBfICFjgCG/AGXMAX8AYW+AI+gB+QA/ZAi8VKSY0+CH8I/pD87ph5954Q0j9G8hIYgRNwAjbAByxAIWxAGjRAChxBrz9/eAPy+gdI0B9E/oCvP2GgBgzQBWfgCRwF/wFfwDk4AefgDfwH/AEX4AFs0AI2wAV4A5fAIWwA9ugAOmgBF+ADfAJvwAnYgAewABagD/gEH4Bf8AsYgR2wgw3wBgZAHfiFfgM2gA9wA94AD3zAr58/fAEt/4XgFXgYiwAAAABJRU5ErkJggg==";
    private static final String AI_ASSISTANT_ICON_STRING = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAACgdz34AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAAsQAAALEBJsbbMAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAEeSURBVEiJ7ZQ/S8NAGMW/e4lEEAsFRMGuoOAgKI4O4uAiuHgSxB/g5OAncHIRtDo7+DkUHBwEHSpYBH+Biy4KguBsb/fYA3qSll5x+HDvuffl3XvfDSH0j/E8AAbABvgAb+AFfMAOfAFf4Bf8AnvQDnfgBtywAnbAFbABN+AO3IAPcAMOQK+SKAUh+wbwA3pADSgBNaB6qYlqYArgAXYAD+BNo9EGPAAbYDcQrwWWYDdIbwM+gB0xaggk8A1s0b4bAi1gi1l02wBfpCaKzRzYBHfAAbgC2F8CnsAmuB4sAA/gI6QADeAB3IAV2IAH2IAX2IDX4C2sAAvQBjvgBEyAM/AFbAVP4Cq8gjM4A2fgPHyAN7AFduANbAF7oBN0g/5gGjQAbcAG+AR+A//CH/gL/gI9wBc4A4fgAx6AG/AB/sB78AnWwev1AS4KhzU5n0YtAAAAAElFTkSuQmCC";

    // Publicly accessible ImageIcon instances
    public static final ImageIcon DASHBOARD_ICON = createImageIcon(DASHBOARD_ICON_STRING);
    public static final ImageIcon COURSES_ICON = createImageIcon(COURSES_ICON_STRING);
    public static final ImageIcon AI_ASSISTANT_ICON = createImageIcon(AI_ASSISTANT_ICON_STRING);

    /**
     * Creates an ImageIcon from a Base64 encoded string.
     *
     * @param base64String The Base64 string representing the image.
     * @return An ImageIcon object, or null if decoding fails.
     */
    private static ImageIcon createImageIcon(String base64String) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64String);
            return new ImageIcon(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
