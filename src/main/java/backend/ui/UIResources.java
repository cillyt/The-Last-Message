package backend.ui;

import javafx.scene.image.Image;
import javafx.scene.text.Font;
import java.io.File;

public class UIResources {
    private static final String FONT_PATH = "assets_old/font.ttf";
    private static final String BG_PATH = "file:assets_old/background.png";

    private static String fontFamily = "Arial";
    private static Image background = null;

    static {
        // Load custom font
        try {
            File fontFile = new File(FONT_PATH);
            if (fontFile.exists()) {
                Font loaded = Font.loadFont(fontFile.toURI().toURL().toExternalForm(), 12);
                if (loaded != null) {
                    fontFamily = loaded.getFamily();
                    System.out.println("✓ Font loaded: " + fontFamily);
                } else {
                    System.err.println("✗ Font.loadFont returned null");
                    fontFamily = "Arial";
                }
            } else {
                System.err.println("✗ Font file not found: " + fontFile.getAbsolutePath());
                fontFamily = "Arial";
            }
        } catch (Exception e) {
            System.err.println("✗ Error loading font: " + e.getMessage());
            e.printStackTrace();
            fontFamily = "Arial";
        }

        try {
            background = new Image(BG_PATH, true);
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
        try {
            String path = String.format("file:assets_old/weapon/%s.png", name);
            return new Image(path, size, size, true, true);
        } catch (Exception e) {
            return null;
        }
    }
}
