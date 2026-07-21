package backend.ui;

import javafx.scene.image.Image;
import javafx.scene.text.Font;
import java.io.InputStream;

public class UIResources {
    private static final String FONT_PATH = "/assets/font.ttf";
    private static final String BG_PATH = "/assets/background.png";

    private static String fontFamily = "Arial";
    private static Image background = null;

    static {
        // Load custom font
        try {
            InputStream is = UIResources.class.getResourceAsStream(FONT_PATH);
            if (is != null) {
                Font loaded = Font.loadFont(is, 12);

                if (loaded != null) {
                    fontFamily = loaded.getFamily();
                    System.out.println("✓ Font loaded: " + fontFamily);
                } else {
                    System.err.println("✗ Font.loadFont returned null");
                    fontFamily = "Arial";
                }
            } else {
                System.err.println("✗ Font file not found in JAR: " + FONT_PATH);
                fontFamily = "Arial";
            }
        } catch (Exception e) {
            System.err.println("✗ Error loading font: " + e.getMessage());
            e.printStackTrace();
            fontFamily = "Arial";
        }

        try {
            InputStream is = UIResources.class.getResourceAsStream(BG_PATH);
            if (is != null) {
                background = new Image(is);
            } else {
                System.err.println("error: file with path " + BG_PATH + " doesent exist in JAR");
            }
        } catch (Exception e) {
            System.err.println("✗ Error loading background: " + e.getMessage());
            background = null;
        }
    }

    public static Font getFont(double size) {
        return Font.font(fontFamily, size);
    }

    public static String getFontCSS() {
        return "-fx-font-family: '" + fontFamily + "';";
    }

    public static Image getBackground() {
        return background;
    }

    public static Image loadWeaponIcon(String name, double size) {
        String path = String.format("/assets/weapon/%s.png", name);
        try {
            InputStream is = UIResources.class.getResourceAsStream(path);
            if (is == null) {
                System.err.println("error: icon not found" + path);
                return null;
            }
            return new Image(is, size, size, true, true);
        } catch (Exception e) {
            return null;
        }
    }
}
