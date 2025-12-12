package ui.theme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Base64;

/**
 * A utility class to create and manage ImageIcons from Base64 strings.
 * This encapsulates the icon creation logic, making it easy to manage and reuse icons throughout the application.
 */
public class IconCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(IconCreator.class);

    // Base64 Encoded Strings for SVG Icons (white fill)
    private static final String DASHBOARD_ICON_STRING = "PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIGhlaWdodD0iMjRweCIgdmll" +
            "d0JveD0iMCAwIDI0IDI0IiB3aWR0aD0iMjRweCIgZmlsbD0iI0ZGRkZGRiI+PHBhdGggZD0iTTAg" +
            "MGgyNHYyNEgwVjB6IiBmaWxsPSJub25lIi8+PHBhdGggZD0iTTE5IDNINWMtMS4xMSAwLTIgLjkt" +
            "MiAydjE0YzAgMS4xLjg5IDIgMiAyaDE0YzEuMSAwIDItLjkgMi0yVjVjMC0xLjEtLjktMi0yLTJ6" +
            "bTAgMTZINVY1aDE0djE0ek03IDEwaDJ2N0g3em00LTNoMnYxMGgtMnptNCA2aDJ2NGgtMnoiLz48" +
            "L3N2Zz4=";
    private static final String COURSES_ICON_STRING = "PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIGhlaWdodD0iMjQiIHZpZXdC" +
            "b3g9IjAgMCAyNCAyNCIgd2lkdGg9IjI0IiBmaWxsPSIjRkZGRkZGIj48cGF0aCBkPSJNMTggMkg2" +
            "Yy0xLjEgMC0yIC45LTIgMnYxNmMwIDEuMS45IDIgMiAyaDEyYzEuMSAwIDItLjkgMi0yVjRjMC0x" +
            "LjEtLjktMi0yLTJ6bTAgMThINlY0aDEydjE2ek05IDdoNnYySDlWN3ptMCA0aDZ2Mkg5di0yem0w" +
            "IDRoNnYySDl2LTJ6Ii8+PC9zdmc+";
    private static final String AI_ASSISTANT_ICON_STRING = "PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIGhlaWdodD0iMjQiIHZpZXdC" +
            "b3g9IjAgMCAyNCAyNCIgd2lkdGg9IjI0IiBmaWxsPSIjRkZGRkZGIj48cGF0aCBkPSJNMTkgOWgt" +
            "NFYzSDl2Nkg1bDcgNyA3LTd6TTUgMTh2MmgxNHYtMkg1eiIvPjwvc3ZnPg==";
    private static final String PERSON_ADD_ICON_STRING = "PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIGhlaWdodD0iMjRweCIgdmlld0JveD0iMCAwIDI0IDI0IiB3aWR0aD0iMjRweCIgZmlsbD0iIzAwMDAwMCI+PHBhdGggZD0iTTAgMGgyNHYyNEgwVjB6IiBmaWxsPSJub25lIi8+PHBhdGggZD0iTTE1IDEyYyAyLjIxIDAgNC0xLjc5IDQtNHMtMS43OS00LTQtNC00IDEuNzktNCA0IDEuNzkgNCA0IDR6bS05LTJWN0g0djNIMXYyaDN2M2gydi0zaDN2LTJINnptOSA0Yy0yLjY3IDAtOCA1LjMzLTggNHYySDE2di0yYzAtMi42Ni01LjMzLTQtOC00eiIvPjwvc3ZnPg==";
    private static final String MOVE_RIGHT_ICON_STRING = "PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgd2lkdGg9IjI0Ij4KICAgIDxwYXRoIGQ9Ik0xMCAxN2w1LTUtNS01djEweiIvPgo8L3N2Zz4=";
    private static final String LOGIN_ICON_STRING = "PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIGhlaWdodD0iMjRweCIgdmlld0JveD0iMCAwIDI0IDI0IiB3aWR0aD0iMjRweCIgZmlsbD0iIzAwMDAwMCI+PHBhdGggZD0iTTAgMGgyNHYyNEgwVjB6IiBmaWxsPSJub25lIi8+PHBhdGggZD0iTTExIDdMOS42IDguNGwyLjYgMi42SDJ2MmwxMC4yLTIuNmwtMi42IDIuNkwxMSAxN2w1LTUtNS01em05IDEyaC04djJoOGMxLjEgMCAyLS45IDItMlY1YzAtMS4xLS45LTItMi0ySDEydjJoOHYxNHoiLz48L3N2Zz4=";
    
    // Publicly accessible ImageIcon instances
    public static final ImageIcon DASHBOARD_ICON = createImageIcon(DASHBOARD_ICON_STRING);
    public static final ImageIcon COURSES_ICON = createImageIcon(COURSES_ICON_STRING);
    public static final ImageIcon AI_ASSISTANT_ICON = createImageIcon(AI_ASSISTANT_ICON_STRING);
    public static final ImageIcon PERSON_ADD_ICON = createImageIcon(PERSON_ADD_ICON_STRING);
    public static final ImageIcon MOVE_RIGHT_ICON = createImageIcon(MOVE_RIGHT_ICON_STRING);
    public static final ImageIcon LOGIN_ICON = createImageIcon(LOGIN_ICON_STRING);

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
            LOGGER.error("Failed to create ImageIcon from Base64 string", e);
            return null;
        }
    }
}
