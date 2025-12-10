
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class IconCreator {

    public static void main(String[] args) throws IOException {
        createIcon(DASHBOARD_ICON, "dashboard.png");
        createIcon(STUDENTS_ICON, "students.png");
        createIcon(COURSES_ICON, "courses.png");
        createIcon(ENROLLMENT_ICON, "enrollment.png");
        createIcon(AI_ASSISTANT_ICON, "ai_assistant.png");
    }

    private static void createIcon(String base64, String fileName) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64);
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
        File outputfile = new File("src/main/resources/icons/" + fileName);
        ImageIO.write(img, "png", outputfile);
    }

    private static final String DASHBOARD_ICON = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAACgdz34AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAAsQAAALEBJsbbMAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAADdSURBVEiJ7ZSxCYAwEEW/N28hNlY2fgzS2goG3sB4A621jZ29iT2Ab2AwYgM2dgH3gB54AAt8gI+gB2xAHjADlsAFuAAnuF/wD/LzZt69dy6UUn8MyCo1gOISGAkUomwCo2QEx9AImyB5YgS8oO/Zk3oTQVgGFf0g/4EVcPoJoK8Y4JGIWgE/YqI/+AifYqIBeCqifwAbaN4fAcUIfAXs0UIJdFfhE0AlREMR3wFNtFJCFD1xAgI/oH4DXmAA3AJ2wA24A2bAAHga3Q5c/8E/ADd+S3gFW3gGAAAAAElFTkSuQmCC";
    private static final String STUDENTS_ICON = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAACgdz34AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAAsQAAALEBJsbbMAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAEGSURBVEiJ7ZUxTgJBFAT/e29xgQl+AbFC4zHQG6E3QHECfgM0XsDaCG8g8QY0nmgj2NiYWItdAvfszOwMwiaITbISi17yTeZ33z13ZoZQ6g8z8AIOwAR4Ad/AnY2XgE/gDlwAJyA+F5YdUIEWeAEn4K+fP3wB+gBf/f0jA0fS2w58A5voXAJ+gIvw1M93gElNnsAcWLoBfOADOE0JvAEnwDk4BS7A1wCzzqYVMATm6H4CdoC/fgp8A0vAebrVwB1onh/hJcQn4I/fK4Ad0KADaE4BPgKkAX3wG1T0BviEfgP+hHYDOgBNv8FGAA3sAbv2J/gHTvAS3gC//4w3AAAAAElFTkSuQmCC";
    private static final String COURSES_ICON = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAACgdz34AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAAsQAAALEBJsbbMAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAADpSURBVEiJ7ZQxCsJAEEX/dhe7VYsLttrYWFp7Kx/CKwsfwwmWsLWGgi2VBYXcB0jwBfICFjgCG/AGXMAX8AYW+AI+gB+QA/ZAi8VKSY0+CH8I/pD87ph5954Q0j9G8hIYgRNwAjbAByxAIWxAGjRAChxBrz9/eAPy+gdI0B9E/oCvP2GgBgzQBWfgCRwF/wFfwDk4AefgDfwH/AEX4AFs0AI2wAV4A5fAIWwA9ugAOmgBF+ADfAJvwAnYgAewABagD/gEH4Bf8AsYgR2wgw3wBgZAHfiFfgM2gA9wA94AD3zAr58/fAEt/4XgFXgYiwAAAABJRU5ErkJggg==";
    private static final String ENROLLMENT_ICON = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAACgdz34AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAAsQAAALEBJsbbMAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAEASURBVEiJ7ZQxSgNBFEX/d3cXG1s7X8IbWFoLi+ANBC+C10ZiLWFiY7WFP8CiC9izh8zOFBYEwSRLseQl7zffvedOZggp/TGfAnfgDkwAA+AEjLAUuAAXwAU4A/GVwHYBJVgAF+A3v358AV+AP/7+EYsPpbcd+AZmwFEJ/AEX4S+/fw/oU2wAs6FpAfzADXiqBKbAGXAGTsAF2J6AOmdTCwZgAuzvAvwB/vJb4AOYAOnmVgM3oHl+hJcQn4A//5cB+oFGHaBNK+AqwApQg/+g/gO+gP8GfAD/BtQA6v4GNkCoGdgDN/Un+AdO8BLeAP//jLcAAAAASUVORK5CYII=";
    private static final String AI_ASSISTANT_ICON = "iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAACgdz34AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAAsQAAALEBJsbbMAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAADpSURBVEiJ7ZQxCsJAEEX/dhe7VYsLttrYWFp7Kx/CKwsfwwmWsLWGgi2VBYXcB0jwBfICFjgCG/AGXMAX8AYW+AI+gB+QA/ZAi8VKSY0+CH8I/pD87ph5954Q0j9G8hIYgRNwAjbAByxAIWxAGjRAChxBrz9/eAPy+gdI0B9E/oCvP2GgBgzQBWfgCRwF/wFfwDk4AefgDfwH/AEX4AFs0AI2wAV4A5fAIWwA9ugAOmgBF+ADfAJvwAnYgAewABagD/gEH4Bf8AsYgR2wgw3wBgZAHfiFfgM2gA9wA94AD3zAr58/fAEt/4XgFXgYiwAAAABJRU5ErkJggg==";
}
