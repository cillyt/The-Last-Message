package backend;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class SaveManager {
    private static final String SAVE_FILE = "savegame.json";

    public static void saveGame() {
        try {
            JSONObject json = new JSONObject();
            json.put("maxLevelReached", GameProgress.maxLevelReached);

            Player p = Player.getInstance();
            if (p != null) {
                json.put("arUnlocked", p.getWeaponUnlocked()[1]);
                json.put("arAmmo", p.getWeapons()[1].getAmmunitionNumber());
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
                writer.write(json.toString(4));
            }
        } catch (Exception e) {
            System.err.println("Помилка при збереженні гри: " + e.getMessage());
        }
    }

    public static void loadGame() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            resetProgress();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            JSONObject json = new JSONObject(content.toString());

            GameProgress.maxLevelReached = json.optInt("maxLevelReached", 1);

            Player p = Player.getInstance();
            if (p != null) {
                p.getWeaponUnlocked()[1] = json.optBoolean("arUnlocked", false);
                int savedArAmmo = json.optInt("arAmmo", 20); // Даємо 20 патронів за замовчуванням
                p.getWeapons()[1].setAmmunitionNumber(savedArAmmo);
            }
        } catch (Exception e) {
            System.err.println("Помилка при завантаженні гри: " + e.getMessage());
            resetProgress();
        }
    }

    public static void resetProgress() {
        GameProgress.maxLevelReached = 1;
        GameProgress.introCutscenePlayed = false;

        Player.getInstance().fullReset(); // Викликаємо повне скидання гравця

        File file = new File(SAVE_FILE);
        if (file.exists()) {
            file.delete();
        }
        // Після скидання, зберігаємо чистий стан
        saveGame();
    }
}
